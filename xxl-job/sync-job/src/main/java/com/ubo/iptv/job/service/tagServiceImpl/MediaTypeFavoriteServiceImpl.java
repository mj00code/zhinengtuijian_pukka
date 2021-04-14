package com.ubo.iptv.job.service.tagServiceImpl;

import com.ubo.iptv.core.constant.ElasticSearchConstant;
import com.ubo.iptv.core.enums.EventTypeEnum;
import com.ubo.iptv.core.enums.MediaTypeEnum;
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
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MediaTypeFavoriteServiceImpl implements TagMarkService {
    @Autowired
    private RestHighLevelClient client;


    @Override
    public void mark(Map<String, List<TagDO>> map, GSGsydUserDO user, List<TagDO> userTags) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must().add(QueryBuilders.termQuery("user_db_id", user.getId()));
        boolQueryBuilder.must().add(QueryBuilders.rangeQuery("create_time").gte("now-30d"));
        boolQueryBuilder.must().add(QueryBuilders.termsQuery("event_type", EventTypeEnum.VOD_PLAY_HEART, EventTypeEnum.TV_PLAY_HEART));

        TermsAggregationBuilder termBuilder = AggregationBuilders.terms("terms").field("media_type");


        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(0);
        searchSourceBuilder.aggregation(termBuilder);


        SearchRequest request = new SearchRequest();
        request.source(searchSourceBuilder);
        request.indices(ElasticSearchConstant.log_index);
        request.types(ElasticSearchConstant.type_default);

        try {
            List<String> typeList = new ArrayList<>();
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            Terms terms = response.getAggregations().get("terms");
            terms.getBuckets().forEach(t -> {
                long count = t.getDocCount();
                if (count >= (10 * 60 / 5)) {
                    typeList.add(t.getKeyAsString());
                }
            });


            List<TagDO> list = map.get("媒资偏好特征");
            Map<String, TagDO> levelMap = new HashMap<>();
            list.forEach(t -> {
                levelMap.put(t.getValue(), t);
            });
            int size = typeList.size() < 3 ? typeList.size() : 3;
            for (int i = 0; i < size; i++) {
                MediaTypeEnum typeEnum = MediaTypeEnum.getByValue(typeList.get(i));

                TagDO userTag = null;
                switch (typeEnum) {
                    case MOVIE:
                        userTag = levelMap.get("电影迷");
                        break;
                    case SERIES:
                        userTag = levelMap.get("电视剧迷");
                        break;
//                    case COMIC:
//                        userTag = levelMap.get("综艺迷");
//                        break;
                    case CHILDREN:
                        userTag = levelMap.get("少儿迷");
                        break;
                    case COMIC:
                        userTag = levelMap.get("动漫迷");
                        break;
                    case LIVE:
                        userTag = levelMap.get("直播偏好者");
                        break;
                }
                if (userTag != null) userTags.add(userTag);

            }


        } catch (IOException e) {
            log.error("查询订单信息失败", e);
        }


    }


}
