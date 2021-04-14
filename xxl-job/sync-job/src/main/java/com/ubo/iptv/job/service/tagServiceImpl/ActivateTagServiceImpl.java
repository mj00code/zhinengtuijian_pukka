package com.ubo.iptv.job.service.tagServiceImpl;

import com.ubo.iptv.core.constant.ElasticSearchConstant;
import com.ubo.iptv.core.enums.EventTypeEnum;
import com.ubo.iptv.job.service.TagMarkService;
import com.ubo.iptv.mybatis.gsgd.entity.GSGsydUserDO;
import com.ubo.iptv.mybatis.recommend.entity.TagDO;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ActivateTagServiceImpl implements TagMarkService {
    @Autowired
    private RestHighLevelClient client;

    @Override
    public void mark(Map<String, List<TagDO>> map, GSGsydUserDO user, List<TagDO> userTags) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must().add(QueryBuilders.termQuery("user_db_id", user.getId()));
        boolQueryBuilder.must().add(QueryBuilders.rangeQuery("create_time").gte("now-7d"));


        BoolQueryBuilder openTv = QueryBuilders.boolQuery();
        openTv.must().add(QueryBuilders.termQuery("event_type", EventTypeEnum.ON_OFF));
        openTv.must().add(QueryBuilders.termQuery("action", "0"));


        TermsQueryBuilder watchTv = QueryBuilders.termsQuery("event_type", EventTypeEnum.TV_PLAY_HEART, EventTypeEnum.VOD_PLAY_HEART);

        BoolQueryBuilder child = QueryBuilders.boolQuery();
        child.should(openTv);
        child.should(watchTv);
        boolQueryBuilder.must(child);
        TermsAggregationBuilder builder = AggregationBuilders.terms("events").field("event_type")
                .subAggregation(AggregationBuilders.terms("hour").script(new Script("doc['create_time'].value.hourOfDay")).size(24));


        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(0);
        searchSourceBuilder.aggregation(builder);

        SearchRequest request = new SearchRequest();
        request.source(searchSourceBuilder);
        request.indices(ElasticSearchConstant.log_index);
        request.types(ElasticSearchConstant.type_default);


        try {
            int[] openTvCounts = new int[24];
            int[] watchTvCounts = new int[24];
            int[] watchVodCounts = new int[24];


            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            Terms terms = response.getAggregations().get("events");

            terms.getBuckets().forEach(e -> {
                Terms terms2 = e.getAggregations().get("hour");
                String key = e.getKeyAsString();
                terms2.getBuckets().forEach(h -> {
                    int index = Integer.parseInt(h.getKeyAsString());
                    if (key.equals(EventTypeEnum.ON_OFF.name())) {
                        openTvCounts[index] = Long.valueOf(h.getDocCount()).intValue();
                    } else if (key.equals(EventTypeEnum.TV_PLAY_HEART.name())) {
                        watchTvCounts[index] = Long.valueOf(h.getDocCount()).intValue();
                    } else if (key.equals(EventTypeEnum.VOD_PLAY_HEART.name())) {
                        watchVodCounts[index] = Long.valueOf(h.getDocCount()).intValue();
                    }
                });
            });


            //处理活跃时间段标签
            List<TagDO> list = map.get("活跃时间段");
            TagDO tag = list.get(0);
            int max = 0;
            for (TagDO d : list) {
                int start = d.getRangeFrom().intValue();
                int end = d.getRangeTo().intValue();
                int add = 0;
                for (int i = start; i < end; i++) {
                    add += openTvCounts[i];
                }
                if (add > max) {
                    tag = d;
                    max = add;
                }
            }
            if (max >= 3) {
                userTags.add(tag);
            }

            //处理观看时长集中时间段标签
            list = map.get("观看时长集中时间段");
            tag = list.get(0);
            max = 0;
            for (TagDO d : list) {
                int start = d.getRangeFrom().intValue();
                int end = d.getRangeTo().intValue();
                int add = 0;
                for (int i = start; i < end; i++) {
                    add += watchTvCounts[i];
                    add += watchVodCounts[i];
                }
                if (add > max) {
                    tag = d;
                    max = add;
                }
            }
            if (max >= (120 / 5)) {
                userTags.add(tag);
            }


            //处理活跃用户标签
            list = map.get("活跃等级");
            Map<String, TagDO> levelMap = new HashMap<>();
            list.forEach(t -> {
                levelMap.put(t.getValue(), t);
            });
            int totalOpen = sum(openTvCounts);
            int totalWatch = sum(watchTvCounts) + sum(watchVodCounts);
            //300分钟  5分钟一次心跳
            if (totalOpen >= 10 && totalWatch >= (300 / 5)) {
                userTags.add(levelMap.get("高活跃用户"));
            } else if (totalOpen >= 5 && totalOpen < 10 && totalWatch >= (150 / 5)) {
                userTags.add(levelMap.get("中活跃用户"));
            }else if (totalOpen >= 0 && totalOpen < 5 && totalWatch >= (30 / 5)) {
                userTags.add(levelMap.get("低活跃用户"));
            } else if (totalOpen == 0 && totalWatch == 0) {
                userTags.add(levelMap.get("沉默用户"));
            } else {
                log.info("用户{} 开机次数:{} 观看心跳次数:{}", user.getId(), totalOpen, totalWatch);
            }


        } catch (IOException e) {
            log.error("查询活跃时间段失败", e);
        }
    }


    private int sum(int[] arr) {
        int sum = 0;
        if (arr.length > 0) {
            for (int a : arr) {
                sum += a;
            }
        }
        return sum;
    }


}
