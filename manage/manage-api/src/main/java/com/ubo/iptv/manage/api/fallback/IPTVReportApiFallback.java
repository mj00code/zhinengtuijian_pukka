package com.ubo.iptv.manage.api.fallback;

import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.manage.api.IPTVReportApi;
import com.ubo.iptv.manage.response.*;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author: xuning
 * @Date: 2020-09-30
 */
@Component
@Slf4j
public class IPTVReportApiFallback implements FallbackFactory<IPTVReportApi> {

    @Override
    public IPTVReportApi create(Throwable throwable) {
        return new IPTVReportApi() {
            @Override
            public CommonResponse<RealTimeDashBoardVO> realTimeDashBoard() {
                log.error(throwable.getMessage());
                return CommonResponse.FALL_BACK;
            }

            @Override
            public CommonResponse<DailyDashBoardVO> dailyPayAmount(String interval) {
                log.error(throwable.getMessage());
                return CommonResponse.FALL_BACK;
            }

            @Override
            public CommonResponse<DailyDashBoardVO> dailyOrderUser(String interval) {
                log.error(throwable.getMessage());
                return CommonResponse.FALL_BACK;
            }

            @Override
            public CommonResponse<DailyDashBoardVO> dailyOrderCounts(String interval) {
                log.error(throwable.getMessage());
                return CommonResponse.FALL_BACK;
            }

            @Override
            public CommonResponse<DailyDashBoardVO> dailyAvgPayAmount(String interval) {
                log.error(throwable.getMessage());
                return CommonResponse.FALL_BACK;
            }

            @Override
            public CommonResponse<DailyDashBoardVO> dailyIncreaseUserCounts(String interval) {
                log.error(throwable.getMessage());
                return CommonResponse.FALL_BACK;
            }

            @Override
            public CommonResponse<DailyComposeDashBoardVO> dailyProductType() {
                log.error(throwable.getMessage());
                return CommonResponse.FALL_BACK;
            }

            @Override
            public CommonResponse<DailyDashBoardVO> vodUserCounts(String interval) {
                log.error(throwable.getMessage());
                return CommonResponse.FALL_BACK;
            }

            @Override
            public CommonResponse<DailyDashBoardVO> vodAvgTimes(String interval) {
                log.error(throwable.getMessage());
                return CommonResponse.FALL_BACK;
            }

            @Override
            public CommonResponse<VodComposeDashBoardVO> vodFixCompose() {
                log.error(throwable.getMessage());
                return CommonResponse.FALL_BACK;
            }

            @Override
            public CommonResponse<DailyDashBoardVO> tvOnline(String interval) {
                log.error(throwable.getMessage());
                return CommonResponse.FALL_BACK;
            }

            @Override
            public CommonResponse<TVComposeDashBoardVO> tvFixCompose() {
                log.error(throwable.getMessage());
                return CommonResponse.FALL_BACK;
            }

            @Override
            public CommonResponse<RecommendComposeDashBoardVO> recommendFixCompose() {
                log.error(throwable.getMessage());
                return CommonResponse.FALL_BACK;
            }
            
        };
    }
}
