package com.ubo.iptv.manage.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ubo.iptv.core.constant.ElasticSearchConstant;
import com.ubo.iptv.core.enums.EventTypeEnum;
import com.ubo.iptv.core.enums.SysEnum;
import com.ubo.iptv.core.util.DateUtil;
import com.ubo.iptv.manage.entity.OrderedUser;
import com.ubo.iptv.manage.requset.TagCondition;
import com.ubo.iptv.manage.requset.TagConditionDTO;
import com.ubo.iptv.manage.response.AbstractTagSummeryVO;
import com.ubo.iptv.manage.response.DailyDashBoardVO;
import com.ubo.iptv.manage.response.TagMediaSummaryVO;
import com.ubo.iptv.manage.response.TagUserSummaryVO;
import com.ubo.iptv.manage.service.EsReportService;
import com.ubo.iptv.mybatis.recommend.entity.TagDO;
import com.ubo.iptv.mybatis.recommend.entity.TagGroupsDO;
import com.ubo.iptv.mybatis.recommend.mapper.TagGroupsMapper;
import com.ubo.iptv.mybatis.recommend.mapper.TagMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.collect.Tuple;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ExtendedBounds;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHits;
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class EsReportServiceImpl implements EsReportService {
    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private TagGroupsMapper tagGroupsMapper;
    @Autowired
    private TagMapper tagMapper;

    private static final long HEART_INTERVAL_SECOND = 5 * 60;

    @Override
    public Tuple<Number, List<Number>> findOnlineUserPartionByHour(String index, LocalDateTime startTime, LocalDateTime endTime) {
        BoolQueryBuilder turnOff = QueryBuilders.boolQuery();
        turnOff.must(QueryBuilders.termQuery("action_type", "on_off"));
        turnOff.must(QueryBuilders.termQuery("action", "1"));

        BoolQueryBuilder activeQuery = QueryBuilders.boolQuery();
        activeQuery.mustNot(turnOff);


        String start = DateUtil.format(startTime);
        String end = DateUtil.format(endTime);
        activeQuery.must(QueryBuilders.rangeQuery("create_time").gte(start).lte(end));


        DateHistogramAggregationBuilder dateHistogramBuilder = AggregationBuilders.dateHistogram("hours");
        dateHistogramBuilder.minDocCount(0);
        dateHistogramBuilder.dateHistogramInterval(DateHistogramInterval.HOUR);
        dateHistogramBuilder.extendedBounds(new ExtendedBounds(start, end));
        dateHistogramBuilder.field("create_time");
        dateHistogramBuilder.format("yyyy-MM-dd HH:mm:ss");
        dateHistogramBuilder.subAggregation(AggregationBuilders.cardinality("distinctUser").field("user_id"));

        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        searchSourceBuilder.query(activeQuery);
        searchSourceBuilder.size(0);
        searchSourceBuilder.aggregation(dateHistogramBuilder);
        searchSourceBuilder.aggregation(AggregationBuilders.cardinality("sumUser").field("user_id"));
        SearchRequest request = new SearchRequest();
        request.indices(index);
        request.types(ElasticSearchConstant.type_default);
        request.source(searchSourceBuilder);
        try {
            log.info("查询实时在线人数");
            log.debug(searchSourceBuilder.toString());
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            ParsedDateHistogram dateHistogram = response.getAggregations().get("hours");
            List<Number> list = new ArrayList<>();
            dateHistogram.getBuckets().forEach(
                    b -> {
                        Cardinality user = b.getAggregations().get("distinctUser");
                        list.add(Long.valueOf(user.getValue()).intValue());
                    }
            );
            Cardinality sumUser = response.getAggregations().get("sumUser");
            return Tuple.tuple(sumUser.getValue(), list);
        } catch (IOException e) {
            log.error("查询实时在线人数失败\r\n" + searchSourceBuilder.toString(), e);
        }
        return null;
    }

    @Override
    public Tuple<Number, List<Number>> findPlayUserPartionByHour(String index, String eventType, LocalDateTime startTime, LocalDateTime endTime) {
        String start = DateUtil.format(startTime);
        String end = DateUtil.format(endTime);

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("event_type", eventType));
        boolQueryBuilder.must(QueryBuilders.rangeQuery("create_time").gte(start).lte(end));


        DateHistogramAggregationBuilder dateHistogramBuilder = AggregationBuilders.dateHistogram("hours");
        dateHistogramBuilder.minDocCount(0);
        dateHistogramBuilder.dateHistogramInterval(DateHistogramInterval.HOUR);
        dateHistogramBuilder.extendedBounds(new ExtendedBounds(start, end));
        dateHistogramBuilder.field("create_time");
        dateHistogramBuilder.format("yyyy-MM-dd HH:mm:ss");
        dateHistogramBuilder.subAggregation(AggregationBuilders.cardinality("distinctUser").field("user_id"));

        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(0);
        searchSourceBuilder.aggregation(dateHistogramBuilder);
        searchSourceBuilder.aggregation(AggregationBuilders.cardinality("sumUser").field("user_id"));
        SearchRequest request = new SearchRequest();
        request.indices(index);
        request.types(ElasticSearchConstant.type_default);
        request.source(searchSourceBuilder);
        try {
            log.info("查询实时播放人数:" + eventType);
            log.debug(searchSourceBuilder.toString());
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            ParsedDateHistogram dateHistogram = response.getAggregations().get("hours");
            List<Number> list = new ArrayList<>();
            dateHistogram.getBuckets().forEach(
                    b -> {
                        Cardinality user = b.getAggregations().get("distinctUser");
                        list.add(Long.valueOf(user.getValue()).intValue());
                    }
            );
            Cardinality sumUser = response.getAggregations().get("sumUser");
            return Tuple.tuple(sumUser.getValue(), list);
        } catch (IOException e) {
            log.error("查询实时播放人数失败:" + eventType + "\r\n" + searchSourceBuilder.toString(), e);
        }
        return null;
    }

    @Override
    public SearchResponse findOrderDataPartionByHour(String index, LocalDateTime startTime, LocalDateTime endTime) {
        String start = DateUtil.format(startTime);
        String end = DateUtil.format(endTime);

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("event_type", EventTypeEnum.ORDER.name()));
        boolQueryBuilder.must(QueryBuilders.termQuery("pay_status", "5"));
        boolQueryBuilder.must(QueryBuilders.rangeQuery("create_time").gte(start).lte(end));


        DateHistogramAggregationBuilder dateHistogramBuilder = AggregationBuilders.dateHistogram("hours");
        dateHistogramBuilder.minDocCount(0);
        dateHistogramBuilder.dateHistogramInterval(DateHistogramInterval.HOUR);
        dateHistogramBuilder.extendedBounds(new ExtendedBounds(start, end));
        dateHistogramBuilder.field("create_time");
        dateHistogramBuilder.format("yyyy-MM-dd HH:mm:ss");
        dateHistogramBuilder.subAggregation(AggregationBuilders.cardinality("userCount").field("user_id"));
        dateHistogramBuilder.subAggregation(AggregationBuilders.sum("sumPayAmout").field("product_price"));

        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(0);
        searchSourceBuilder.aggregation(dateHistogramBuilder);
        searchSourceBuilder.aggregation(AggregationBuilders.cardinality("sumUser").field("user_id"));
        SearchRequest request = new SearchRequest();
        request.indices(index);
        request.types(ElasticSearchConstant.type_default);
        request.source(searchSourceBuilder);
        try {
            log.info("查询实时订单数据");
            log.debug(searchSourceBuilder.toString());
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            return response;
        } catch (IOException e) {
            log.error("查询实时订单数据失败\r\n" + searchSourceBuilder.toString(), e);
        }
        return null;
    }

    @Override
    public SearchResponse intervalOrderData(LocalDate start, LocalDate end) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("event_type", EventTypeEnum.ORDER.name()));
        boolQueryBuilder.must(QueryBuilders.termQuery("pay_status", "5"));
        boolQueryBuilder.must(QueryBuilders.rangeQuery("create_time").gte(start).lte(end));


        DateHistogramAggregationBuilder dateHistogramBuilder = AggregationBuilders.dateHistogram("days");
        dateHistogramBuilder.minDocCount(0);
        dateHistogramBuilder.dateHistogramInterval(DateHistogramInterval.DAY);
        dateHistogramBuilder.extendedBounds(new ExtendedBounds(start.toString(), end.toString()));
        dateHistogramBuilder.field("create_time");
        dateHistogramBuilder.format("yyyy-MM-dd");
        dateHistogramBuilder.subAggregation(AggregationBuilders.sum("payAmount").field("product_price"));
        dateHistogramBuilder.subAggregation(AggregationBuilders.cardinality("userCount").field("user_id"));

        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(0);
        searchSourceBuilder.aggregation(dateHistogramBuilder);
        searchSourceBuilder.aggregation(AggregationBuilders.cardinality("sumUser").field("user_id"));
        SearchRequest request = new SearchRequest();
        request.indices(ElasticSearchConstant.rt_log_index);
        request.types(ElasticSearchConstant.type_default);
        request.source(searchSourceBuilder);
        try {
            log.info("查询订单数据");
            log.debug(searchSourceBuilder.toString());
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            return response;
        } catch (IOException e) {
            log.error("查询订单数据失败\r\n" + searchSourceBuilder.toString(), e);
        }
        return null;
    }


    @Override
    public List<OrderedUser> intervalOrderedUserIds(LocalDate start, LocalDate end) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("event_type", EventTypeEnum.ORDER.name()));
        boolQueryBuilder.must(QueryBuilders.termQuery("pay_status", "5"));
        boolQueryBuilder.must(QueryBuilders.rangeQuery("create_time").gte(start.minusDays(90)).lte(end));

        TermsAggregationBuilder userTerms = AggregationBuilders.terms("userList").field("user_id").size(Integer.MAX_VALUE);
        userTerms.subAggregation(AggregationBuilders.max("max").field("log_time"));
        long startMs = start.atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        Map<String, String> pathMap = new HashMap<>();
        pathMap.put("max", "max");
        userTerms.subAggregation(PipelineAggregatorBuilders.bucketSelector("script", pathMap, new Script("params.max>=" + startMs + "L")));
        userTerms.subAggregation(AggregationBuilders.topHits("orderList").sort("log_time", SortOrder.DESC).size(2).fetchSource(new String[]{"create_time", "user_id"}, new String[]{}));


        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(0);
        searchSourceBuilder.aggregation(userTerms);
        SearchRequest request = new SearchRequest();
        request.indices(ElasticSearchConstant.rt_log_index);
        request.types(ElasticSearchConstant.type_default);
        request.source(searchSourceBuilder);

        List<OrderedUser> list = new ArrayList<>();

        try {
            log.info("查询新增下单用户");
            log.debug(searchSourceBuilder.toString());
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            Terms userList = response.getAggregations().get("userList");
            userList.getBuckets().forEach(u -> {
                TopHits orderList = u.getAggregations().get("orderList");
                if (orderList.getHits().getTotalHits() == 1) {
                    SearchHit hit = orderList.getHits().getHits()[0];
                    OrderedUser user = new OrderedUser(hit.getSourceAsString());
                    list.add(user);
                } else {
                    OrderedUser first = new OrderedUser(orderList.getHits().getHits()[0].getSourceAsString());
                    OrderedUser second = new OrderedUser(orderList.getHits().getHits()[1].getSourceAsString());
                    Duration duration = Duration.between(second.getDate(), first.getDate());
                    if (duration.toDays() > 90) {
                        list.add(first);
                    }
                }
            });
        } catch (IOException e) {
            log.error(String.format("查询新增下单用户\r\n%s", searchSourceBuilder.toString()), e);
        }
        return list;
    }

    @Override
    public SearchResponse dailyProductType(LocalDate start, LocalDate end) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("event_type", EventTypeEnum.ORDER.name()));
        boolQueryBuilder.must(QueryBuilders.termQuery("pay_status", "5"));
        boolQueryBuilder.must(QueryBuilders.rangeQuery("create_time").gte(start).lte(end));


        TermsAggregationBuilder productTypeBuilder = AggregationBuilders.terms("productType").field("product_type").size(100);

        TermsAggregationBuilder productNameBuilder = AggregationBuilders.terms("productName").field("product_name").size(10);
        DateHistogramAggregationBuilder dateHistogramBuilder = AggregationBuilders.dateHistogram("days");
        dateHistogramBuilder.minDocCount(0);
        dateHistogramBuilder.dateHistogramInterval(DateHistogramInterval.DAY);
        dateHistogramBuilder.extendedBounds(new ExtendedBounds(start.toString(), end.toString()));
        dateHistogramBuilder.field("create_time");
        dateHistogramBuilder.format("yyyy-MM-dd");
        dateHistogramBuilder.order(BucketOrder.key(false));
        productNameBuilder.subAggregation(dateHistogramBuilder);


        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(0);
        searchSourceBuilder.aggregation(productTypeBuilder);
        searchSourceBuilder.aggregation(productNameBuilder);
        SearchRequest request = new SearchRequest();
        request.indices(ElasticSearchConstant.rt_log_index);
        request.types(ElasticSearchConstant.type_default);
        request.source(searchSourceBuilder);

        try {
            log.info("查询产品类型");
            log.debug(searchSourceBuilder.toString());
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            return response;
        } catch (IOException e) {
            log.error(String.format("查询产品类型\r\n%s", searchSourceBuilder.toString()), e);
        }
        return null;
    }

    @Override
    public int viewCount(LocalDate start, LocalDate end) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        int[] scenid = new int[]{14, 15, 16};
        boolQueryBuilder.must(QueryBuilders.termsQuery("scene_id", scenid));
        boolQueryBuilder.must(QueryBuilders.rangeQuery("create_time").gte(start).lte(end));


        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(0);
        searchSourceBuilder.aggregation(AggregationBuilders.cardinality("userCount").field("user_id"));
        SearchRequest request = new SearchRequest();
        request.indices(ElasticSearchConstant.rt_log_index);
        request.types(ElasticSearchConstant.type_default);
        request.source(searchSourceBuilder);
        try {
            log.info("进入详情页人数");
            log.debug(searchSourceBuilder.toString());
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            Cardinality cardinality = response.getAggregations().get("userCount");
            return Long.valueOf(cardinality.getValue()).intValue();
        } catch (IOException e) {
            log.error(String.format("进入详情页次数失败\r\n%s", searchSourceBuilder.toString()), e);
        }
        return 0;
    }

    @Override
    public int openCount(LocalDate start, LocalDate end) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("event_type", EventTypeEnum.ON_OFF.name()));
        boolQueryBuilder.must(QueryBuilders.termQuery("action", "0"));
        boolQueryBuilder.must(QueryBuilders.rangeQuery("create_time").gte(start).lte(end));


        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(0);
        searchSourceBuilder.aggregation(AggregationBuilders.cardinality("userCount").field("user_id"));
        SearchRequest request = new SearchRequest();
        request.indices(ElasticSearchConstant.rt_log_index);
        request.types(ElasticSearchConstant.type_default);
        request.source(searchSourceBuilder);
        try {
            log.info("开机次数查询");
            log.debug(searchSourceBuilder.toString());
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            Cardinality cardinality = response.getAggregations().get("userCount");
            return Long.valueOf(cardinality.getValue()).intValue();
        } catch (IOException e) {
            log.error(String.format("开机次数查询失败\r\n%s", searchSourceBuilder.toString()), e);
        }
        return 0;
    }

    @Override
    public DailyDashBoardVO vodAvgTimes(LocalDate start, LocalDate end) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("event_type", EventTypeEnum.VOD_PLAY_HEART.name()));
        boolQueryBuilder.must(QueryBuilders.rangeQuery("create_time").gte(start).lte(end));
        boolQueryBuilder.must(QueryBuilders.scriptQuery(new Script("doc.log_time.value.getMillis()-doc.start_time.value.getMillis()*1000>0")));


        DateHistogramAggregationBuilder dateHistogramBuilder = AggregationBuilders.dateHistogram("days");
        dateHistogramBuilder.minDocCount(0);
        dateHistogramBuilder.dateHistogramInterval(DateHistogramInterval.DAY);
        dateHistogramBuilder.extendedBounds(new ExtendedBounds(start.toString(), end.toString()));
        dateHistogramBuilder.field("create_time");
        dateHistogramBuilder.format("yyyy-MM-dd");
        dateHistogramBuilder.subAggregation(AggregationBuilders.cardinality("distinct_user").field("user_id"));


        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.aggregation(dateHistogramBuilder);
        searchSourceBuilder.size(0);
        SearchRequest request = new SearchRequest();
        request.indices(ElasticSearchConstant.rt_log_index);
        request.types(ElasticSearchConstant.type_default);
        request.source(searchSourceBuilder);

        try {
            log.info("查询平均点播时长");
            log.debug(searchSourceBuilder.toString());
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            ParsedDateHistogram dateHistogram = response.getAggregations().get("days");


            DailyDashBoardVO vo = new DailyDashBoardVO();
            List<String> dates = new ArrayList<>();
            List<BigDecimal> values = new ArrayList<>();
            dateHistogram.getBuckets().forEach(d -> {
                dates.add(d.getKeyAsString());
                long counts = d.getDocCount();


                Cardinality cardinality = d.getAggregations().get("distinct_user");
                long uv = cardinality.getValue();
                if (uv > 0) {
                    BigDecimal avgSecond = new BigDecimal(counts * 300).divide(new BigDecimal(uv), 0, BigDecimal.ROUND_DOWN);
                    BigDecimal avgHour = avgSecond.divide(new BigDecimal("3600"), 2, BigDecimal.ROUND_DOWN);
                    values.add(avgHour);
                } else {
                    values.add(BigDecimal.ZERO);
                }

            });
            if (values.size() > 0) {
                BigDecimal avg = new BigDecimal(values.stream().mapToDouble(d -> d.doubleValue()).average().getAsDouble()).setScale(2, BigDecimal.ROUND_DOWN);
                vo.setSum(avg);
            }
            vo.setKeys(dates);
            vo.setValues(values);
            return vo;
        } catch (IOException e) {
            log.error(String.format("查询平均点播时长失败:\r\n%s", searchSourceBuilder.toString()), e);
        }

        return null;
    }

    @Override
    public DailyDashBoardVO vodUserCounts(LocalDate start, LocalDate end) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("event_type", EventTypeEnum.VOD_PLAY_HEART.name()));
        boolQueryBuilder.must(QueryBuilders.rangeQuery("create_time").gte(start).lte(end));


        DateHistogramAggregationBuilder dateHistogramBuilder = AggregationBuilders.dateHistogram("days");
        dateHistogramBuilder.minDocCount(0);
        dateHistogramBuilder.dateHistogramInterval(DateHistogramInterval.DAY);
        dateHistogramBuilder.extendedBounds(new ExtendedBounds(start.toString(), end.toString()));
        dateHistogramBuilder.field("create_time");
        dateHistogramBuilder.format("yyyy-MM-dd");
        dateHistogramBuilder.subAggregation(AggregationBuilders.cardinality("distinct_user").field("user_id"));


        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.aggregation(dateHistogramBuilder);
        searchSourceBuilder.size(0);
        SearchRequest request = new SearchRequest();
        request.indices(ElasticSearchConstant.rt_log_index);
        request.types(ElasticSearchConstant.type_default);
        request.source(searchSourceBuilder);

        try {
            log.info("查询点播人数");
            log.debug(searchSourceBuilder.toString());
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            ParsedDateHistogram dateHistogram = response.getAggregations().get("days");


            DailyDashBoardVO vo = new DailyDashBoardVO();
            List<String> dates = new ArrayList<>();
            List<Integer> values = new ArrayList<>();
            dateHistogram.getBuckets().forEach(d -> {

                Cardinality cardinality = d.getAggregations().get("distinct_user");
                long uv = cardinality.getValue();
                dates.add(d.getKeyAsString());
                values.add(Long.valueOf(uv).intValue());
            });
            vo.setSum(values.stream().mapToInt(d -> d).sum());
            vo.setKeys(dates);
            vo.setValues(values);
            return vo;
        } catch (IOException e) {
            log.error(String.format("查询点播人数失败:\r\n%s", searchSourceBuilder.toString()), e);
        }

        return null;
    }

    @Override
    public DailyDashBoardVO vodEffective(LocalDate start, LocalDate end) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("event_type", EventTypeEnum.VOD_PLAY_HEART.name()));
        boolQueryBuilder.must(QueryBuilders.rangeQuery("create_time").gte(start).lte(end));


        DateHistogramAggregationBuilder dateHistogramBuilder = AggregationBuilders.dateHistogram("days");
        dateHistogramBuilder.minDocCount(0);
        dateHistogramBuilder.dateHistogramInterval(DateHistogramInterval.DAY);
        dateHistogramBuilder.extendedBounds(new ExtendedBounds(start.toString(), end.toString()));
        dateHistogramBuilder.field("create_time");
        dateHistogramBuilder.format("yyyy-MM-dd");


        CardinalityAggregationBuilder cab = AggregationBuilders.cardinality("cab1").script(new Script("doc.user_id.value+'-'+doc.media_code.value"));
        dateHistogramBuilder.subAggregation(cab);

        AggregationBuilder cab2 = AggregationBuilders.filter("filter", QueryBuilders.rangeQuery("currentplaytime").gte(600))
                .subAggregation(AggregationBuilders.cardinality("cab2").script(new Script("doc.user_id.value+'-'+doc.media_code.value")));
        dateHistogramBuilder.subAggregation(cab2);


        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.aggregation(dateHistogramBuilder);
        searchSourceBuilder.size(0);
        SearchRequest request = new SearchRequest();
        request.indices(ElasticSearchConstant.rt_log_index);
        request.types(ElasticSearchConstant.type_default);
        request.source(searchSourceBuilder);

        try {
            log.info(searchSourceBuilder.toString());
            log.debug(searchSourceBuilder.toString());
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            ParsedDateHistogram dateHistogram = response.getAggregations().get("days");


            DailyDashBoardVO vo = new DailyDashBoardVO();
            List<String> dates = new ArrayList<>();
            List<BigDecimal> values = new ArrayList<>();
            dateHistogram.getBuckets().forEach(d -> {
                dates.add(d.getKeyAsString());

                Cardinality c1 = d.getAggregations().get("cab1");
                Filter filter = d.getAggregations().get("filter");
                Cardinality c2 = filter.getAggregations().get("cab2");

                if (c2.getValue() == 0) {
                    values.add(BigDecimal.ZERO);
                } else {
                    BigDecimal a = new BigDecimal(c1.getValue());
                    BigDecimal b = new BigDecimal(c2.getValue());
                    values.add(b.divide(a, 4, BigDecimal.ROUND_DOWN).movePointRight(2));
                }

            });
            BigDecimal avg = new BigDecimal(values.stream().mapToDouble(d -> d.doubleValue()).average().getAsDouble()).setScale(2, BigDecimal.ROUND_DOWN);
            vo.setSum(avg);
            vo.setKeys(dates);
            vo.setValues(values);
            return vo;
        } catch (IOException e) {
            log.error(String.format("查询点播人数失败:\r\n%s", searchSourceBuilder.toString()), e);
        }

        return null;
    }

    @Override
    public DailyDashBoardVO vodMediaTypes(LocalDate start, LocalDate end) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("event_type", EventTypeEnum.VOD_PLAY_HEART.name()));
        boolQueryBuilder.must(QueryBuilders.rangeQuery("create_time").gte(start).lte(end));


        TermsAggregationBuilder termsQueryBuilder = AggregationBuilders.terms("mediaTypes").field("media_type_name").size(100);


        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.aggregation(termsQueryBuilder);
        searchSourceBuilder.size(0);
        SearchRequest request = new SearchRequest();
        request.indices(ElasticSearchConstant.rt_log_index);
        request.types(ElasticSearchConstant.type_default);
        request.source(searchSourceBuilder);

        try {
            log.info("查询点播媒资类型");
            log.debug(searchSourceBuilder.toString());
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            Terms terms = response.getAggregations().get("mediaTypes");

            if (terms.getBuckets().size() > 0) {
                DailyDashBoardVO vo = new DailyDashBoardVO();
                List<String> dates = new ArrayList<>();
                List<Long> values = new ArrayList<>();


                terms.getBuckets().forEach(d -> {
                    dates.add(d.getKeyAsString());
                    long wholeHour = d.getDocCount() * 5 / 60;
                    values.add(wholeHour);
                });
                vo.setKeys(dates);
                vo.setValues(values);
                return vo;
            }
        } catch (IOException e) {
            log.error(String.format("查询点播媒资类型:\r\n%s", searchSourceBuilder.toString()), e);
        }
        return null;
    }

    @Override
    public DailyDashBoardVO tvOnline(LocalDate start, LocalDate end) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("event_type", EventTypeEnum.TV_PLAY_HEART.name()));
        boolQueryBuilder.must(QueryBuilders.rangeQuery("create_time").gte(start).lte(end));


        DateHistogramAggregationBuilder dateHistogramBuilder = AggregationBuilders.dateHistogram("days");
        dateHistogramBuilder.minDocCount(0);
        dateHistogramBuilder.dateHistogramInterval(DateHistogramInterval.DAY);
        dateHistogramBuilder.extendedBounds(new ExtendedBounds(start.toString(), end.toString()));
        dateHistogramBuilder.field("create_time");
        dateHistogramBuilder.format("yyyy-MM-dd");
        dateHistogramBuilder.subAggregation(AggregationBuilders.cardinality("distinct_day").field("user_id"));


        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.aggregation(dateHistogramBuilder);
        searchSourceBuilder.size(0);
        SearchRequest request = new SearchRequest();
        request.indices(ElasticSearchConstant.rt_log_index);
        request.types(ElasticSearchConstant.type_default);
        request.source(searchSourceBuilder);

        try {
            log.info("查询直播人数");
            log.debug(searchSourceBuilder.toString());
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            DailyDashBoardVO vo = new DailyDashBoardVO();


            ParsedDateHistogram dateHistogram = response.getAggregations().get("days");
            List<String> dates = new ArrayList<>();
            List<Integer> values = new ArrayList<>();
            dateHistogram.getBuckets().forEach(d -> {
                Cardinality cardinality = d.getAggregations().get("distinct_day");
                long uv = cardinality.getValue();
                dates.add(d.getKeyAsString());
                values.add(Long.valueOf(uv).intValue());
            });
            vo.setSum(values.stream().mapToInt(Integer::intValue).sum());
            vo.setKeys(dates);
            vo.setValues(values);
            return vo;
        } catch (IOException e) {
            log.error(String.format("查询直播人数失败:\r\n%s", searchSourceBuilder.toString()), e);
        }
        return null;
    }

    @Override
    public Map<String, List<String>> tvUserTops(LocalDate start, LocalDate end) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("event_type", EventTypeEnum.TV_PLAY_HEART.name()));
        boolQueryBuilder.must(QueryBuilders.rangeQuery("create_time").gte(start).lte(end));


        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("mediaCodes").field("media_id").size(10);
        termsAggregationBuilder.subAggregation(AggregationBuilders.cardinality("uv").field("user_db_id"));


        DateHistogramAggregationBuilder dateHistogramBuilder = AggregationBuilders.dateHistogram("days");
        dateHistogramBuilder.minDocCount(0);
        dateHistogramBuilder.dateHistogramInterval(DateHistogramInterval.DAY);
        dateHistogramBuilder.extendedBounds(new ExtendedBounds(start.toString(), end.toString()));
        dateHistogramBuilder.field("create_time");
        dateHistogramBuilder.format("yyyy-MM-dd");
        dateHistogramBuilder.order(BucketOrder.key(false));
        dateHistogramBuilder.subAggregation(AggregationBuilders.cardinality("distinct_day").field("user_db_id"));
        termsAggregationBuilder.subAggregation(dateHistogramBuilder);

        termsAggregationBuilder.order(BucketOrder.aggregation("uv", false));

        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.aggregation(termsAggregationBuilder);
        searchSourceBuilder.size(0);
        SearchRequest request = new SearchRequest();
        request.indices(ElasticSearchConstant.rt_log_index);
        request.types(ElasticSearchConstant.type_default);
        request.source(searchSourceBuilder);

        try {
            log.info("查询人数top10直播");
            log.debug(searchSourceBuilder.toString());
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            Terms terms = response.getAggregations().get("mediaCodes");

            Map<String, List<String>> map = new HashMap<>();
            for (Terms.Bucket bucket : terms.getBuckets()) {
                String mediaCode = bucket.getKeyAsString();
                ParsedDateHistogram dateHistogram = bucket.getAggregations().get("days");
                List<String> list = new ArrayList<>();
                dateHistogram.getBuckets().forEach(d -> {
                    Cardinality cardinality = d.getAggregations().get("distinct_day");
                    long uv = cardinality.getValue();
                    list.add(String.valueOf(uv));
                });
                map.put(mediaCode, list);

            }
            return map;
        } catch (IOException e) {
            log.error(String.format("查询人数top10直播失败:\r\n%s", searchSourceBuilder.toString()), e);
        }
        return null;
    }

    @Override
    public Map<String, List<String>> tvTimeTops(LocalDate start, LocalDate end) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("event_type", EventTypeEnum.TV_PLAY_HEART.name()));
        boolQueryBuilder.must(QueryBuilders.rangeQuery("create_time").gte(start).lte(end));
        boolQueryBuilder.must(QueryBuilders.rangeQuery("currentplaytime").gte(300));


        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("mediaCodes").field("media_id").size(10);


        DateHistogramAggregationBuilder dateHistogramBuilder = AggregationBuilders.dateHistogram("days");
        dateHistogramBuilder.minDocCount(0);
        dateHistogramBuilder.dateHistogramInterval(DateHistogramInterval.DAY);
        dateHistogramBuilder.extendedBounds(new ExtendedBounds(start.toString(), end.toString()));
        dateHistogramBuilder.field("create_time");
        dateHistogramBuilder.format("yyyy-MM-dd");
        dateHistogramBuilder.order(BucketOrder.key(false));
        termsAggregationBuilder.subAggregation(dateHistogramBuilder);

        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.aggregation(termsAggregationBuilder);
        searchSourceBuilder.size(0);
        SearchRequest request = new SearchRequest();
        request.indices(ElasticSearchConstant.rt_log_index);
        request.types(ElasticSearchConstant.type_default);
        request.source(searchSourceBuilder);

        try {
            log.info("查询时长top10直播");
            log.debug(searchSourceBuilder.toString());
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            Terms terms = response.getAggregations().get("mediaCodes");

            Map<String, List<String>> map = new HashMap<>();
            for (Terms.Bucket bucket : terms.getBuckets()) {
                String mediaCode = bucket.getKeyAsString();
                ParsedDateHistogram dateHistogram = bucket.getAggregations().get("days");
                List<String> list = new ArrayList<>();
                dateHistogram.getBuckets().forEach(d -> {
                    long counts = d.getDocCount();
                    BigDecimal second = new BigDecimal(counts * HEART_INTERVAL_SECOND);
                    BigDecimal perHour = new BigDecimal("3600");
                    list.add(second.divide(perHour, 2, BigDecimal.ROUND_DOWN).toEngineeringString());
                });
                map.put(mediaCode, list);
            }
            return map;
        } catch (IOException e) {
            log.error(String.format("查询时长top10直播失败:\r\n%s", searchSourceBuilder.toString()), e);
        }
        return null;
    }


    @Override
    public SearchResponse recommendClick(LocalDate start, LocalDate end) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("event_type", EventTypeEnum.RECOMMEND_LOCATION_CLICK.name()));
        boolQueryBuilder.must(QueryBuilders.rangeQuery("create_time").gte(start).lte(end));
        boolQueryBuilder.must(QueryBuilders.existsQuery("scene_id"));


        TermsAggregationBuilder terms1 = AggregationBuilders.terms("mediaTypes").field("media_type_name").size(10);
        TermsAggregationBuilder terms2 = AggregationBuilders.terms("sceneNames").field("scene_name.keyword").size(20);


        DateHistogramAggregationBuilder dateHistogramBuilder = AggregationBuilders.dateHistogram("days");
        dateHistogramBuilder.minDocCount(0);
        dateHistogramBuilder.dateHistogramInterval(DateHistogramInterval.DAY);
        dateHistogramBuilder.extendedBounds(new ExtendedBounds(start.toString(), end.toString()));
        dateHistogramBuilder.field("create_time");
        dateHistogramBuilder.format("yyyy-MM-dd");
        dateHistogramBuilder.order(BucketOrder.key(false));
        terms1.subAggregation(dateHistogramBuilder);
        terms2.subAggregation(dateHistogramBuilder);

        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.aggregation(terms1);
        searchSourceBuilder.aggregation(terms2);
        searchSourceBuilder.size(0);
        SearchRequest request = new SearchRequest();
        request.indices(ElasticSearchConstant.rt_log_index);
        request.types(ElasticSearchConstant.type_default);
        request.source(searchSourceBuilder);

        try {
            log.info("查询推荐位数据");
            log.debug(searchSourceBuilder.toString());
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            return response;
        } catch (IOException e) {
            log.error(String.format("查询推荐位数据失败:\r\n%s", searchSourceBuilder.toString()), e);
        }
        return null;
    }

//    @Override
//    public Pair<String, List<TagUserSummaryVO>> searchUserGroup(TagConditionDTO dto) {
//        BoolQueryBuilder userQuery = QueryBuilders.boolQuery();
//        List<TagCondition> conditions = dto.getConditions();
//        conditions.forEach(tagCondition -> {
//            BoolQueryBuilder tagsQuery = QueryBuilders.boolQuery();
//            //查询条件 0:等于 1:不等于
//            if (0 == tagCondition.getSearchCondition()) {
//                tagsQuery.must(QueryBuilders.termQuery("tag_id", tagCondition.getTagId()));
//            } else {
//                tagsQuery.mustNot(QueryBuilders.termQuery("tag_id", tagCondition.getTagId()));
//            }
//
//            Integer andOrFlag = dto.getAndOrFlag();
//            //查询条件 0:且 1:或
//            if (0 == andOrFlag) {
//                userQuery.must(tagsQuery);
//            } else {
//                userQuery.should(tagsQuery);
//            }
//
//        });
//        String queryCondition = JSONObject.toJSONString(userQuery);
//        TermsAggregationBuilder terms1 = AggregationBuilders.terms("userId").field("user_id").size(Integer.MAX_VALUE)
//                .subAggregation(AggregationBuilders.terms("area").field("area").size(Integer.MAX_VALUE));
//
//        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
//        searchSourceBuilder.query(userQuery).size(dto.getSize());
//        searchSourceBuilder.aggregation(terms1);
//        SearchRequest request = new SearchRequest();
//        request.indices(ElasticSearchConstant.user_index);
//        request.types(ElasticSearchConstant.type_default);
//        request.source(searchSourceBuilder);
//
//        try {
//            log.info("查询用户分群列表");
//            log.debug(searchSourceBuilder.toString());
//            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
//            List<TagUserSummaryVO> list = new ArrayList<>();
//            Terms term1s = response.getAggregations().get("userId");
//            term1s.getBuckets().forEach(term -> {
//                String userId = term.getKeyAsString();
//                Terms term2s = term.getAggregations().get("area");
//                term2s.getBuckets().forEach(term2 -> {
//                    String area = term2.getKeyAsString();
//                    TagUserSummaryVO vo = new TagUserSummaryVO();
//                    vo.setUserId(Long.parseLong(userId));
//                    vo.setAddress(area);
//                    list.add(vo);
//                });
//            });
//            return new Pair<>(queryCondition, list);
//
//        } catch (IOException e) {
//            log.error("查询用户分群列表失败\r\n" + searchSourceBuilder.toString(), e);
//        }
//        return null;
//    }
//
//    @Override
//    public List<TagUserSummaryVO> searchUserGroup(String queryCondition) {
//        BoolQueryBuilder userQuery = JSONObject.parseObject(queryCondition, BoolQueryBuilder.class);
//        TermsAggregationBuilder terms1 = AggregationBuilders.terms("userId").field("user_id").size(Integer.MAX_VALUE)
//                .subAggregation(AggregationBuilders.terms("area").field("area").size(Integer.MAX_VALUE));
//
//        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
//        searchSourceBuilder.query(userQuery);
//        searchSourceBuilder.aggregation(terms1);
//        SearchRequest request = new SearchRequest();
//        request.indices(ElasticSearchConstant.user_index);
//        request.types(ElasticSearchConstant.type_default);
//        request.source(searchSourceBuilder);
//
//        try {
//            log.info("查询用户分群列表");
//            log.debug(searchSourceBuilder.toString());
//            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
//            List<TagUserSummaryVO> list = new ArrayList<>();
//            Terms term1s = response.getAggregations().get("userId");
//            term1s.getBuckets().forEach(term -> {
//                String userId = term.getKeyAsString();
//                Terms term2s = term.getAggregations().get("area");
//                term2s.getBuckets().forEach(term2 -> {
//                    String area = term2.getKeyAsString();
//                    TagUserSummaryVO vo = new TagUserSummaryVO();
//                    vo.setUserId(Long.parseLong(userId));
//                    vo.setAddress(area);
//                    list.add(vo);
//                });
//            });
//            return list;
//
//        } catch (IOException e) {
//            log.error("查询用户分群列表失败\r\n" + searchSourceBuilder.toString(), e);
//        }
//        return null;
//    }
//
//    @Override
//    public Pair<String, List<TagMediaSummaryVO>> searchMediaGroup(TagConditionDTO dto) {
//        BoolQueryBuilder userQuery = QueryBuilders.boolQuery();
//        List<TagCondition> conditions = dto.getConditions();
//        conditions.forEach(tagCondition -> {
//            BoolQueryBuilder tagsQuery = QueryBuilders.boolQuery();
//            //查询条件 0:等于 1:不等于
//            if (0 == tagCondition.getSearchCondition()) {
//                tagsQuery.must(QueryBuilders.termQuery("tag_id", tagCondition.getTagId()));
//            } else {
//                tagsQuery.mustNot(QueryBuilders.termQuery("tag_id", tagCondition.getTagId()));
//            }
//
//            Integer andOrFlag = dto.getAndOrFlag();
//            //查询条件 0:且 1:或
//            if (0 == andOrFlag) {
//                userQuery.must(tagsQuery);
//            } else {
//                userQuery.should(tagsQuery);
//            }
//
//        });
//        String queryCondition = JSONObject.toJSONString(userQuery);
//        TermsAggregationBuilder terms1 = AggregationBuilders.terms("terms1").field("media_id").size(Integer.MAX_VALUE)
//                .subAggregation(AggregationBuilders.terms("terms2").field("media_name").size(Integer.MAX_VALUE));
//
//        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
//        searchSourceBuilder.query(userQuery).size(dto.getSize());
//        searchSourceBuilder.aggregation(terms1);
//        SearchRequest request = new SearchRequest();
//        request.indices(ElasticSearchConstant.media_index);
//        request.types(ElasticSearchConstant.type_default);
//        request.source(searchSourceBuilder);
//
//        try {
//            log.info("查询媒资分群列表");
//            log.debug(searchSourceBuilder.toString());
//            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
//            response.getHits().getHits()[1].getSortValues();
//            List<TagMediaSummaryVO> list = new ArrayList<>();
//            Terms term1s = response.getAggregations().get("terms1");
//            term1s.getBuckets().forEach(term -> {
//                String mediaId = term.getKeyAsString();
//                Terms term2s = term.getAggregations().get("terms2");
//                term2s.getBuckets().forEach(term2 -> {
//                    String mediaName = term2.getKeyAsString();
//                    TagMediaSummaryVO vo = new TagMediaSummaryVO();
//                    vo.setMediaId(Long.parseLong(mediaId));
//                    vo.setMediaName(mediaName);
//                    list.add(vo);
//                });
//            });
//            return new Pair<>(queryCondition, list);
//        } catch (IOException e) {
//            log.error("查询媒资分群列表失败\r\n" + searchSourceBuilder.toString(), e);
//        }
//        return null;
//    }

    @Override
    public List<? extends AbstractTagSummeryVO> groupMatchList(TagGroupsDO tagGroupsDO, Integer size) {
        try {
            SearchRequest request = searchRequestByGroup(tagGroupsDO);
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            List<AbstractTagSummeryVO> list = new ArrayList<>();
            response.getHits().forEach(h -> {
                JSONObject jsonObject = JSONObject.parseObject(h.getSourceAsString());
                if (tagGroupsDO.getType() == 0) {
                    TagUserSummaryVO vo = new TagUserSummaryVO();
                    vo.setUserId(jsonObject.getLong("userId"));
                    vo.setAddress(jsonObject.getString("cityName"));
                    vo.setActiveTime(jsonObject.getString("activeTime"));
                    vo.setSysId(SysEnum.description(jsonObject.getString("sysId")));
                    list.add(vo);
                } else {
                    TagMediaSummaryVO vo = new TagMediaSummaryVO();
                    vo.setMediaId(jsonObject.getLong("mediaId"));
                    vo.setMediaName(jsonObject.getString("name"));
                    vo.setMediaType(jsonObject.getString("mediaTypeName"));
                    vo.setDirectors(StringUtils.isEmpty(jsonObject.getString("director")) ? "" : jsonObject.getString("director").replace("\"", "").replace("[", "").replace("]", ""));
                    vo.setActors(StringUtils.isEmpty(jsonObject.getString("actor")) ? "" : jsonObject.getString("actor").replace("\"", "").replace("[", "").replace("]", ""));
                    vo.setPublishTime(jsonObject.getString("releaseYear"));
                    vo.setIsCharge("true".equals(jsonObject.getString("free")));
                    list.add(vo);
                }
            });
            return list;
        } catch (Exception e) {
            log.error("查询标签组失败\r\n" + JSONObject.toJSONString(tagGroupsDO), e);
        }
        return new ArrayList<>();
    }

    public static void main(String[] args) {
        String s = "\"陈思思\",\"向群\",\"龚秋霞\",\"文逸民\",\"孙芷君\"";
        System.out.println(s.replace("\"", ""));
    }

    @Override
    public SearchResponse downLoadGroupInfo(Long groupId) {
        TagGroupsDO tagGroupsDO = tagGroupsMapper.selectOne(new LambdaQueryWrapper<TagGroupsDO>().eq(TagGroupsDO::getId, groupId));
        if (tagGroupsDO == null) {
            return null;
        }
        try {
            SearchRequest request = searchRequestByGroup(tagGroupsDO);
            final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
            request.scroll(scroll);
            request.source().size(10000);
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            return response;
        } catch (Exception e) {
            log.error("查询标签组失败\r\n" + JSONObject.toJSONString(tagGroupsDO), e);
        }
        return null;
    }

    @Override
    public SearchResponse downLoadGroupInfo(String scrollId) {
        SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
        scrollRequest.scroll(TimeValue.timeValueMinutes(10L));
        try {
            return client.scroll(scrollRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("es错误", e);
        }
        return null;
    }

    /**
     * 组装查询
     *
     * @param tagGroup
     * @return
     */
    private SearchSourceBuilder sourceBuilderByGroup(TagGroupsDO tagGroup) {
        SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource();

        String json = tagGroup.getQuery();
        TagConditionDTO dto = JSON.parseObject(json, TagConditionDTO.class);
        Integer flag = dto.getAndOrFlag();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        List<QueryBuilder> list = flag == 0 ? boolQuery.must() : boolQuery.should();
        List<TagCondition> conditions = dto.getConditions();

        for (TagCondition t : conditions) {
            TagDO tagDO = tagMapper.selectById(t.getTagId());
            if (tagDO == null) break;


            String field = tagDO.getField();
            QueryBuilder queryBuilder = null;
            if (StringUtils.isNotBlank(field)) {
                String value = null;
                if (StringUtils.isNotBlank(t.getInputValue())) {
                    value = t.getInputValue();
                    queryBuilder = QueryBuilders.wildcardQuery(field, "*" + value + "*");
                } else if (StringUtils.isNotBlank(tagDO.getName())) {
                    value = tagDO.getName();
                    queryBuilder = QueryBuilders.termQuery(field, value);
                }


                if (isRangeFormate(value)) {
                    queryBuilder = QueryBuilders.rangeQuery(field).gte(value.split("-")[0]).lte(value.split("-")[1]);
                } else if (value.equals("今年")) {
                    queryBuilder = QueryBuilders.termQuery(field, LocalDate.now().getYear());
                } else if (value.equals("去年")) {
                    queryBuilder = QueryBuilders.termQuery(field, LocalDate.now().minusYears(1).getYear());
                }
            } else {
                queryBuilder = QueryBuilders.termQuery("tagId", tagDO.getId());
            }

            if (queryBuilder != null) {
                //查询条件 0:等于 1:不等于
                if (0 == t.getSearchCondition()) {
                    list.add(queryBuilder);
                } else {
                    BoolQueryBuilder tagBool = QueryBuilders.boolQuery();
                    tagBool.mustNot(queryBuilder);
                    list.add(tagBool);
                }
            }
        }


        //如果是媒资,则增加过滤子集
        if (1 == tagGroup.getType()) {
            list.add(QueryBuilders.termQuery("publishStatus", 1));
        }

        sourceBuilder.query(boolQuery);
//        sourceBuilder.query(QueryBuilders.matchAllQuery());
        log.info("标签组{} 查询DSL\r\n{}", tagGroup.getId(), sourceBuilder.toString());
        return sourceBuilder;
    }

    /**
     * 目标索引
     *
     * @param tagGroup
     * @return
     */
    private SearchRequest searchRequestByGroup(TagGroupsDO tagGroup) {
        SearchSourceBuilder searchSourceBuilder = sourceBuilderByGroup(tagGroup);
        SearchRequest searchRequest = new SearchRequest();
        if (tagGroup.getType() == 0) {
            searchRequest.indices(ElasticSearchConstant.user_index);
        } else {
            searchRequest.indices(ElasticSearchConstant.media_index);
        }
        searchRequest.types(ElasticSearchConstant.type_default);
        searchRequest.source(searchSourceBuilder);
        return searchRequest;
    }


    @Override
    public SearchResponse groupMatchListWithSummary(TagGroupsDO tagGroupsDO, Set<Long> tagIds) {
        try {
            SearchRequest request = searchRequestByGroup(tagGroupsDO);
            request.source().aggregation(AggregationBuilders.filter("filter", QueryBuilders.termsQuery("tagId", tagIds))
                    .subAggregation(AggregationBuilders.terms("terms").field("tagId").size(1000)));
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            return response;
        } catch (Exception e) {
            log.error("查询标签组失败\r\n" + JSONObject.toJSONString(tagGroupsDO), e);
        }
        return null;
    }


    private static boolean isRangeFormate(String value) {
        if (StringUtils.isBlank(value))
            return false;
        Pattern pattern = Pattern.compile("\\d+-\\d+");
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();

    }
}