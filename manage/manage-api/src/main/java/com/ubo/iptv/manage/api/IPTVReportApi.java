package com.ubo.iptv.manage.api;

import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.core.annotation.APIType;
import com.ubo.iptv.core.enums.APITypeEnum;
import com.ubo.iptv.manage.api.fallback.IPTVReportApiFallback;
import com.ubo.iptv.manage.response.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: xuning
 * @Date: 2020-09-30
 */
@FeignClient(value = "iptv-manage", fallbackFactory = IPTVReportApiFallback.class)
@Api(value = "report", tags = "IPTV报表", description = "IPTV报表相关接口")
@APIType(APITypeEnum.PUBLIC)
public interface IPTVReportApi {

    @RequestMapping(value = "/v1/iptv/report/realtime", method = RequestMethod.GET)
    @ApiOperation(value = "实时数据看板", httpMethod = "GET")
    public CommonResponse<RealTimeDashBoardVO> realTimeDashBoard();

    @RequestMapping(value = "/v1/iptv/report/order/payamount", method = RequestMethod.GET)
    @ApiOperation(value = "订购金额", httpMethod = "GET")
    public CommonResponse<DailyDashBoardVO> dailyPayAmount(@ApiParam(name = "interval") @RequestParam(name = "interval") String interval);

    @RequestMapping(value = "/v1/iptv/report/order/orderuser", method = RequestMethod.GET)
    @ApiOperation(value = "订购人数", httpMethod = "GET")
    public CommonResponse<DailyDashBoardVO> dailyOrderUser(@ApiParam(name = "interval") @RequestParam(name = "interval") String interval);

    @RequestMapping(value = "/v1/iptv/report/order/ordercounts", method = RequestMethod.GET)
    @ApiOperation(value = "订购次数", httpMethod = "GET")
    public CommonResponse<DailyDashBoardVO> dailyOrderCounts(@ApiParam(name = "interval") @RequestParam(name = "interval") String interval);

    @RequestMapping(value = "/v1/iptv/report/order/avgpayamount", method = RequestMethod.GET)
    @ApiOperation(value = "订购人均金额", httpMethod = "GET")
    public CommonResponse<DailyDashBoardVO> dailyAvgPayAmount(@ApiParam(name = "interval") @RequestParam(name = "interval") String interval);

    @RequestMapping(value = "/v1/iptv/report/order/increaseuser", method = RequestMethod.GET)
    @ApiOperation(value = "新增订购人数", httpMethod = "GET")
    public CommonResponse<DailyDashBoardVO> dailyIncreaseUserCounts(@ApiParam(name = "interval") @RequestParam(name = "interval") String interval);

    @RequestMapping(value = "/v1/iptv/report/order/fixcompose", method = RequestMethod.GET)
    @ApiOperation(value = "7天内的订购数据组合", httpMethod = "GET")
    public CommonResponse<DailyComposeDashBoardVO> dailyProductType();

    @RequestMapping(value = "/v1/iptv/report/vod/usercounts", method = RequestMethod.GET)
    @ApiOperation(value = "点播人数", httpMethod = "GET")
    public CommonResponse<DailyDashBoardVO> vodUserCounts(@ApiParam(name = "interval") @RequestParam(name = "interval") String interval);

    @RequestMapping(value = "/v1/iptv/report/vod/avgtimes", method = RequestMethod.GET)
    @ApiOperation(value = "平均点播时长", httpMethod = "GET")
    public CommonResponse<DailyDashBoardVO> vodAvgTimes(@ApiParam(name = "interval") @RequestParam(name = "interval") String interval);

    @RequestMapping(value = "/v1/iptv/report/vod/fixcompose", method = RequestMethod.GET)
    @ApiOperation(value = "点播固定数据组合", httpMethod = "GET")
    public CommonResponse<VodComposeDashBoardVO> vodFixCompose();

    @RequestMapping(value = "/v1/iptv/report/tv/online", method = RequestMethod.GET)
    @ApiOperation(value = "直播在线数据", httpMethod = "GET")
    public CommonResponse<DailyDashBoardVO> tvOnline(@ApiParam(name = "interval") @RequestParam(name = "interval") String interval);

    @RequestMapping(value = "/v1/iptv/report/tv/fixcompose", method = RequestMethod.GET)
    @ApiOperation(value = "直播固定数据组合", httpMethod = "GET")
    public CommonResponse<TVComposeDashBoardVO> tvFixCompose();

    @RequestMapping(value = "/v1/iptv/report/recommend/fixcompose", method = RequestMethod.GET)
    @ApiOperation(value = "推荐位数据组合", httpMethod = "GET")
    public CommonResponse<RecommendComposeDashBoardVO> recommendFixCompose();
}
