package com.ubo.iptv.job.handler;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ubo.iptv.core.constant.ElasticSearchConstant;
import com.ubo.iptv.core.constant.RedisConstant;
import com.ubo.iptv.core.enums.StrategyTypeEnum;
import com.ubo.iptv.core.util.DateUtil;
import com.ubo.iptv.job.service.RedisService;
import com.ubo.iptv.mybatis.recommend.entity.SceneDO;
import com.ubo.iptv.mybatis.recommend.entity.StrategyColdBootDO;
import com.ubo.iptv.mybatis.recommend.entity.StrategyDO;
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

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: xuning
 * @Date: 2020-11-02
 */
@Service
@JobHandler(value = "codeBootJudgeJobHandler")
@Slf4j
public class CodeBootJudgeJobHandler extends IJobHandler {

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

        job(shardingVO);

        XxlJobLogger.log("任务完成：共耗时 {}秒", (System.currentTimeMillis() - begin) / 1000);
        return SUCCESS;
    }

    public void job(ShardingUtil.ShardingVO shardingVO) {
        int index = 0;
        int size = 2000;
        //获取所有场景的信息
        List<SceneDO> sceneList = sceneMapper.selectList(null);
        //昨天有操作记录的所有用户的信息
        while (true) {
            // 利用页数分片
            if (shardingVO.getTotal() > 1 && (index + 1) % shardingVO.getTotal() != shardingVO.getIndex()) {
                index++;
                continue;
            }
            String yesterday = DateUtil.format(LocalDate.now(), "yyyyMMdd");
            //String yesterday = DateUtil.format(LocalDate.now().minusDays(1L), "yyyyMMdd");
            Set<String> userSet = redisService.getZSet(String.format(RedisConstant.DAY_ACTIVE_USER_IDS, yesterday), index, size);
            userSet.forEach(userId -> {
                //设置冷启动
                setCodeBoot(sceneList, Integer.parseInt(userId));
            });
            // 数量不足退出
            if (userSet.size() < size) {
                break;
            }
            log.info("processing : index={}, size={}", index, size);
            index++;
        }
    }

    private void setCodeBoot(List<SceneDO> sceneList, Integer userId) {
        //从缓存中获取场景冷启动信息
        String key = String.format(RedisConstant.CODE_BOOT, userId);
        String userColdBoot = redisService.get(key);
        Map<String, Boolean> map = new HashMap();
        if (StringUtils.isNotEmpty(userColdBoot)) {
            map = (Map) JSONObject.parseObject(userColdBoot, HashMap.class);
        }

        //用户所有情景下点击次数
        Map<String, Long> resultMap = getClickCount(userId);
        for (SceneDO sceneDO : sceneList) {
            //冷启动设置的点击次数门槛
            map.put(sceneDO.getId().toString(), true);
            if (resultMap.containsKey(sceneDO.getId().toString())) {
                StrategyDO strategyDO = strategyMapper.selectOne(new LambdaQueryWrapper<StrategyDO>().eq(StrategyDO::getSceneId, sceneDO.getId()).eq(StrategyDO::getType, StrategyTypeEnum.COLD_BOOT.intValue()));
                if (null != strategyDO) {
                    StrategyColdBootDO strategyColdBootDO = strategyColdBootMapper.selectOne(new LambdaQueryWrapper<StrategyColdBootDO>().eq(StrategyColdBootDO::getStrategyId, strategyDO.getId()));
                    if (null != strategyColdBootDO) {
                        if (resultMap.get(sceneDO.getId().toString()) > strategyColdBootDO.getClickNums()) {
                            map.put(sceneDO.getId().toString(), false);
                        }
                    }
                }
            }
        }
        redisService.set(key, JSONObject.toJSON(map).toString());
    }

    private Map<String, Long> getClickCount(Integer userId) {
        //搜索条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must().add(QueryBuilders.termQuery("user_db_id", userId));
        queryBuilder.must().add(QueryBuilders.existsQuery("RECOMMEND_LOCATION_CLICK"));

        AggregationBuilder userBucket = AggregationBuilders.terms("terms1").field("scene_id").size(100);

        //searchSourceBuilder
        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.aggregation(userBucket);
        searchSourceBuilder.size(0);
        log.info(searchSourceBuilder.toString());

        //searchRequest
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(ElasticSearchConstant.log_index).types(ElasticSearchConstant.type_default);
        searchRequest.source(searchSourceBuilder);
//        log.debug("________________________");
//        log.debug(searchSourceBuilder.toString());
        System.out.println(searchSourceBuilder);
        try {
            //搜索
            SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
            Map<String, Long> sceneInfo = new HashMap<>();

            Terms terms1 = searchResponse.getAggregations().get("terms1");
            terms1.getBuckets().forEach(term1 -> {
                sceneInfo.put(term1.getKeyAsString(), term1.getDocCount());
            });
            return sceneInfo;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
