package com.ubo.iptv.job.handler;

import com.alibaba.fastjson.JSONObject;
import com.ubo.iptv.core.constant.ElasticSearchConstant;
import com.ubo.iptv.core.constant.RedisConstant;
import com.ubo.iptv.core.util.DateUtil;
import com.ubo.iptv.entity.gdgd.BrowseFilterDTO;
import com.ubo.iptv.entity.gdgd.SceneDTO;
import com.ubo.iptv.entity.gdgd.StrategyDetailDTO;
import com.ubo.iptv.job.entity.UserBlackItem;
import com.ubo.iptv.job.service.EsService;
import com.ubo.iptv.job.service.RedisService;
import com.ubo.iptv.mybatis.recommend.entity.SceneDO;
import com.ubo.iptv.mybatis.recommend.entity.StrategyBrowseFilterDO;
import com.ubo.iptv.mybatis.recommend.mapper.SceneMapper;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.util.ShardingUtil;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilders;
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
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @Author: xuning
 * @Date: 2020-11-02
 */
@Service
@JobHandler(value = "browseFilterJobHandler")
@Slf4j
public class BrowseFilterJobHandler extends IJobHandler {

    @Autowired
    private RedisService redisService;
    @Resource
    private SceneMapper sceneMapper;
    @Autowired
    private EsService esService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        long begin = System.currentTimeMillis();
        // ????????????
        ShardingUtil.ShardingVO shardingVO = ShardingUtil.getShardingVo();
        XxlJobLogger.log("????????????: ??????????????????={}, ????????????={}", shardingVO.getIndex(), shardingVO.getTotal());

        browseFilter(shardingVO);

        XxlJobLogger.log("???????????????????????? {}???", (System.currentTimeMillis() - begin) / 1000);
        return SUCCESS;
    }

    private void browseFilter(ShardingUtil.ShardingVO shardingVO) {
        int index = 0;
        int size = 2000;

        //???????????????????????????
        List<SceneDO> sceneList = sceneMapper.selectList(null);
        Pair<Integer, Map<String, StrategyBrowseFilterDO>> mediaTypeConfig = getMediaTypeConfig(sceneList);
        //??????????????????????????????????????????
        Integer maxBrowseDays = mediaTypeConfig.getKey();
        //??????????????????????????????????????????
        Map<String, StrategyBrowseFilterDO> browseFilterSceneMap = mediaTypeConfig.getValue();
        //??????????????????????????????,???????????????
        if (browseFilterSceneMap.keySet().size() <= 0) {
            return;
        }
        //?????????????????????????????????????????????
        while (true) {
            // ??????????????????
            if (shardingVO.getTotal() > 1 && (index + 1) % shardingVO.getTotal() != shardingVO.getIndex()) {
                index++;
                continue;
            }
            String yesterday = DateUtil.format(LocalDate.now().minusDays(1L), "yyyyMMdd");
            Set<String> userSet = redisService.getZSet(String.format(RedisConstant.DAY_ACTIVE_USER_IDS, yesterday), index, size);
            userSet.forEach(userId -> {
                //??????????????????5???,???????????????????????????????????????
                List<Integer> userIdList = new ArrayList<>();
                userIdList.add(Integer.parseInt(userId));
                List<UserBlackItem> exposureNoClick = exposureNoClick(LocalDate.now(), maxBrowseDays, userIdList);
                browseFilterSceneMap.entrySet().stream().forEach(stringMapEntry -> {
                            String sceneId = stringMapEntry.getKey();
                            StrategyBrowseFilterDO strategyBrowseFilterDO = stringMapEntry.getValue();
                            //???????????????????????????????????????
                            Map<String, BrowseFilterDTO> browseFilterMap = new HashMap();
                            String browseFilterKey = String.format(RedisConstant.EXPOSURE_EXCLUDE, Integer.parseInt(userId), Integer.parseInt(sceneId));
                            String oldBrowseFilterValue = redisService.get(browseFilterKey);
                            if (StringUtils.isNotEmpty(oldBrowseFilterValue)) {
                                browseFilterMap = (Map) JSONObject.parseObject(oldBrowseFilterValue);
                            }
                            //?????????????????????????????????????????????
                            Integer freezeDays = 30;
                            Integer browseDays = 5;
                            if (null != strategyBrowseFilterDO) {
                                freezeDays = strategyBrowseFilterDO.getFreezeDays();
                                browseDays = strategyBrowseFilterDO.getBrowseDays();
                            }
                            //??????????????????????????????????????????
                            for (UserBlackItem userBlackItem : exposureNoClick) {
                                if (userBlackItem.getSceneId().toString().equals(sceneId) && userBlackItem.getDays() >= browseDays) {
                                    Integer lastIndex = userBlackItem.getDaysValue().lastIndexOf("0");
                                    if (lastIndex < userBlackItem.getDays() - browseDays) {
                                        BrowseFilterDTO browseFilterDTO = new BrowseFilterDTO();
                                        browseFilterDTO.setUserId(userId);
                                        browseFilterDTO.setSceneId(sceneId);
                                        browseFilterDTO.setMediaId(userBlackItem.getMediaId().toString());
                                        browseFilterDTO.setDate(DateUtil.format(LocalDateTime.now()));
                                        browseFilterMap.put(userBlackItem.getMediaId().toString(), browseFilterDTO);
                                    }
                                }
                            }
                            //?????????????????????????????????
                            for (String key : browseFilterMap.keySet()) {
                                BrowseFilterDTO browseFilterDTO = browseFilterMap.get(key);
                                String date = browseFilterDTO.getDate();
                                if (DateUtil.parse(date).isBefore(LocalDateTime.now().minusDays(freezeDays))) {
                                    //????????????????????????
                                    browseFilterMap.remove(key);
                                }
                            }
                            //????????????
                            redisService.set(browseFilterKey, JSONObject.toJSONString(browseFilterMap), maxBrowseDays * 24 * 60 * 60L);
                        }

                );
            });
            // ??????????????????
            if (userSet.size() < size) {
                break;
            }
            log.info("processing : index={}, size={}", index, size);
            index++;
        }
    }

    private Pair<Integer, Map<String, StrategyBrowseFilterDO>> getMediaTypeConfig(List<SceneDO> sceneList) {
        Map<String, StrategyBrowseFilterDO> mediaTypeMap = new HashMap<>();
        Integer maxBrowseDays = 5;
        for (SceneDO sceneDO : sceneList) {
            String sceneInfo = redisService.getHash(RedisConstant.HASH_SCENE_KEY, sceneDO.getId().toString());
            if (StringUtils.isNotEmpty(sceneInfo)) {
                //??????????????????
                SceneDTO strategyConfig = JSONObject.parseObject(sceneInfo, SceneDTO.class);
                StrategyDetailDTO recommendStrategyDetail = strategyConfig.getRecommendStrategyDetail();
                if (null != recommendStrategyDetail) {
                    StrategyBrowseFilterDO strategyBrowseFilterDO = recommendStrategyDetail.getStrategyBrowseFilterDO();
                    if (null != strategyBrowseFilterDO) {
                        mediaTypeMap.put(sceneDO.getId().toString(), strategyBrowseFilterDO);
                        if (strategyBrowseFilterDO.getBrowseDays() > maxBrowseDays) {
                            maxBrowseDays = strategyBrowseFilterDO.getBrowseDays();
                        }
                    }
                }
            }
        }
        return new Pair<>(maxBrowseDays, mediaTypeMap);
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
//        log.debug("________________________");
//        log.debug(searchSourceBuilder.toString());
//        System.out.println(searchSourceBuilder);
        List<UserBlackItem> list = new ArrayList<>();
        try {
            SearchResponse response = esService.getClient().search(request, RequestOptions.DEFAULT);
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
