package com.ubo.iptv.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.core.constant.ElasticSearchConstant;
import com.ubo.iptv.core.enums.EventTypeEnum;
import com.ubo.iptv.manage.entity.OrderedUser;
import com.ubo.iptv.manage.enums.ProductTypeEnum;
import com.ubo.iptv.manage.response.*;
import com.ubo.iptv.manage.service.EsReportService;
import com.ubo.iptv.manage.service.IPTVReportService;
import com.ubo.iptv.mybatis.gsgd.entity.GSChannelDO;
import com.ubo.iptv.mybatis.gsgd.entity.GSContentinfoDO;
import com.ubo.iptv.mybatis.gsgd.mapper.GSChannelMapper;
import com.ubo.iptv.mybatis.gsgd.mapper.GSContentinfoMapper;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.collect.Tuple;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class IPTVReportServiceImpl implements IPTVReportService {
    @Autowired
    private EsReportService esReportService;
    @Autowired
    private GSContentinfoMapper gsContentinfoMapper;

    @Override
    public CommonResponse<RealTimeDashBoardVO> realTimeDashBoardVO(LocalDateTime today) {
        RealTimeDashBoardVO vo = new RealTimeDashBoardVO();
        vo.setDate(today.toLocalDate().toString());
        LocalDate yesterday = today.minusDays(1).toLocalDate();

        vo.setOnline(onlineUserData(yesterday, today));
        vo.setVodPlay(vodPlayUserData(yesterday, today));
        vo.setTvPlay(tvPlayUserData(yesterday, today));

        Map<String, RealTimeDashBoardItemVO> map = orderData(yesterday, today);
        vo.setPayAmount(map.get("payAmount"));
        vo.setPayUser(map.get("user"));
        vo.setPayCounts(map.get("count"));
        return CommonResponse.success(vo);
    }

    @Override
    public CommonResponse<DailyDashBoardVO> dailyOrderAmount(String interval) {
        Tuple<LocalDate, LocalDate> tuple = getStartDate(interval, LocalDate.now());
        LocalDate start = tuple.v1();
        LocalDate end = tuple.v2();

        SearchResponse response = esReportService.intervalOrderData(start, end);
        ParsedDateHistogram dateHistogram = response.getAggregations().get("days");

        DailyDashBoardVO vo = new DailyDashBoardVO();
        List<String> dates = new ArrayList<>();
        List<Number> payAmounts = new ArrayList<>();

        dateHistogram.getBuckets().forEach(d -> {
            dates.add(d.getKeyAsString());
            Sum ds = d.getAggregations().get("payAmount");
            payAmounts.add(new BigDecimal(ds.getValue()).setScale(2, BigDecimal.ROUND_DOWN));
        });

        Double sum = payAmounts.stream().mapToDouble(Number::doubleValue).sum();

        vo.setKeys(dates);
        vo.setSum(new BigDecimal(sum).setScale(2, BigDecimal.ROUND_DOWN));
        vo.setValues(payAmounts);
        return CommonResponse.success(vo);
    }

    @Override
    public CommonResponse<DailyDashBoardVO> dailyOrderUser(String interval) {
        Tuple<LocalDate, LocalDate> tuple = getStartDate(interval, LocalDate.now());
        LocalDate start = tuple.v1();
        LocalDate end = tuple.v2();

        DailyDashBoardVO vo = new DailyDashBoardVO();
        List<String> dates = new ArrayList<>();
        List<Number> userCounts = new ArrayList<>();

        SearchResponse response = esReportService.intervalOrderData(start, end);
        ParsedDateHistogram dateHistogram = response.getAggregations().get("days");
        dateHistogram.getBuckets().forEach(d -> {
            dates.add(d.getKeyAsString());
            Cardinality userCount = d.getAggregations().get("userCount");
            userCounts.add(userCount.getValue());
        });
        Cardinality sumUser = response.getAggregations().get("sumUser");


        vo.setKeys(dates);
        vo.setSum(sumUser.getValue());
        vo.setValues(userCounts);
        return CommonResponse.success(vo);
    }

    @Override
    public CommonResponse<DailyDashBoardVO> dailyOrderCounts(String interval) {
        Tuple<LocalDate, LocalDate> tuple = getStartDate(interval, LocalDate.now());
        LocalDate start = tuple.v1();
        LocalDate end = tuple.v2();

        DailyDashBoardVO vo = new DailyDashBoardVO();
        List<String> dates = new ArrayList<>();
        List<Number> userCounts = new ArrayList<>();

        SearchResponse response = esReportService.intervalOrderData(start, end);
        ParsedDateHistogram dateHistogram = response.getAggregations().get("days");
        dateHistogram.getBuckets().forEach(d -> {
            dates.add(d.getKeyAsString());
            userCounts.add(d.getDocCount());
        });
        vo.setKeys(dates);
        vo.setSum(userCounts.stream().mapToLong(Number::longValue).sum());
        vo.setValues(userCounts);
        return CommonResponse.success(vo);
    }


    @Override
    public CommonResponse<DailyDashBoardVO> dailyAvgAmount(String interval) {
        Tuple<LocalDate, LocalDate> tuple = getStartDate(interval, LocalDate.now());
        LocalDate start = tuple.v1();
        LocalDate end = tuple.v2();

        DailyDashBoardVO vo = new DailyDashBoardVO();
        List<String> dates = new ArrayList<>();
        List<Number> avgPayAmount = new ArrayList<>();

        SearchResponse response = esReportService.intervalOrderData(start, end);
        ParsedDateHistogram dateHistogram = response.getAggregations().get("days");
        dateHistogram.getBuckets().forEach(d -> {
            dates.add(d.getKeyAsString());

            Sum payAmount = d.getAggregations().get("payAmount");
            Cardinality userCount = d.getAggregations().get("userCount");
            BigDecimal a = new BigDecimal(payAmount.getValue());
            BigDecimal b = new BigDecimal(userCount.getValue());
            if (b.compareTo(BigDecimal.ZERO) == 0) {
                avgPayAmount.add(BigDecimal.ZERO);
            } else {
                avgPayAmount.add(a.divide(b, 2, BigDecimal.ROUND_DOWN));
            }
        });
        Double sum = avgPayAmount.stream().mapToDouble(Number::doubleValue).sum();
        vo.setKeys(dates);
        vo.setSum(new BigDecimal(sum).setScale(2, BigDecimal.ROUND_DOWN));
        vo.setValues(avgPayAmount);
        return CommonResponse.success(vo);
    }

    @Override
    public CommonResponse<DailyDashBoardVO> dailyIncreaseUserCounts(String interval) {
        Tuple<LocalDate, LocalDate> tuple = getStartDate(interval, LocalDate.now());
        LocalDate start = tuple.v1();
        LocalDate end = tuple.v2();


        List<OrderedUser> users = esReportService.intervalOrderedUserIds(start, end);
        Map<LocalDate, List<OrderedUser>> map = users.stream().collect(Collectors.groupingBy(t -> t.getDate().toLocalDate()));
        DailyDashBoardVO vo = new DailyDashBoardVO();
        List<String> keys = new ArrayList<>();
        List<Number> values = new ArrayList<>();
        Integer count = 0;
        while (!start.isAfter(end)) {
            keys.add(start.toString());
            List<OrderedUser> list = map.get(start);
            int num = list == null ? 0 : list.size();
            values.add(num);
            count += num;
            start = start.plusDays(1);

        }
        vo.setSum(count);
        vo.setValues(values);
        vo.setKeys(keys);
        return CommonResponse.success(vo);
    }

    @Override
    public CommonResponse<DailyComposeDashBoardVO> orderFixCompose() {
        DailyComposeDashBoardVO vo = new DailyComposeDashBoardVO();
        Tuple<LocalDate, LocalDate> tuple = getStartDate("7d", LocalDate.now());
        LocalDate start = tuple.v1();
        LocalDate end = tuple.v2();

        SearchResponse response = esReportService.dailyProductType(start, end);
        if (response != null) {
            Terms productTypeList = response.getAggregations().get("productType");
            List<String> keys = new ArrayList<>();
            List<Number> values = new ArrayList<>();

            productTypeList.getBuckets().forEach(pt -> {
                String key = pt.getKeyAsString();
                keys.add(ProductTypeEnum.description(key));
                values.add(pt.getDocCount());
            });
            DailyDashBoardVO productTypes = new DailyDashBoardVO();
            productTypes.setKeys(keys);
            productTypes.setValues(values);
            vo.setProductTypes(productTypes);
            vo.setOrderCount(values.stream().mapToInt(t -> t.intValue()).sum());


            Terms productNamesList = response.getAggregations().get("productName");
            Map<String, List<String>> map = new HashMap<>();
            productNamesList.getBuckets().forEach(pn -> {
                List<String> list = new ArrayList<>();
                String productName = pn.getKeyAsString();
                ParsedDateHistogram dateHistogram = pn.getAggregations().get("days");
                dateHistogram.getBuckets().forEach(d -> list.add(d.getDocCount() + ""));
                map.put(productName, list);
            });
            vo.setProductNameMap(map);

        }
        vo.setOpenCount(esReportService.openCount(start, end));
        vo.setViewCount(esReportService.viewCount(start, end));

        List<String> dates = new ArrayList<>();
        while (!start.isAfter(end)) {
            dates.add(end.toString());
            end = end.minusDays(1);
        }
        vo.setDates(dates);


        vo.composeRate();
        return CommonResponse.success(vo);
    }

    private Tuple<LocalDate, LocalDate> getStartDate(String interval, LocalDate end) {
        LocalDate start = null;
        if ("week".equals(interval)) {
            start = end.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        } else if ("month".equals(interval)) {
            start = end.withDayOfMonth(1);
        } else {
            end = end.minusDays(1);
            start = end.minusDays(6);
        }
        return Tuple.tuple(start, end);
    }

    private RealTimeDashBoardItemVO onlineUserData(LocalDate yesterday, LocalDateTime today) {
        RealTimeDashBoardItemVO vo = new RealTimeDashBoardItemVO();
        Tuple<Number, List<Number>> yesterdayList = esReportService.findOnlineUserPartionByHour(ElasticSearchConstant.rt_log_index, yesterday.atStartOfDay().minusHours(1), yesterday.atTime(22, 59, 59));
        Tuple<Number, List<Number>> todayList = esReportService.findOnlineUserPartionByHour(ElasticSearchConstant.rt_log_index, today.with(LocalTime.MIN).minusHours(1), today.minusHours(1).withMinute(59).withSecond(59));

        vo.setToday(todayList.v2());
        vo.setYesterday(yesterdayList.v2());
        vo.setSum(todayList.v1());
        return vo;
    }

    private RealTimeDashBoardItemVO vodPlayUserData(LocalDate yesterday, LocalDateTime today) {
        Tuple<Number, List<Number>> todayList = esReportService.findPlayUserPartionByHour(ElasticSearchConstant.rt_log_index, EventTypeEnum.VOD_PLAY_HEART.name(), today.with(LocalTime.MIN).minusHours(1), today.minusHours(1).withMinute(59).withSecond(59));
        Tuple<Number, List<Number>> yesterdayList = esReportService.findPlayUserPartionByHour(ElasticSearchConstant.rt_log_index, EventTypeEnum.VOD_PLAY_HEART.name(), yesterday.atStartOfDay().minusHours(1), yesterday.atTime(22, 59, 59));

        RealTimeDashBoardItemVO vo = new RealTimeDashBoardItemVO();
        vo.setToday(todayList.v2());
        vo.setYesterday(yesterdayList.v2());
        vo.setSum(todayList.v1());
        return vo;
    }

    private RealTimeDashBoardItemVO tvPlayUserData(LocalDate yesterday, LocalDateTime today) {
        Tuple<Number, List<Number>> todayList = esReportService.findPlayUserPartionByHour(ElasticSearchConstant.rt_log_index, EventTypeEnum.TV_PLAY_HEART.name(), today.with(LocalTime.MIN).minusHours(1), today.minusHours(1).withMinute(59).withSecond(59));
        Tuple<Number, List<Number>> yesterdayList = esReportService.findPlayUserPartionByHour(ElasticSearchConstant.rt_log_index, EventTypeEnum.TV_PLAY_HEART.name(), yesterday.atStartOfDay().minusHours(1), yesterday.atTime(22, 59, 59));
        RealTimeDashBoardItemVO vo = new RealTimeDashBoardItemVO();
        vo.setToday(todayList.v2());
        vo.setYesterday(yesterdayList.v2());
        vo.setSum(todayList.v1());
        return vo;
    }


    private Map<String, RealTimeDashBoardItemVO> orderData(LocalDate yesterday, LocalDateTime today) {
        SearchResponse todayResponse = esReportService.findOrderDataPartionByHour(ElasticSearchConstant.rt_log_index, today.with(LocalTime.MIN).minusHours(1), today.minusHours(1).withMinute(59).withSecond(59));
        SearchResponse yesterdayResponse = esReportService.findOrderDataPartionByHour(ElasticSearchConstant.rt_log_index, yesterday.atStartOfDay().minusHours(1), yesterday.atTime(22, 59, 59));


        RealTimeDashBoardItemVO userVO = new RealTimeDashBoardItemVO();
        RealTimeDashBoardItemVO countVO = new RealTimeDashBoardItemVO();
        RealTimeDashBoardItemVO payAmountVO = new RealTimeDashBoardItemVO();


        ParsedDateHistogram dateHistogram = todayResponse.getAggregations().get("hours");
        dateHistogram.getBuckets().forEach(
                b -> {
                    userVO.getToday().add(Long.valueOf(b.getDocCount()).intValue());
                    Cardinality user = b.getAggregations().get("userCount");
                    countVO.getToday().add(Long.valueOf(user.getValue()).intValue());
                    Sum sum = b.getAggregations().get("sumPayAmout");
                    payAmountVO.getToday().add(sum.getValue());
                }
        );
        Cardinality sumUser = todayResponse.getAggregations().get("sumUser");
        userVO.setSum(sumUser.getValue());
        Double sum = payAmountVO.getToday().stream().mapToDouble(Number::doubleValue).sum();
        payAmountVO.setSum(new BigDecimal(sum).setScale(2, BigDecimal.ROUND_DOWN));
        countVO.setSum(todayResponse.getHits().totalHits);


        ParsedDateHistogram dateHistogram2 = yesterdayResponse.getAggregations().get("hours");
        dateHistogram2.getBuckets().forEach(
                b -> {
                    userVO.getYesterday().add(Long.valueOf(b.getDocCount()).intValue());
                    Cardinality user = b.getAggregations().get("userCount");
                    countVO.getYesterday().add(Long.valueOf(user.getValue()).intValue());
                    Sum sumPayAmout = b.getAggregations().get("sumPayAmout");
                    payAmountVO.getYesterday().add(sumPayAmout.getValue());
                }
        );


        Map<String, RealTimeDashBoardItemVO> map = new HashMap<>();
        map.put("user", userVO);
        map.put("count", countVO);
        map.put("payAmount", payAmountVO);
        return map;
    }

    @Override
    public CommonResponse<DailyDashBoardVO> vodAvgTimes(String interval) {
        Tuple<LocalDate, LocalDate> tuple = getStartDate(interval, LocalDate.now());
        LocalDate start = tuple.v1();
        LocalDate end = tuple.v2();

        DailyDashBoardVO vo = esReportService.vodAvgTimes(start, end);
        if (vo != null) {
            return CommonResponse.success(vo);
        } else {
            return CommonResponse.FAIL;
        }

    }

    @Override
    public CommonResponse<DailyDashBoardVO> vodUserCounts(String interval) {
        Tuple<LocalDate, LocalDate> tuple = getStartDate(interval, LocalDate.now());
        LocalDate start = tuple.v1();
        LocalDate end = tuple.v2();

        DailyDashBoardVO vo = esReportService.vodUserCounts(start, end);
        if (vo != null) {
            return CommonResponse.success(vo);
        } else {
            return CommonResponse.FAIL;
        }
    }

    @Override
    public CommonResponse<VodComposeDashBoardVO> vodFixCompose() {
        Tuple<LocalDate, LocalDate> tuple = getStartDate("7d", LocalDate.now());
        LocalDate start = tuple.v1();
        LocalDate end = tuple.v2();

        VodComposeDashBoardVO vo = new VodComposeDashBoardVO();
        vo.setEffective(esReportService.vodEffective(start, end));
        vo.setMediaTypes(esReportService.vodMediaTypes(start, end));
        return CommonResponse.success(vo);
    }

    @Override
    public CommonResponse<DailyDashBoardVO> tvOnline(String interval) {
        Tuple<LocalDate, LocalDate> tuple = getStartDate(interval, LocalDate.now());
        LocalDate start = tuple.v1();
        LocalDate end = tuple.v2();


        DailyDashBoardVO vo = esReportService.tvOnline(start, end);
        if (vo != null) {
            return CommonResponse.success(vo);
        } else {
            return CommonResponse.FAIL;
        }
    }

    @Override
    public CommonResponse<TVComposeDashBoardVO> tvFixCompose() {
        Tuple<LocalDate, LocalDate> tuple = getStartDate("7d", LocalDate.now());
        LocalDate start = tuple.v1();
        LocalDate end = tuple.v2();

        Map<String, List<String>> userMap = esReportService.tvUserTops(start, end);
        Map<String, List<String>> timeMap = esReportService.tvTimeTops(start, end);

        TVComposeDashBoardVO vo = new TVComposeDashBoardVO();

        List<String> dates = new ArrayList<>();
        while (!start.isAfter(end)) {
            dates.add(end.toString());
            end = end.minusDays(1);
        }
        vo.setDates(dates);


        Map<String, List<String>> userValues = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : userMap.entrySet()) {
            String mediaId = entry.getKey();
            String mediaName = mediaId;
            GSContentinfoDO contentinfoDO = gsContentinfoMapper.selectById(mediaId);
            if (contentinfoDO != null) {
                mediaName = contentinfoDO.getName();
            }
            userValues.put(mediaName, entry.getValue());
        }
        vo.setUserValues(userValues);


        Map<String, List<String>> timeValues = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : timeMap.entrySet()) {
            String mediaId = entry.getKey();
            String mediaName = mediaId;
            GSContentinfoDO contentinfoDO = gsContentinfoMapper.selectById(mediaId);
            if (contentinfoDO != null) {
                mediaName = contentinfoDO.getName();
            }
            timeValues.put(mediaName, entry.getValue());
        }
        vo.setTimeValues(timeValues);
        return CommonResponse.success(vo);
    }

    @Override
    public CommonResponse<RecommendComposeDashBoardVO> recommendFixCompose() {
        RecommendComposeDashBoardVO vo = new RecommendComposeDashBoardVO();


        Tuple<LocalDate, LocalDate> tuple = getStartDate("7d", LocalDate.now());
        LocalDate start = tuple.v1();
        LocalDate end = tuple.v2();


        SearchResponse response = esReportService.recommendClick(start, end);
        if (response == null) return CommonResponse.FAIL;

        Map<String, List<String>> sceneMap = new HashMap<>();
        Terms terms = response.getAggregations().get("sceneNames");
        terms.getBuckets().forEach(t -> {
            List<String> list = new ArrayList<>();
            String sceneName = t.getKeyAsString();
            ParsedDateHistogram dateHistogram = t.getAggregations().get("days");
            dateHistogram.getBuckets().forEach(d -> {
                list.add(d.getDocCount() + "");
            });
            sceneMap.put(sceneName, list);
        });
        vo.setSceneData(sceneMap);


        Map<String, List<String>> mediaTypeMap = new HashMap<>();
        Terms terms2 = response.getAggregations().get("mediaTypes");
        terms2.getBuckets().forEach(t -> {
            List<String> list = new ArrayList<>();
            String mediaType = t.getKeyAsString();
            ParsedDateHistogram dateHistogram = t.getAggregations().get("days");
            dateHistogram.getBuckets().forEach(d -> {
                list.add(d.getDocCount() + "");
            });
            mediaTypeMap.put(mediaType, list);
        });
        vo.setMediaData(mediaTypeMap);


        List<String> dates = new ArrayList<>();
        while (!start.isAfter(end)) {
            dates.add(end.toString());
            end = end.minusDays(1);
        }
        vo.setDates(dates);
        return CommonResponse.success(vo);
    }
}
