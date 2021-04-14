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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
@Slf4j
public class OrderTagServiceImpl implements TagMarkService {
    @Autowired
    private RestHighLevelClient client;


    @Override
    public void mark(Map<String, List<TagDO>> map, GSGsydUserDO user, List<TagDO> userTags) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must().add(QueryBuilders.termQuery("user_db_id", user.getId()));
        boolQueryBuilder.must().add(QueryBuilders.rangeQuery("create_time").gte("now-30d"));
        boolQueryBuilder.must().add(QueryBuilders.termQuery("event_type", EventTypeEnum.ORDER));
        boolQueryBuilder.must().add(QueryBuilders.termQuery("payStatus", 5));

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
            long[] mediaTypeCount = new long[MediaTypeEnum.values().length];
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            long orderCount = response.getHits().totalHits;
            Terms terms = response.getAggregations().get("terms");
            terms.getBuckets().forEach(t -> {
                int mediaType = Integer.parseInt(t.getKeyAsString());
                long count = t.getDocCount();
                mediaTypeCount[mediaType] = count;
            });


//            List<TagDO> list = map.get("订购次数");
//            for (TagDO d : list) {
//                if (orderCount >= d.getRangeFrom().longValue() && orderCount <= d.getRangeTo().longValue()) {
//                    userTags.add(d);
//                }
//            }


            List<TagDO> list = map.get("订购类型");
            Map<String, TagDO> levelMap = new HashMap<>();
            list.forEach(t -> {
                levelMap.put(t.getValue(), t);
            });
            for (MediaTypeEnum type : MediaTypeEnum.values()) {
                int mediaType = type.intValue();
                long count = mediaTypeCount[mediaType];
                if (count > 0) {
                    TagDO userTag = null;
                    switch (type) {
                        case MOVIE:
                            userTag = levelMap.get("电影订购用户");
                            break;
                        case SERIES:
                            userTag = levelMap.get("电视剧订购用户");
                            break;
//                        case VARIETY:
//                            userTag = levelMap.get("综艺订购用户");
//                            break;
                        case CHILDREN:
                            userTag = levelMap.get("少儿订购用户");
                            break;
                        case COMIC:
                            userTag = levelMap.get("动漫订购用户");
                            break;
                    }
                    if (userTag != null) userTags.add(userTag);
                }
            }


        } catch (IOException e) {
            log.error("查询订单信息失败", e);
        }


    }


}
