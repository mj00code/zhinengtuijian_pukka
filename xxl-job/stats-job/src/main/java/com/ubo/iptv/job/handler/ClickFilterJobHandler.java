package com.ubo.iptv.job.handler;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ubo.iptv.core.constant.ElasticSearchConstant;
import com.ubo.iptv.core.constant.RedisConstant;
import com.ubo.iptv.core.util.DateUtil;
import com.ubo.iptv.entity.gdgd.ClickFilterDTO;
import com.ubo.iptv.entity.gdgd.SceneDTO;
import com.ubo.iptv.entity.gdgd.StrategyDetailDTO;
import com.ubo.iptv.job.service.RedisService;
import com.ubo.iptv.mybatis.recommend.entity.SceneDO;
import com.ubo.iptv.mybatis.recommend.entity.StrategyClickFilterDO;
import com.ubo.iptv.mybatis.recommend.mapper.SceneMapper;
import com.ubo.iptv.mybatis.recommend.mapper.StrategyColdBootMapper;
import com.ubo.iptv.mybatis.recommend.mapper.StrategyMapper;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.util.ShardingUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @Author: xuning
 * @Date: 2020-11-02
 */
@Service
@JobHandler(value = "clickFilterJobHandler")
@Slf4j
public class ClickFilterJobHandler extends IJobHandler {

    @Autowired
    private RestHighLevelClient esClient;
    @Autowired
    private RedisService redisService;
    @Autowired
    private SceneMapper sceneMapper;
    @Autowired
    private StrategyMapper strategyMapper;
    @Autowired
    private StrategyColdBootMapper strategyColdBootMapper;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        long begin = System.currentTimeMillis();
        // 分片参数
        ShardingUtil.ShardingVO shardingVO = ShardingUtil.getShardingVo();
        XxlJobLogger.log("分片参数: 当前分片序号={}, 总分片数={}", shardingVO.getIndex(), shardingVO.getTotal());

        clickFilter(shardingVO);

        XxlJobLogger.log("任务完成：共耗时 {}秒", (System.currentTimeMillis() - begin) / 1000);
        return SUCCESS;
    }

    public void clickFilter(ShardingUtil.ShardingVO shardingVO) {
        int index = 0;
        int size = 2000;
        //获取所有场景的信息
        List<SceneDO> sceneList = sceneMapper.selectList(new LambdaQueryWrapper<SceneDO>());
        //获取场景下类型的点击排除信息
        Map<String, Map<String, StrategyClickFilterDO>> clickFilterConfigMap = getMediaTypeConfig(sceneList);
        //如果所有场景都没有配置点击排除,则逻辑结束
        if (clickFilterConfigMap.keySet().size() <= 0) {
            return;
        }
        //昨天有操作记录的所有用户的信息
        while (true) {
            // 利用页数分片
            if (shardingVO.getTotal() > 1 && (index + 1) % shardingVO.getTotal() != shardingVO.getIndex()) {
                index++;
                continue;
            }
            String yesterday = DateUtil.format(LocalDate.now().minusDays(1L), "yyyyMMdd");
            Set<String> userSet = redisService.getZSet(String.format(RedisConstant.DAY_ACTIVE_USER_IDS, yesterday), index, size);
            userSet.forEach(userId -> {
                //当前用户昨天点击过的媒资列表
                Map yesterdayClickMediaMap = getYesterdayClickMedia(userId);
                //搜索观看超过10分钟的所有日志信息  以用户,scene_id,媒资类型分组 详细信息包括媒资id
                List<ClickFilterDTO> clickFilterDTOList = getMustRecommendMedia(userId, clickFilterConfigMap);
                clickFilterConfigMap.entrySet().stream().forEach(stringMapEntry -> {
                    String sceneId = stringMapEntry.getKey();
                    Map<String, StrategyClickFilterDO> strategyClickFilterDOMap = stringMapEntry.getValue();
                    //获取上次缓存的点击必推数据
                    Map<String, ClickFilterDTO> clickMustMap = new HashMap();
                    String clickMustKey = String.format(RedisConstant.CLICK_MUST, Integer.parseInt(userId), Integer.parseInt(sceneId));
                    String oldClickMustValue = redisService.get(clickMustKey);
                    if (StringUtils.isNotEmpty(oldClickMustValue)) {
                        clickMustMap = (Map) JSONObject.parseObject(oldClickMustValue);
                    }
                    //点击必推数据map加入今天点击必推的媒资
                    for (ClickFilterDTO clickFilterDTO : clickFilterDTOList) {
                        if (sceneId.equals(clickFilterDTO.getSceneId())) {
                            clickMustMap.put(clickFilterDTO.getMediaId(), clickFilterDTO);
                        }
                    }

                    //获取上次缓存的点击排除数据
                    Map<String, ClickFilterDTO> clickExcludeMap = new HashMap();
                    String clickExcludeKey = String.format(RedisConstant.CLICK_EXCLUDE, Integer.parseInt(userId), Integer.parseInt(sceneId));
                    String oldClickExcludeValue = redisService.get(clickExcludeKey);
                    if (StringUtils.isNotEmpty(oldClickExcludeValue)) {
                        clickExcludeMap = (Map) JSONObject.parseObject(oldClickExcludeValue);
                    }

                    //查询下map中超过过期时间的数据,今天是否有点击行为,如果有则更新时间,没有则放到点击排除列表中,并且从点击必推列表中删除
                    //次数的设置要从策略的缓存取出来
                    Integer ignoreDays = 2;
                    for (String key : clickMustMap.keySet()) {
                        ClickFilterDTO clickFilterDTO = clickMustMap.get(key);
                        String date = clickFilterDTO.getDate();
                        StrategyClickFilterDO strategyClickFilterDO = strategyClickFilterDOMap.get(sceneId + clickFilterDTO.getMediaType());
                        if (null != strategyClickFilterDO) ignoreDays = strategyClickFilterDO.getIgnoreDays();
                        if (DateUtil.parse(date).isBefore(LocalDateTime.now().minusDays(ignoreDays))) {
                            // 从缓存中查看当前用户有没有点击过这个媒资
                            if (yesterdayClickMediaMap.containsKey(key)) {
                                clickFilterDTO.setDate(DateUtil.format(LocalDateTime.now()));
                                clickMustMap.put(key, clickFilterDTO);
                            }
                        } else {
                            //放到点击排除列表
                            clickFilterDTO.setDate(DateUtil.format(LocalDateTime.now()));
                            clickExcludeMap.put(key, clickFilterDTO);
                            //剔除点击必推列表
                            clickMustMap.remove(key);
                        }
                    }
                    //次数的设置要从策略的缓存取出来
                    Integer freezeDays = 30;
                    //删除过期的点击排除列表
                    for (String key : clickExcludeMap.keySet()) {
                        ClickFilterDTO clickFilterDTO = clickExcludeMap.get(key);
                        String date = clickFilterDTO.getDate();
                        StrategyClickFilterDO strategyClickFilterDO = strategyClickFilterDOMap.get(sceneId + clickFilterDTO.getMediaType());
                        if (null != strategyClickFilterDO) freezeDays = strategyClickFilterDO.getFreezeDays();
                        if (DateUtil.parse(date).isBefore(LocalDateTime.now().minusDays(freezeDays))) {
                            //剔除点击必推列表
                            clickExcludeMap.remove(key);
                        }
                    }

                    //把新的必推信息更新到点击必推的redis中
                    redisService.set(clickMustKey, JSONObject.toJSONString(clickMustMap), ignoreDays * 24 * 60 * 60L);

                    //把新的点击排除信息更新到点击排除的redis中
                    redisService.set(clickExcludeKey, JSONObject.toJSONString(clickExcludeMap), freezeDays * 24 * 60 * 60L);
                });

            });
            // 数量不足退出
            if (userSet.size() < size) {
                break;
            }
            log.info("processing : index={}, size={}", index, size);
            index++;
        }


    }

    private Map<String, Map<String, StrategyClickFilterDO>> getMediaTypeConfig(List<SceneDO> sceneList) {
        Map sceneMap = new HashMap();
        for (SceneDO sceneDO : sceneList) {
            Map<String, StrategyClickFilterDO> mediaTypeMap = new HashMap();
            String sceneInfo = redisService.getHash(RedisConstant.HASH_SCENE_KEY, sceneDO.getId().toString());
            if (StringUtils.isNotEmpty(sceneInfo)) {
                //策略详细配置
                SceneDTO strategyConfig = JSONObject.parseObject(sceneInfo, SceneDTO.class);
                StrategyDetailDTO recommendStrategyDetail = strategyConfig.getRecommendStrategyDetail();
                if (null != recommendStrategyDetail) {
                    List<StrategyClickFilterDO> strategyClickFilterDOS = recommendStrategyDetail.getStrategyClickFilterList();
                    if (!CollectionUtils.isEmpty(strategyClickFilterDOS)) {
                        for (StrategyClickFilterDO strategyClickFilterDO : strategyClickFilterDOS) {
                            mediaTypeMap.put(sceneDO.getId() + strategyClickFilterDO.getMediaType() + "", strategyClickFilterDO);
                        }
                        sceneMap.put(sceneDO.getId().toString(), mediaTypeMap);
                    }
                }
            }

        }
        return sceneMap;
    }

    private List<ClickFilterDTO> getMustRecommendMedia(String userId, Map<String, Map<String, StrategyClickFilterDO>> mediaTypeConfigMap) {
        //queryBuilder
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now().minusDays(1);
        queryBuilder.must().add(QueryBuilders.rangeQuery("log_time").gte(start).lte(end));
        //queryBuilder
        queryBuilder.must().add(QueryBuilders.termQuery("action_type", "heart_beat"));
        queryBuilder.must().add(QueryBuilders.termQuery("user_db_id", userId));
        //AggregationBuilder
        AggregationBuilder userBucket = AggregationBuilders.terms("terms1").field("scene_id").size(100)
                .subAggregation(AggregationBuilders.terms("terms2").field("mediaType").size(100)
                        .subAggregation(AggregationBuilders.terms("terms3").field("mediaId").size(300)));
        //searchSourceBuilder
        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.aggregation(userBucket);

        //searchRequest
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(ElasticSearchConstant.log_index).types(ElasticSearchConstant.type_default);
        searchRequest.source(searchSourceBuilder);
//        log.debug("________________________");
//        log.debug(searchSourceBuilder.toString());
        try {
            SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
            List<ClickFilterDTO> list = new ArrayList<>();

            Terms terms1 = searchResponse.getAggregations().get("terms1");

            terms1.getBuckets().forEach(term1 -> {
                Terms terms2 = term1.getAggregations().get("terms2");
                terms2.getBuckets().forEach(term2 -> {
                    Terms terms3 = term2.getAggregations().get("terms3");
                    terms3.getBuckets().forEach(term3 -> {
                        //次数的设置要从策略的缓存取出来
                        Integer playMinutes = 10;
                        StrategyClickFilterDO strategyClickFilterDO = mediaTypeConfigMap.get(term1.getKeyAsString()).get(term1.getKeyAsString() + term2.getKeyAsString());
                        if (null != strategyClickFilterDO) playMinutes = strategyClickFilterDO.getPlayMinutes();
                        double heartBeatCount = Math.ceil(playMinutes / 5);
                        if (term3.getDocCount() >= heartBeatCount) {
                            ClickFilterDTO clickFilterDTO = new ClickFilterDTO();
                            clickFilterDTO.setUserId(userId);
                            clickFilterDTO.setSceneId(term1.getKeyAsString());
                            clickFilterDTO.setMediaType(term2.getKeyAsString());
                            clickFilterDTO.setMediaId(term3.getKeyAsString());
                            clickFilterDTO.setDate(DateUtil.format(LocalDateTime.now()));
                            list.add(clickFilterDTO);
                        }
                    });


                });
            });

            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, ClickFilterDTO> getYesterdayClickMedia(String userId) {
        //queryBuilder
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now().minusDays(1);
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must().add(QueryBuilders.termQuery("action_type", "click"));
        queryBuilder.must().add(QueryBuilders.termQuery("user_db_id", userId));
        queryBuilder.must().add(QueryBuilders.rangeQuery("log_time").gte(start).lte(end));
        //AggregationBuilder
        AggregationBuilder userBucket = AggregationBuilders.terms("terms1").field("scene_id").size(100)
                .subAggregation(AggregationBuilders.terms("terms2").field("mediaType").size(100)
                        .subAggregation(AggregationBuilders.terms("terms3").field("mediaId").size(300)));
        //searchSourceBuilder
        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.aggregation(userBucket);

        //searchRequest
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(ElasticSearchConstant.log_index).types(ElasticSearchConstant.type_default);
        searchRequest.source(searchSourceBuilder);
//        log.debug("________________________");
//        log.debug(searchSourceBuilder.toString());
        try {
            SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
            Map<String, ClickFilterDTO> map = new HashMap<>();

            Terms terms1 = searchResponse.getAggregations().get("terms1");

            terms1.getBuckets().forEach(term1 -> {
                Terms terms2 = term1.getAggregations().get("terms2");
                terms2.getBuckets().forEach(term2 -> {
                    Terms terms3 = term2.getAggregations().get("terms3");
                    terms3.getBuckets().forEach(term3 -> {
                        ClickFilterDTO clickFilterDTO = new ClickFilterDTO();
                        clickFilterDTO.setUserId(userId);
                        clickFilterDTO.setSceneId(term1.getKeyAsString());
                        clickFilterDTO.setMediaType(term2.getKeyAsString());
                        clickFilterDTO.setMediaId(term3.getKeyAsString());
                        clickFilterDTO.setDate(DateUtil.format(LocalDateTime.now()));
                        map.put(clickFilterDTO.getMediaId(), clickFilterDTO);
                    });


                });
            });

            return map;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
