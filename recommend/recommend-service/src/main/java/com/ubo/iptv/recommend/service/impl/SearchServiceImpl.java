package com.ubo.iptv.recommend.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ubo.iptv.core.constant.ElasticSearchConstant;
import com.ubo.iptv.core.enums.MediaSortEnum;
import com.ubo.iptv.entity.gdgd.SceneDTO;
import com.ubo.iptv.entity.gdgd.StarESSearchDTO;
import com.ubo.iptv.entity.gdgd.TypeKindESSearchDTO;
import com.ubo.iptv.entity.gdgd.recommend.MediaKindTopDTO;
import com.ubo.iptv.entity.gdgd.recommend.MediaTypeTopDTO;
import com.ubo.iptv.entity.gdgd.recommend.RecommendEngineDTO;
import com.ubo.iptv.mybatis.recommend.entity.StrategyMediaSortDO;
import com.ubo.iptv.mybatis.recommend.entity.StrategyMediaTypeDO;
import com.ubo.iptv.recommend.entity.MediaDoc;
import com.ubo.iptv.recommend.entity.MediaSort;
import com.ubo.iptv.recommend.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHits;
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SearchServiceImpl implements SearchService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public List<MediaDoc> findMediaList(String category, String catalog, List<MediaSort> sorts, List<String> ignoreList, int size) {
        final String index = "", type = "";

        SearchRequest searchRequest = new SearchRequest();

        BoolQueryBuilder query = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(catalog)) {
            query.must().add(QueryBuilders.termQuery("catalog", catalog));
        }
        if (StringUtils.isNotBlank(category)) {
            query.must().add(QueryBuilders.termQuery("category", category));
        }

        return null;
    }

    @Override
    public List<String> exposureNoClick(LocalDate date, int number) {
        LocalDate start = date.minusDays(number);

        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("log_time").gte(start).lt(date);

        TermsAggregationBuilder userBucket = AggregationBuilders.terms("user_bucket").field("user_id");

        DateHistogramAggregationBuilder dayBucket = AggregationBuilders.dateHistogram("day_bucket").field("log_time").minDocCount(0).dateHistogramInterval(DateHistogramInterval.DAY);
        dayBucket.subAggregation(AggregationBuilders.cardinality("day_exposure").field("recommend_location_exposure"));
        dayBucket.subAggregation(AggregationBuilders.cardinality("day_click").field("recommend_location_click"));
        userBucket.subAggregation(dayBucket);
        userBucket.subAggregation(PipelineAggregatorBuilders.sumBucket("exposure", "day_bucket>day_exposure"));
        userBucket.subAggregation(PipelineAggregatorBuilders.sumBucket("click", "day_bucket>day_click"));

        Map<String, String> map = new HashMap<>();
        map.put("exposure", "exposure");
        map.put("click", "click");
        userBucket.subAggregation(PipelineAggregatorBuilders.bucketSelector("selector", map, new Script("params.click==0 && params.exposure==" + number)));

        searchSourceBuilder.query(rangeQuery);
        searchSourceBuilder.aggregation(userBucket);

        SearchRequest request = new SearchRequest();
        request.indices("iptv_log");
        request.types("doc");
        request.source(searchSourceBuilder);

        try {
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            Terms terms = response.getAggregations().get("user_bucket");
            List<String> keys = terms.getBuckets().stream().map(t -> ((Terms.Bucket) t).getKeyAsString()).collect(Collectors.toList());
            return keys;
        } catch (IOException e) {
            log.error(e.getMessage());
            log.error(request.source().toString());
        }
        return null;
    }

    @Override
    public Map<String, Long> mediaCountByEventType(int media_id, String sys_id, LocalDate date) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must().add(QueryBuilders.termQuery("media_id", media_id));
        queryBuilder.must().add(QueryBuilders.termQuery("sys_id", sys_id));
        if (date != null) {
            queryBuilder.must().add(QueryBuilders.rangeQuery("log_time").gte(date).lte(date));
        }

        TermsAggregationBuilder eventTypeBucket = AggregationBuilders.terms("event_type_bucket").field("event_type");
        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.aggregation(eventTypeBucket);

        SearchRequest request = new SearchRequest();
        request.indices(ElasticSearchConstant.log_index);
        request.types(ElasticSearchConstant.type_default);
        try {
//            log.debug(searchSourceBuilder.toString());
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            Terms terms = response.getAggregations().get("event_type_bucket");
            Map<String, Long> map = new HashMap<>();
            terms.getBuckets().forEach(t -> {
                map.put(t.getKeyAsString(), t.getDocCount());
            });
            return map;
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return new HashMap<>();
    }

    private ScoreFunctionBuilder getStrategyMediaSort(List<StrategyMediaSortDO> strategyMediaSortList) {
        if (strategyMediaSortList == null || strategyMediaSortList.size() == 0) {
            ScoreFunctionBuilder functions = ScoreFunctionBuilders.scriptFunction(new Script("_score"));
            return functions;
        }

        StringBuilder scriptBuilder = new StringBuilder();
        Map<String, Object> params = new HashMap<String, Object>();
        for (StrategyMediaSortDO sort : strategyMediaSortList) {
            double percent = sort.getWeight().doubleValue();
            if (sort.getMediaSort() == MediaSortEnum.BY_PLAY.intValue()) {
                params.put("maxPlayCount", 9999.0);
                params.put("percent1", percent);
                scriptBuilder.append("+ (doc['playCount'].value/params.maxPlayCount)*params.percent1");
            } else if (sort.getMediaSort() == MediaSortEnum.BY_SEARCH.intValue()) {
                params.put("maxSearchCount", 9999.0);
                params.put("percent2", percent);
                scriptBuilder.append("+ (doc['searchCount'].value/params.maxSearchCount)*params.percent2");
            } else if (sort.getMediaSort() == MediaSortEnum.BY_SCORE.intValue()) {
                params.put("percent3", percent);
                scriptBuilder.append("+ (doc['score'].value/10.0)*params.percent3");
            } else if (sort.getMediaSort() == MediaSortEnum.BY_RELEASE_TIME.intValue()) {
                params.put("percent4", percent);
                params.put("year", LocalDate.now().getYear());
                scriptBuilder.append("+ 1/(params.year-doc['releaseYear'].value+1.0)*params.percent4");
            } else if (sort.getMediaSort() == MediaSortEnum.BY_PUBLIC_TIME.intValue()) {
                params.put("year", LocalDate.now().getYear());
                params.put("month", LocalDate.now().getMonthValue());
                params.put("twelve", 12);
                params.put("percent5", percent);
                scriptBuilder.append("+ 1/(params.year*params.twelve+params.month-doc['publishTime'].value.getYear()*12-doc['publishTime'].value.getMonthOfYear()+1.0)*params.percent5");
            }
        }
        String script = scriptBuilder.length() > 0 ? scriptBuilder.toString().replaceFirst("\\+", "") : "";
        ScoreFunctionBuilder functions = ScoreFunctionBuilders.scriptFunction(new Script(ScriptType.INLINE, "painless", script, params));
        return functions;

    }

    @Override
    public List<TypeKindESSearchDTO> findMediaByTypeAndKind(SceneDTO sceneDTO, LinkedHashMap excludeMap, RecommendEngineDTO recommendEngine, Boolean isColdBoot) {
        List<TypeKindESSearchDTO> resultList = new ArrayList<>();

        String sysId = sceneDTO.getSceneDO().getSysId();
        Long strategyId = isColdBoot ? sceneDTO.getCodeBootStrategyDetail().getStrategyDO().getId() : sceneDTO.getRecommendStrategyDetail().getStrategyDO().getId();
        List<MediaTypeTopDTO> mediaTypeList = recommendEngine.getMediaTypeList();

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.filter().add(QueryBuilders.termQuery("sysId", sysId));
        queryBuilder.filter().add(QueryBuilders.termQuery("publishStatus", "1"));
        if (!CollectionUtils.isEmpty(mediaTypeList)) {
            BoolQueryBuilder should = QueryBuilders.boolQuery();
            for (MediaTypeTopDTO type : mediaTypeList) {
                for (MediaKindTopDTO kind : type.getMediaKindList()) {
                    BoolQueryBuilder child = QueryBuilders.boolQuery();
                    child.must().add(QueryBuilders.termQuery("mediaType", type.getMediaType()));
                    child.must().add(QueryBuilders.termQuery("mediaKindId", kind.getMediaKind()));
                    should.should().add(child);
                }
            }
            queryBuilder.must().add(should);
        }
        if (excludeMap.size() > 0) {
            queryBuilder.mustNot().add(QueryBuilders.termsQuery("mediaId", excludeMap.keySet()));
        }

        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("terms").field("mediaTypeKindIdFree.keyword").size(30);
        aggregationBuilder.subAggregation(AggregationBuilders.topHits("tops").fetchSource(new String[]{"mediaId"}, null).sort("strategyScore" + strategyId, SortOrder.DESC).size(20));

        SearchSourceBuilder searchRequestBuilder = SearchSourceBuilder.searchSource();
        searchRequestBuilder.query(queryBuilder);
        searchRequestBuilder.aggregation(aggregationBuilder);
        searchRequestBuilder.size(0);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(searchRequestBuilder);
        searchRequest.indices(ElasticSearchConstant.media_index);
        searchRequest.types(ElasticSearchConstant.type_default);
        try {
//            log.debug(functionScoreQuery.toString());
//            log.debug("________________________");
//            log.debug(searchRequestBuilder.toString());
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            Terms terms = response.getAggregations().get("terms");
            terms.getBuckets().forEach(bucket -> {
                String[] keys = bucket.getKeyAsString().split("_");

                TopHits tops = bucket.getAggregations().get("tops");
                Iterator<SearchHit> iterator = tops.getHits().iterator();
                while (iterator.hasNext()) {
                    SearchHit hit = iterator.next();
                    JSONObject doc = JSONObject.parseObject(hit.getSourceAsString());
                    Integer mediaId = doc.getInteger("mediaId");
                    TypeKindESSearchDTO dto = new TypeKindESSearchDTO();
                    dto.setMediaId(mediaId);
                    dto.setMediaType(Integer.parseInt(keys[0]));
                    dto.setMediaKind(Integer.parseInt(keys[1]));
                    dto.setIsCharge("true".equals(keys[2]) ? 0 : 1);
                    dto.setScore(Float.parseFloat(hit.getSortValues()[0].toString()));
                    resultList.add(dto);
                }
            });
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return resultList;
    }

    @Override
    public List<TypeKindESSearchDTO> findMediaByType(SceneDTO sceneDTO, LinkedHashMap excludeMap, RecommendEngineDTO recommendEngine, Boolean isColdBoot) {
        List<TypeKindESSearchDTO> resultList = new ArrayList<>();

        String sysId = sceneDTO.getSceneDO().getSysId();
        List<MediaTypeTopDTO> mediaTypeList = recommendEngine.getMediaTypeList();
        Long strategyId = isColdBoot ? sceneDTO.getCodeBootStrategyDetail().getStrategyDO().getId() : sceneDTO.getRecommendStrategyDetail().getStrategyDO().getId();

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.filter().add(QueryBuilders.termQuery("sysId", sysId));
        queryBuilder.filter().add(QueryBuilders.termQuery("publishStatus", "1"));
        if (!CollectionUtils.isEmpty(mediaTypeList)) {
            BoolQueryBuilder should = QueryBuilders.boolQuery();
            for (MediaTypeTopDTO type : mediaTypeList) {
                should.should().add(QueryBuilders.termQuery("mediaType", type.getMediaType()));
            }
            queryBuilder.must().add(should);
        }
        if (excludeMap.size() > 0) {
            //mediaId改为mediaCode
            queryBuilder.mustNot().add(QueryBuilders.termsQuery("mediaCode", excludeMap.keySet()));
        }

        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("terms").field("mediaTypeFree").size(10);
        aggregationBuilder.subAggregation(AggregationBuilders.topHits("tops").fetchSource(new String[]{"mediaId"}, null).sort("strategyScore" + strategyId, SortOrder.DESC).size(50));

        SearchSourceBuilder searchRequestBuilder = SearchSourceBuilder.searchSource();
        searchRequestBuilder.query(queryBuilder);
        searchRequestBuilder.aggregation(aggregationBuilder);
        searchRequestBuilder.size(0);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(searchRequestBuilder);
        searchRequest.indices(ElasticSearchConstant.media_index);
        searchRequest.types(ElasticSearchConstant.type_default);
        try {
//            log.debug(functionScoreQuery.toString());
//            log.debug("---------------------------");
//            log.debug(searchRequestBuilder.toString());
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            Terms terms = response.getAggregations().get("terms");
            terms.getBuckets().forEach(bucket -> {
                String[] keys = bucket.getKeyAsString().split("_");

                TopHits tops = bucket.getAggregations().get("tops");
                Iterator<SearchHit> iterator = tops.getHits().iterator();
                while (iterator.hasNext()) {
                    SearchHit hit = iterator.next();
                    JSONObject doc = JSONObject.parseObject(hit.getSourceAsString());
                    Integer mediaId = doc.getInteger("mediaId");
                    //String mediaCode = doc.getString("mediaCode");
                    TypeKindESSearchDTO dto = new TypeKindESSearchDTO();
                    dto.setMediaId(mediaId);
                    //dto.setMediaCode(mediaCode);
                    dto.setMediaType(Integer.parseInt(keys[0]));
                    dto.setIsCharge("true".equals(keys[1]) ? 0 : 1);
                    //dto.setScore(RandomUtils.nextFloat(0.0f, 1.0f));
                    dto.setScore(Float.parseFloat(hit.getSortValues()[0].toString()));
                    resultList.add(dto);
                }
            });
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return resultList;
    }

    @Override
    public List<String> topActors(int days, int size) {
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must().add(QueryBuilders.rangeQuery("log_time").gte(LocalDate.now().minusDays(days)).lte(LocalDate.now()));
        query.must().add(QueryBuilders.existsQuery("actor"));

        AggregationBuilder actorBucket = AggregationBuilders.terms("actor").size(size);

        SearchSourceBuilder searchRequestBuilder = SearchSourceBuilder.searchSource();
        searchRequestBuilder.query(QueryBuilders.matchAllQuery());
        searchRequestBuilder.aggregation(actorBucket);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(searchRequestBuilder);
        searchRequest.indices(ElasticSearchConstant.log_index);
        searchRequest.types(ElasticSearchConstant.type_default);

        List<String> topActors = new ArrayList<>();
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            Terms actors = response.getAggregations().get("actor");
            actors.getBuckets().forEach(a -> {
                topActors.add(a.getKeyAsString());
            });

        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return topActors;
    }

    @Override
    public List<StarESSearchDTO> findMediaByActors(SceneDTO sceneDTO, LinkedHashMap excludeMap, Map<Integer, List<String>> actors, Boolean isColdBoot, Integer size) {
        List<StarESSearchDTO> resultList = new ArrayList<>();

        String sysId = sceneDTO.getSceneDO().getSysId();
        List<StrategyMediaTypeDO> mediaTypeList = isColdBoot ? sceneDTO.getCodeBootStrategyDetail().getStrategyMediaTypeList() : sceneDTO.getRecommendStrategyDetail().getStrategyMediaTypeList();
        Long strategyId = isColdBoot ? sceneDTO.getCodeBootStrategyDetail().getStrategyDO().getId() : sceneDTO.getRecommendStrategyDetail().getStrategyDO().getId();

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.filter().add(QueryBuilders.termQuery("sysId", sysId));
        queryBuilder.filter().add(QueryBuilders.termQuery("publishStatus", "1"));
        if (!CollectionUtils.isEmpty(mediaTypeList)) {
            BoolQueryBuilder should = QueryBuilders.boolQuery();
            for (StrategyMediaTypeDO type : mediaTypeList) {
                List<String> actorList = actors.get(type.getMediaType());
                if (!CollectionUtils.isEmpty(actorList)) {
                    BoolQueryBuilder actorShould = QueryBuilders.boolQuery();
                    actorShould.must().add(QueryBuilders.termsQuery("actor", actorList));
                    actorShould.must().add(QueryBuilders.termQuery("mediaType", type.getMediaType()));
                    should.should(actorShould);
                } else {
                    should.should(QueryBuilders.termQuery("mediaType", type.getMediaType()));
                }

            }
            queryBuilder.must().add(should);
        }
        if (excludeMap.size() > 0) {
            queryBuilder.mustNot().add(QueryBuilders.termsQuery("mediaId", excludeMap.keySet()));
        }
        TermsAggregationBuilder freeTerms = AggregationBuilders.terms("freeTerms").field("free");
        freeTerms.subAggregation(AggregationBuilders.topHits("topN").size(size));

        SearchSourceBuilder searchRequestBuilder = SearchSourceBuilder.searchSource();
        searchRequestBuilder.query(queryBuilder);
        searchRequestBuilder.aggregation(freeTerms);
        searchRequestBuilder.sort("strategyScore" + strategyId, SortOrder.DESC);
        searchRequestBuilder.size(size);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(searchRequestBuilder);
        searchRequest.indices(ElasticSearchConstant.media_index);
        searchRequest.types(ElasticSearchConstant.type_default);
        try {
//            log.debug("明星推荐:\r\n{}", searchRequest.toString());
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Terms terms = response.getAggregations().get("freeTerms");
            terms.getBuckets().forEach(t -> {
                TopHits topHits = t.getAggregations().get("topN");
                topHits.getHits().forEach(h -> {
                    JSONObject doc = JSONObject.parseObject(h.getSourceAsString());
                    Integer mediaId = doc.getInteger("mediaId");
                    String mediaType = doc.getString("mediaType");
                    String free = doc.getString("free");
                    StarESSearchDTO dto = new StarESSearchDTO();
                    dto.setMediaId(mediaId);
                    dto.setMediaType(Integer.parseInt(mediaType));
                    dto.setIsCharge("true".equals(free) ? 0 : 1);
                    resultList.add(dto);
                });

            });
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return resultList;
    }
}
