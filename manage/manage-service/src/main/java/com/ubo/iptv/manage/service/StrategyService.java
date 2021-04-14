package com.ubo.iptv.manage.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.manage.requset.StrategyDetailSaveDTO;
import com.ubo.iptv.manage.requset.StrategyManualAddDTO;
import com.ubo.iptv.manage.response.StrategyDetailVO;
import com.ubo.iptv.manage.response.StrategyInfoVO;
import com.ubo.iptv.manage.response.StrategyManualVO;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author: xuning
 * @Date: 2020-11-07
 */
public interface StrategyService {

    /**
     * 策略列表
     *
     * @param ispId
     * @param type
     * @param page
     * @return
     */
    CommonResponse<StrategyInfoVO> listStrategy(
            @ApiParam(value = "运营商id", required = true) @RequestParam(value = "ispId") String ispId,
            @ApiParam(value = "类型", required = true) @RequestParam(value = "type") Integer type,
            Page page);

    /**
     * 策略详情
     *
     * @param strategyId
     * @return
     */
    CommonResponse<StrategyDetailVO> getStrategyDetail(@ApiParam(value = "策略id", required = true) @RequestParam(value = "strategyId") Long strategyId);

    /**
     * 保存策略详情
     *
     * @param dto
     * @return
     */
    CommonResponse saveStrategyDetail(@ApiParam(value = "参数", required = true) @RequestBody @Valid StrategyDetailSaveDTO dto);

    /**
     * 人工干预媒资列表
     *
     * @param strategyId
     * @return
     */
    CommonResponse<List<StrategyManualVO>> listManual(Long strategyId);

    /**
     * 人工干预搜索媒资列表
     *
     * @param strategyId
     * @param content
     * @return
     */
    CommonResponse<List<StrategyManualVO>> searchMedia(Long strategyId, String content);

    /**
     * 添加人工干预媒资
     *
     * @param dto
     * @return
     */
    CommonResponse addManual(StrategyManualAddDTO dto);

    /**
     * 取消人工干预媒资
     *
     * @param id
     * @return
     */
    CommonResponse removeManual(Long id);
}
