package com.ubo.iptv.job.service;

import com.ubo.iptv.core.constant.ElasticSearchConstant;
import com.ubo.iptv.job.entity.UserBlackItem;
import com.ubo.iptv.mybatis.recommend.entity.MediaSummaryDO;
import com.ubo.iptv.mybatis.recommend.entity.UserDailySummaryDO;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ExtendedBounds;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.ParsedCardinality;
import org.elasticsearch.search.aggregations.pipeline.ParsedSimpleValue;
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * @Author: xuning
 * @Date: 2020-11-03
 */
@Component
@Slf4j
public class EsService {

    @Autowired
    private RestHighLevelClient esClient;

    /**
     * 批量写入ES
     *
     * @param request
     * @return
     */
    public boolean bulk(BulkRequest request) {
        try {
            BulkResponse bulkResponse = esClient.bulk(request, RequestOptions.DEFAULT);
            return !bulkResponse.hasFailures();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<UserDailySummaryDO> searchDailySummary(Set<Long> uids) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must().add(QueryBuilders.termsQuery("user_db_id", uids));


        BoolQueryBuilder should = QueryBuilders.boolQuery();
        should.should().add(QueryBuilders.existsQuery("RECOMMEND_LOCATION_CLICK"));
        should.should().add(QueryBuilders.existsQuery("DETAIL_2_PALY_PAGE"));
        queryBuilder.must().add(should);

        AggregationBuilder userBucket = AggregationBuilders.terms("terms1").field("user_db_id");
        AggregationBuilder tkBucket = AggregationBuilders.terms("terms2").script(new Script("List y=new ArrayList();for (int i = 0; i < doc['mediaKindId'].length; ++i) {y.add( doc['mediaKindId'][i]+':'+doc['mediaType'].value) } return y;"));
        AggregationBuilder playFilter = AggregationBuilders.filter("playFilter", QueryBuilders.existsQuery("DETAIL_2_PALY_PAGE"));
        AggregationBuilder clickFilter = AggregationBuilders.filter("clickFilter", QueryBuilders.existsQuery("RECOMMEND_LOCATION_CLICK"));
        tkBucket.subAggregation(playFilter);
        tkBucket.subAggregation(clickFilter);
        userBucket.subAggregation(tkBucket);


        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        searchSourceBuilder.query(queryBuilder);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(searchSourceBuilder);


        try {
            SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
            List<UserDailySummaryDO> list = new ArrayList<>();

            Terms terms1 = searchResponse.getAggregations().get("terms1");
            terms1.getBuckets().forEach(u -> {
                Terms terms2 = u.getAggregations().get("terms2");
                terms2.getBuckets().forEach(m -> {
                    String[] key = m.getKeyAsString().split(":");
                    UserDailySummaryDO uds = new UserDailySummaryDO();
                    uds.setMediaType(Integer.parseInt(key[1]));
                    uds.setMediaKindId(Integer.parseInt(key[0]));

                    Filter play = m.getAggregations().get("playFilter");
                    Filter click = m.getAggregations().get("clickFilter");

                    uds.setPlayCount(Long.valueOf(play.getDocCount()).intValue());
                    uds.setClickCount(Long.valueOf(click.getDocCount()).intValue());
                    list.add(uds);
                });
            });

            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }


    public List<MediaSummaryDO> searchMediaSummary(Set<Long> mediaIds) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must().add(QueryBuilders.termsQuery("mediaId", mediaIds));


        BoolQueryBuilder should = QueryBuilders.boolQuery();
        should.should().add(QueryBuilders.existsQuery("DETAIL_2_PALY_PAGE"));
        should.should().add(QueryBuilders.existsQuery("SEARCH_CLICK"));
        queryBuilder.must().add(should);

        LocalDate yesterday = LocalDate.now().minusDays(1);

        AggregationBuilder mediaBucket = AggregationBuilders.terms("terms1").field("media_id");
        AggregationBuilder eventBucket = AggregationBuilders.terms("terms2").field("event_type");
        AggregationBuilder filter = AggregationBuilders.filter("filter1", QueryBuilders.rangeQuery("log_time").gte(yesterday).lte(yesterday));

        eventBucket.subAggregation(filter);
        mediaBucket.subAggregation(eventBucket);


        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.aggregation(mediaBucket);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(searchSourceBuilder);


        try {
            SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
            List<MediaSummaryDO> list = new ArrayList<>();

            Terms terms1 = searchResponse.getAggregations().get("terms1");
            terms1.getBuckets().forEach(u -> {
                MediaSummaryDO mediaSummary = new MediaSummaryDO();
                mediaSummary.setMediaId(Integer.parseInt(u.getKeyAsString()));
                mediaSummary.setPlayCountDeadline(yesterday.plusDays(7).atTime(LocalTime.MAX));
                mediaSummary.setSearchCountDeadline(yesterday.plusDays(7).atTime(LocalTime.MAX));


                Terms terms2 = searchResponse.getAggregations().get("terms2");
                terms2.getBuckets().forEach(e -> {
                    Filter filter1 = e.getAggregations().get("filter1");
                    if ("DETAIL_2_PALY_PAGE".equals(e.getKeyAsString())) {
                        mediaSummary.setPlayCount(Long.valueOf(filter1.getDocCount()).intValue());
                        mediaSummary.setPlayTotalCount(Long.valueOf(e.getDocCount()).intValue());
                    } else if ("SEARCH_CLICK".equals(e.getKeyAsString())) {
                        mediaSummary.setSearchCount(Long.valueOf(filter1.getDocCount()).intValue());
                        mediaSummary.setSearchTotalCount(Long.valueOf(e.getDocCount()).intValue());
                    }
                });
            });

            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<UserBlackItem> exposureNoClick(LocalDate date, int days, List<Integer> uids) {
        LocalDate start = date.minusDays(days);
        LocalDate end = date.minusDays(1);


        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must().add(QueryBuilders.termsQuery("user_db_id", uids));
        queryBuilder.must().add(QueryBuilders.rangeQuery("log_time").gte(start).lte(end));
        queryBuilder.must().add(QueryBuilders.termsQuery("event_type", "RECOMMEND_LOCATION_CLICK", "RECOMMEND_LOCATION_EXPOSURE"));


        TermsAggregationBuilder userBucket = AggregationBuilders.terms("user_bucket").field("user_db_id").size(uids.size());
        TermsAggregationBuilder sceneBucket = AggregationBuilders.terms("scene_bucket").field("scene_id").size(100);
        TermsAggregationBuilder mediaBucket = AggregationBuilders.terms("media_bucket").field("media_id").size(300);


        DateHistogramAggregationBuilder dayBucket = AggregationBuilders.dateHistogram("day_bucket").field("log_time").minDocCount(0).dateHistogramInterval(DateHistogramInterval.DAY).extendedBounds(new ExtendedBounds(start.toString(), end.toString()));
        dayBucket.subAggregation(AggregationBuilders.cardinality("day_exposure").field("RECOMMEND_LOCATION_EXPOSURE"));
        dayBucket.subAggregation(AggregationBuilders.cardinality("day_click").field("RECOMMEND_LOCATION_CLICK"));


        Map<String, String> map = new HashMap<>();
        map.put("click", "day_click");
        map.put("exposure", "day_exposure");
        dayBucket.subAggregation(PipelineAggregatorBuilders.bucketScript("day_tag", map, new Script("if(params.click>0){return 0}else{return params.exposure-params.click}")));
        mediaBucket.subAggregation(dayBucket);
        mediaBucket.subAggregation(PipelineAggregatorBuilders.sumBucket("sum_exposure", "day_bucket>day_tag"));
//        mediaBucket.subAggregation(PipelineAggregatorBuilders.sumBucket("click", "day_bucket>day_click"));

//        mediaBucket.subAggregation(PipelineAggregatorBuilders.bucketSelector("selector", map, new Script("params.click==0")));
//        mediaBucket.subAggregation(PipelineAggregatorBuilders.bucketSelector("selector", map, new Script("params.click==0 && params.exposure==" + days)));
        sceneBucket.subAggregation(mediaBucket);
        userBucket.subAggregation(sceneBucket);

        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.aggregation(userBucket);


        SearchRequest request = new SearchRequest();
        request.indices(ElasticSearchConstant.log_index);
        request.types(ElasticSearchConstant.type_default);
        request.source(searchSourceBuilder);
//        System.out.println(searchSourceBuilder);
        List<UserBlackItem> list = new ArrayList<>();
        try {
            SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
            Terms ut = response.getAggregations().get("user_bucket");
            for (Terms.Bucket ub : ut.getBuckets()) {
                Terms st = ub.getAggregations().get("scene_bucket");
                for (Terms.Bucket sb : st.getBuckets()) {
                    Terms mt = sb.getAggregations().get("media_bucket");
                    for (Terms.Bucket mb : mt.getBuckets()) {
                        ParsedSimpleValue sp = mb.getAggregations().get("sum_exposure");

                        UserBlackItem item = new UserBlackItem();
                        item.setSceneId(sb.getKeyAsNumber().intValue());
                        item.setUserDbId(ub.getKeyAsNumber().intValue());
                        item.setMediaId(mb.getKeyAsNumber().intValue());
                        item.setDays(Double.valueOf(sp.value()).intValue());
                        StringBuilder daysValue = new StringBuilder();

                        ParsedDateHistogram dateHistogram = mb.getAggregations().get("day_bucket");
                        dateHistogram.getBuckets().forEach(
                                b -> {
                                    ParsedCardinality day_exposure = b.getAggregations().get("day_exposure");
                                    ParsedCardinality day_click = b.getAggregations().get("day_click");
                                    if (day_click.getValue() == 0 && day_exposure.getValue() == 1) {
                                        daysValue.append(1);
                                    } else {
                                        daysValue.append(0);
                                    }

                                }
                        );
                        item.setDaysValue(daysValue.toString());

                        list.add(item);
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            log.error(request.source().toString());
        }
        return list;
    }

}
