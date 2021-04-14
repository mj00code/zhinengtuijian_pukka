package com.ubo.iptv.manage.api;

import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.core.annotation.APIType;
import com.ubo.iptv.core.enums.APITypeEnum;
import com.ubo.iptv.manage.api.fallback.StrategyApiFallback;
import com.ubo.iptv.manage.requset.StrategyDetailSaveDTO;
import com.ubo.iptv.manage.requset.StrategyManualAddDTO;
import com.ubo.iptv.manage.response.StrategyDetailVO;
import com.ubo.iptv.manage.response.StrategyInfoVO;
import com.ubo.iptv.manage.response.StrategyManualVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author: xuning
 * @Date: 2020-11-09
 */
@FeignClient(value = "iptv-manage", fallbackFactory = StrategyApiFallback.class)
@Api(value = "strategy api", tags = "推荐策略", description = "推荐策略相关接口")
@APIType(APITypeEnum.PUBLIC)
public interface StrategyApi {

    /**
     * 策略列表
     *
     * @param ispId
     * @param type
     * @param page
     * @param size
     * @return
     */
    @ApiOperation(value = "策略列表", httpMethod = "GET", notes = "0:成功，1：失败")
    @RequestMapping(value = "/v1/strategy/list", method = RequestMethod.GET)
    CommonResponse<StrategyInfoVO> listStrategy(@ApiParam(value = "运营商id", required = true) @RequestParam(value = "ispId") String ispId,
                                                @ApiParam(value = "类型", required = true) @RequestParam(value = "type") Integer type,
                                                @ApiParam(value = "当前页") @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                @ApiParam(value = "每页数量") @RequestParam(value = "size", required = false, defaultValue = "10") Integer size);

    /**
     * 策略详情
     *
     * @param strategyId
     * @return
     */
    @ApiOperation(value = "策略详情", httpMethod = "GET", notes = "0:成功，1：失败")
    @RequestMapping(value = "/v1/strategy/detail", method = RequestMethod.GET)
    CommonResponse<StrategyDetailVO> getStrategyDetail(@ApiParam(value = "策略id", required = true) @RequestParam(value = "strategyId") Long strategyId);

    /**
     * 保存策略详情
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "保存策略详情", httpMethod = "POST", notes = "0:成功，1：失败")
    @RequestMapping(value = "/v1/strategy/detail/save", method = RequestMethod.POST)
    CommonResponse saveStrategyDetail(@ApiParam(value = "参数", required = true) @RequestBody @Valid StrategyDetailSaveDTO dto);

    /**
     * 人工干预媒资列表
     *
     * @param strategyId
     * @return
     */
    @ApiOperation(value = "人工干预媒资列表", httpMethod = "GET", notes = "0:成功，1：失败")
    @RequestMapping(value = "/v1/strategy/manual/list", method = RequestMethod.GET)
    CommonResponse<List<StrategyManualVO>> listManual(@ApiParam(value = "策略id", required = true) @RequestParam(value = "strategyId") Long strategyId);

    /**
     * 人工干预搜索媒资列表
     *
     * @param strategyId
     * @param content
     * @return
     */
    @ApiOperation(value = "人工干预搜索媒资列表", httpMethod = "GET", notes = "0:成功，1：失败")
    @RequestMapping(value = "/v1/strategy/manual/search", method = RequestMethod.GET)
    CommonResponse<List<StrategyManualVO>> searchMedia(@ApiParam(value = "策略id", required = true) @RequestParam(value = "strategyId") Long strategyId,
                                                       @ApiParam(value = "媒资名称/id", required = true) @RequestParam(value = "content") String content);

    /**
     * 添加人工干预媒资
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "添加人工干预媒资", httpMethod = "POST", notes = "0:成功，1：失败")
    @RequestMapping(value = "/v1/strategy/manual/add", method = RequestMethod.POST)
    CommonResponse addManual(@ApiParam(value = "参数", required = true) @RequestBody @Valid StrategyManualAddDTO dto);

    /**
     * 取消人工干预媒资
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "取消人工干预媒资", httpMethod = "POST", notes = "0:成功，1：失败")
    @RequestMapping(value = "/v1/strategy/manual/remove", method = RequestMethod.GET)
    CommonResponse removeManual(@ApiParam(value = "人工干预id", required = true) @RequestParam(value = "id") Long id);
}
