package com.ubo.iptv.job.handler;

import com.alibaba.fastjson.JSONObject;
import com.ubo.iptv.core.constant.ElasticSearchConstant;
import com.ubo.iptv.core.constant.RedisConstant;
import com.ubo.iptv.job.service.EsService;
import com.ubo.iptv.job.service.RedisService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.util.ShardingUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: xuning
 * @Date: 2020-11-06
 */
@Service
@JobHandler(value = "topActorsJobHandler")
@Slf4j
public class TopActorsJobHandler extends IJobHandler {
    @Autowired
    private EsService esService;
    @Autowired
    private RedisService redisService;


    @Override
    public ReturnT<String> execute(String param) throws Exception {
        long begin = System.currentTimeMillis();
        // 分片参数
        ShardingUtil.ShardingVO shardingVO = ShardingUtil.getShardingVo();
        XxlJobLogger.log("分片参数: 当前分片序号={}, 总分片数={}", shardingVO.getIndex(), shardingVO.getTotal());

        cacheTopActors(shardingVO);

        XxlJobLogger.log("任务完成：共耗时 {}秒", (System.currentTimeMillis() - begin) / 1000);
        return SUCCESS;
    }

    private void cacheTopActors(ShardingUtil.ShardingVO shardingVO) {
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must().add(QueryBuilders.rangeQuery("log_time").gte(LocalDate.now().minusDays(60)).lte(LocalDate.now()));
        query.must().add(QueryBuilders.existsQuery("actor"));
        query.mustNot().add(QueryBuilders.termsQuery("actor.keyword", "暂无", "未知", "无", ""));

        AggregationBuilder aggregation = AggregationBuilders.terms("type_bucket").field("media_type").size(10);
        aggregation.subAggregation(AggregationBuilders.terms("actor_bucket").field("actor.keyword").size(5));

        SearchSourceBuilder searchRequestBuilder = SearchSourceBuilder.searchSource();
        searchRequestBuilder.query(query);
        searchRequestBuilder.aggregation(aggregation);
        searchRequestBuilder.size(0);
        log.info(searchRequestBuilder.toString());

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(searchRequestBuilder);
        searchRequest.indices(ElasticSearchConstant.log_index);
        searchRequest.types(ElasticSearchConstant.type_default);

        Map<Integer, List<String>> map = new HashMap<>();
        try {
            SearchResponse response = esService.getClient().search(searchRequest, RequestOptions.DEFAULT);

            Terms types = response.getAggregations().get("type_bucket");
            types.getBuckets().forEach(t -> {
                List<String> topActors = new ArrayList<>();
                Terms actors = t.getAggregations().get("actor_bucket");
                actors.getBuckets().forEach(a -> {
                    topActors.add(a.getKeyAsString());
                });
                map.put(t.getKeyAsNumber().intValue(), topActors);
            });

        } catch (IOException e) {
            log.error(e.getMessage());
        }

        redisService.set(RedisConstant.MEDIA_ACTOR_TOP5, JSONObject.toJSONString(map));

    }

}
