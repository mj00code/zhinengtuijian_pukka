package com.ubo.iptv.job.handler;

import com.alibaba.fastjson.JSONArray;
import com.ubo.iptv.core.constant.ElasticSearchConstant;
import com.ubo.iptv.core.constant.RedisConstant;
import com.ubo.iptv.core.util.DateUtil;
import com.ubo.iptv.entity.gdgd.recommend.MediaKindTopDTO;
import com.ubo.iptv.entity.gdgd.recommend.MediaTypeTopDTO;
import com.ubo.iptv.job.service.EsService;
import com.ubo.iptv.job.service.RedisService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.util.ShardingUtil;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: xuning
 * @Date: 2020-11-06
 */
@Service
@JobHandler(value = "userRealTimeFavourJobHandler")
@Slf4j
public class UserRealTimeFavourJobHandler extends IJobHandler {

    @Autowired
    private RedisService redisService;
    @Autowired
    private EsService esService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        long begin = System.currentTimeMillis();
        // 分片参数
        ShardingUtil.ShardingVO shardingVO = ShardingUtil.getShardingVo();
        XxlJobLogger.log("分片参数: 当前分片序号={}, 总分片数={}", shardingVO.getIndex(), shardingVO.getTotal());

        cacheRealTimeFavour(shardingVO);

        XxlJobLogger.log("任务完成：共耗时 {}秒", (System.currentTimeMillis() - begin) / 1000);
        return SUCCESS;
    }

    /**
     * 缓存实时用户偏好
     *
     * @param shardingVO
     */
    private void cacheRealTimeFavour(ShardingUtil.ShardingVO shardingVO) {
        int index = 0;
        int size = 100;
        while (true) {
            // 利用页数分片
            if (shardingVO.getTotal() > 1 && (index + 1) % shardingVO.getTotal() != shardingVO.getIndex()) {
                index++;
                continue;
            }
            LocalDate today = LocalDate.now();
            Set<String> set = redisService.getZSet(String.format(RedisConstant.DAY_ACTIVE_USER_IDS, DateUtil.format(today, "yyyyMMdd")), index, size);

            Map<String, String> map = new HashMap<>(set.size());
            if (!CollectionUtils.isEmpty(set)) {
                SearchResponse searchResponse = searchUserDailySummary(set, today);
                if (searchResponse != null && searchResponse.getAggregations() != null) {
                    Terms terms1 = searchResponse.getAggregations().get("terms1");
                    terms1.getBuckets().forEach(u -> {
                        // 类型-题材集合
                        Map<Integer, List<Pair<Integer, Integer>>> typeKindMap = new HashMap<>(7);

                        Terms terms2 = u.getAggregations().get("terms2");
                        terms2.getBuckets().forEach(m -> {
                            String[] key = m.getKeyAsString().split(":");
                            Filter play = m.getAggregations().get("playFilter");
                            Filter click = m.getAggregations().get("clickFilter");

                            Integer mediaType = Integer.parseInt(key[1]);
                            Integer mediaKind = Integer.parseInt(key[0]);
                            Integer score = Long.valueOf(click.getDocCount() + play.getDocCount() * 3).intValue();

                            List<Pair<Integer, Integer>> kindList = typeKindMap.getOrDefault(mediaType, new ArrayList<>());
                            kindList.add(new Pair<>(mediaKind, score));
                            typeKindMap.put(mediaType, kindList);
                        });

                        // 按分数排序
                        List<Pair<Integer, Integer>> typeList = typeKindMap.entrySet().stream().map(m -> new Pair<>(m.getKey(), m.getValue().stream().mapToInt(Pair::getValue).sum())).collect(Collectors.toList());
                        Collections.sort(typeList, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
                        // 类型偏好
                        List<MediaTypeTopDTO> mediaTypeList = new ArrayList<>();
                        for (Pair<Integer, Integer> type : typeList) {
                            // 按分数排序
                            List<Pair<Integer, Integer>> kindList = typeKindMap.get(type.getKey());
                            Collections.sort(kindList, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
                            // 题材偏好
                            List<MediaKindTopDTO> mediaKindList = new ArrayList<>();
                            int i = 0;
                            for (Pair<Integer, Integer> kind : kindList) {
                                if (++i > 3) {
                                    break;
                                }
                                MediaKindTopDTO mediaKind = new MediaKindTopDTO();
                                mediaKind.setMediaKind(kind.getKey());
                                mediaKind.setScore(new BigDecimal(kind.getValue()));
                                mediaKindList.add(0, mediaKind);
                            }

                            MediaTypeTopDTO mediaType = new MediaTypeTopDTO();
                            mediaType.setMediaType(type.getKey());
                            mediaType.setScore(new BigDecimal(type.getValue()));
                            mediaType.setMediaKindList(mediaKindList);
                            mediaTypeList.add(0, mediaType);
                        }

                        // 组装redisMap集合
                        Integer userId = Integer.parseInt(u.getKeyAsString());
                        map.put(String.format(RedisConstant.MEDIA_TYPE_REAL_TIME_TOP, userId), JSONArray.toJSONString(mediaTypeList));
                    });
                }
            }
            // 同步至redis
            boolean redisResponse = redisService.set(map, 864000L);
            if (!redisResponse) {
                log.error("Redis set error: index={}, size={}", index, size);
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
     * 统计用户媒资类型、题材点击、点播次数
     *
     * @param set
     * @param day
     * @return
     */
    private SearchResponse searchUserDailySummary(Set<String> set, LocalDate day) {
        // String转Integer
        Set<Integer> longSet = set.stream().map(Integer::parseInt).collect(Collectors.toSet());

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must().add(QueryBuilders.termsQuery("user_db_id", longSet));
        queryBuilder.must().add(QueryBuilders.rangeQuery("log_time").gte(day).lt(day.plusDays(1L)));


        BoolQueryBuilder should = QueryBuilders.boolQuery();
        should.should().add(QueryBuilders.existsQuery("RECOMMEND_LOCATION_CLICK"));
        should.should().add(QueryBuilders.existsQuery("DETAIL_2_PALY_PAGE"));
        queryBuilder.must().add(should);


        AggregationBuilder userBucket = AggregationBuilders.terms("terms1").field("user_db_id");
        AggregationBuilder tkBucket = AggregationBuilders.terms("terms2").script(new Script("List y=new ArrayList();for (int i = 0; i < doc['media_kind_id'].length; ++i) {y.add( doc['media_kind_id'][i]+':'+doc['media_type'].value) } return y;"));
        AggregationBuilder playFilter = AggregationBuilders.filter("playFilter", QueryBuilders.existsQuery("DETAIL_2_PALY_PAGE"));
        AggregationBuilder clickFilter = AggregationBuilders.filter("clickFilter", QueryBuilders.existsQuery("RECOMMEND_LOCATION_CLICK"));
        tkBucket.subAggregation(playFilter);
        tkBucket.subAggregation(clickFilter);
        userBucket.subAggregation(tkBucket);

        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.aggregation(userBucket);
        log.info(searchSourceBuilder.toString());

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(ElasticSearchConstant.rt_log_index);
        searchRequest.types(ElasticSearchConstant.type_default);
        searchRequest.source(searchSourceBuilder);

        try {
            return esService.getClient().search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("search user daily summary error", e);
        }
        return null;
    }
}
