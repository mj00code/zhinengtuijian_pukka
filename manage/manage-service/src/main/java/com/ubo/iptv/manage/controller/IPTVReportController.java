package com.ubo.iptv.manage.controller;

import com.ubo.iptv.core.AbstractController;
import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.manage.api.IPTVReportApi;
import com.ubo.iptv.manage.response.*;
import com.ubo.iptv.manage.service.IPTVReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * @Author: xuning
 * @Date: 2020-09-30
 */
@Api(value = "iptv api", tags = "iptv报表", description = "IPTV报表相关接口")
@RestController
public class IPTVReportController extends AbstractController implements IPTVReportApi {
    @Autowired
    private IPTVReportService iptvReportService;

    public CommonResponse<RealTimeDashBoardVO> realTimeDashBoard() {
        return iptvReportService.realTimeDashBoardVO(LocalDateTime.now());
    }

    @Override
    public CommonResponse<DailyDashBoardVO> dailyPayAmount(@ApiParam(name = "interval") @RequestParam(name = "interval") String interval) {
        return iptvReportService.dailyOrderAmount(interval);
    }

    @Override
    public CommonResponse<DailyDashBoardVO> dailyOrderUser(@ApiParam(name = "interval") @RequestParam(name = "interval") String interval) {
        return iptvReportService.dailyOrderUser(interval);
    }

    @Override
    public CommonResponse<DailyDashBoardVO> dailyOrderCounts(@ApiParam(name = "interval") @RequestParam(name = "interval") String interval) {
        return iptvReportService.dailyOrderCounts(interval);
    }

    @Override
    public CommonResponse<DailyDashBoardVO> dailyAvgPayAmount(@ApiParam(name = "interval") @RequestParam(name = "interval") String interval) {
        return iptvReportService.dailyAvgAmount(interval);
    }

    @Override
    public CommonResponse<DailyDashBoardVO> dailyIncreaseUserCounts(@ApiParam(name = "interval") @RequestParam(name = "interval") String interval) {
        return iptvReportService.dailyIncreaseUserCounts(interval);
    }

    @Override
    public CommonResponse<DailyComposeDashBoardVO> dailyProductType() {
        return iptvReportService.orderFixCompose();
    }

    @Override
    public CommonResponse<DailyDashBoardVO> vodUserCounts(@ApiParam(name = "interval") @RequestParam(name = "interval") String interval) {
        return iptvReportService.vodUserCounts(interval);
    }

    @Override
    public CommonResponse<DailyDashBoardVO> vodAvgTimes(@ApiParam(name = "interval") @RequestParam(name = "interval") String interval) {
        return iptvReportService.vodAvgTimes(interval);
    }

    @Override
    public CommonResponse<VodComposeDashBoardVO> vodFixCompose() {
        return iptvReportService.vodFixCompose();
    }

    @Override
    public CommonResponse<DailyDashBoardVO> tvOnline(@ApiParam(name = "interval") @RequestParam(name = "interval") String interval) {
        return iptvReportService.tvOnline(interval);
    }

    @Override
    public CommonResponse<TVComposeDashBoardVO> tvFixCompose() {
        return iptvReportService.tvFixCompose();
    }

    @Override
    public CommonResponse<RecommendComposeDashBoardVO> recommendFixCompose() {
        return iptvReportService.recommendFixCompose();
    }
}
