package com.ubo.iptv.manage.service;

import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.manage.response.*;

import java.time.LocalDateTime;

public interface IPTVReportService {

    public CommonResponse<RealTimeDashBoardVO> realTimeDashBoardVO(LocalDateTime dateTime);


    public CommonResponse<DailyDashBoardVO> dailyOrderAmount(String interval);

    public CommonResponse<DailyDashBoardVO> dailyOrderUser(String interval);

    public CommonResponse<DailyDashBoardVO> dailyOrderCounts(String interval);

    public CommonResponse<DailyDashBoardVO> dailyIncreaseUserCounts(String interval);

    public CommonResponse<DailyDashBoardVO> dailyAvgAmount(String interval);

    public CommonResponse<DailyComposeDashBoardVO> orderFixCompose();

    public CommonResponse<DailyDashBoardVO> vodAvgTimes(String interval);

    public CommonResponse<DailyDashBoardVO> vodUserCounts(String interval);

    public CommonResponse<VodComposeDashBoardVO> vodFixCompose();

    public CommonResponse<DailyDashBoardVO> tvOnline(String interval);

    public CommonResponse<TVComposeDashBoardVO> tvFixCompose();

    public CommonResponse<RecommendComposeDashBoardVO> recommendFixCompose();
}
