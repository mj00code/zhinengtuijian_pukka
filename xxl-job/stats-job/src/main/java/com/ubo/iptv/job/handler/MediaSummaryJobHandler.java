package com.ubo.iptv.job.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ubo.iptv.core.constant.ElasticSearchConstant;
import com.ubo.iptv.core.constant.RedisConstant;
import com.ubo.iptv.core.util.DateUtil;
import com.ubo.iptv.job.service.EsService;
import com.ubo.iptv.job.service.RedisService;
import com.ubo.iptv.mybatis.recommend.entity.MediaSummaryDO;
import com.ubo.iptv.mybatis.recommend.mapper.MediaSummaryMapper;
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
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: xuning
 * @Date: 2020-11-06
 */
@Service
@JobHandler(value = "mediaSummaryJobHandler")
@Slf4j
public class MediaSummaryJobHandler extends IJobHandler {
    @Autowired
    private RedisService redisService;
    @Autowired
    private EsService esService;
    @Resource
    private MediaSummaryMapper mediaSummaryMapper;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        long begin = System.currentTimeMillis();
        // 分片参数
        ShardingUtil.ShardingVO shardingVO = ShardingUtil.getShardingVo();
        XxlJobLogger.log("分片参数: 当前分片序号={}, 总分片数={}", shardingVO.getIndex(), shardingVO.getTotal());

        saveMediaSummary(shardingVO);

        XxlJobLogger.log("任务完成：共耗时 {}秒", (System.currentTimeMillis() - begin) / 1000);
        return SUCCESS;
    }

    /**
     * 统计媒资热度并保存
     *
     * @param shardingVO
     */
    private void saveMediaSummary(ShardingUtil.ShardingVO shardingVO) {
        int index = 0;
        int size = 100;
        while (true) {
            // 利用页数分片
            if (shardingVO.getTotal() > 1 && (index + 1) % shardingVO.getTotal() != shardingVO.getIndex()) {
                index++;
                continue;
            }
            LocalDate yesterday = LocalDate.now();
            //LocalDate yesterday = LocalDate.now().minusDays(1L);
            Set<String> set = redisService.getZSet(String.format(RedisConstant.DAY_ACTIVE_MEDIA_IDS, DateUtil.format(yesterday, "yyyyMMdd")), index, size);
            if (!CollectionUtils.isEmpty(set)) {
                SearchResponse searchResponse = searchMediaSummary(set, yesterday);
                if (searchResponse != null && searchResponse.getAggregations() != null) {
                    Terms terms1 = searchResponse.getAggregations().get("terms1");
                    terms1.getBuckets().forEach(u -> {
                        MediaSummaryDO entity = new MediaSummaryDO();
                        entity.setMediaId(Integer.parseInt(u.getKeyAsString()));
                        entity.setPlayCountDeadline(yesterday.plusDays(30).atTime(LocalTime.of(23, 59, 59)));
                        entity.setSearchCountDeadline(yesterday.plusDays(30).atTime(LocalTime.of(23, 59, 59)));

                        Terms terms2 = u.getAggregations().get("terms2");
                        terms2.getBuckets().forEach(e -> {
                            Filter filter1 = e.getAggregations().get("filter1");
                            if ("RECOMMEND_LOCATION_CLICK".equals(e.getKeyAsString())) {
                                entity.setPlayCount(Long.valueOf(filter1.getDocCount()).intValue());
                                entity.setPlayTotalCount(Long.valueOf(e.getDocCount()).intValue());
                            } else if ("SEARCH_CLICK".equals(e.getKeyAsString())) {
                                entity.setSearchCount(Long.valueOf(filter1.getDocCount()).intValue());
                                entity.setSearchTotalCount(Long.valueOf(e.getDocCount()).intValue());
                            }
                        });
                        int count = mediaSummaryMapper.selectCount(new LambdaQueryWrapper<MediaSummaryDO>().eq(MediaSummaryDO::getMediaId, entity.getMediaId()));
                        if (count == 0) {
                            mediaSummaryMapper.insert(entity);
                        } else {
                            mediaSummaryMapper.update(entity, new LambdaUpdateWrapper<MediaSummaryDO>().eq(MediaSummaryDO::getMediaId, entity.getMediaId()));
                        }
                    });
                }
            }
            // 数量不足退出
            if (set.size() < size) {
                break;
            }
            XxlJobLogger.log("当前进度 : index={}, size={}", index, size);
            index++;
        }
    }

    /**
     * 统计媒资点击、点播次数
     *
     * @param set
     * @param day
     * @return
     */
    private SearchResponse searchMediaSummary(Set<String> set, LocalDate day) {
        // String转Integer
        Set<Integer> longSet = set.stream().map(Integer::parseInt).collect(Collectors.toSet());

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must().add(QueryBuilders.termsQuery("media_id", longSet));

        BoolQueryBuilder should = QueryBuilders.boolQuery();
        should.should().add(QueryBuilders.existsQuery("RECOMMEND_LOCATION_CLICK"));
        should.should().add(QueryBuilders.existsQuery("SEARCH_CLICK"));
        queryBuilder.must().add(should);

        AggregationBuilder mediaBucket = AggregationBuilders.terms("terms1").field("media_id").size(300);
        AggregationBuilder eventBucket = AggregationBuilders.terms("terms2").field("event_type").size(100);
        AggregationBuilder filter = AggregationBuilders.filter("filter1", QueryBuilders.rangeQuery("log_time").gte(day).lt(day.plusDays(1L)));
        eventBucket.subAggregation(filter);
        mediaBucket.subAggregation(eventBucket);

        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.aggregation(mediaBucket);
        searchSourceBuilder.size(0);
        log.info(searchSourceBuilder.toString());

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(ElasticSearchConstant.log_index);
        //searchRequest.indices(ElasticSearchConstant.log_index+"_"+day.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        searchRequest.types(ElasticSearchConstant.type_default);
        searchRequest.source(searchSourceBuilder);

        try {
            return esService.getClient().search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("search media summary error", e);
        }
        return null;
    }
}
