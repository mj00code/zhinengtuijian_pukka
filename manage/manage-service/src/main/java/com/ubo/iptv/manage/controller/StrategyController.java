package com.ubo.iptv.manage.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubo.iptv.core.AbstractController;
import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.manage.api.StrategyApi;
import com.ubo.iptv.manage.requset.StrategyDetailSaveDTO;
import com.ubo.iptv.manage.requset.StrategyManualAddDTO;
import com.ubo.iptv.manage.response.StrategyDetailVO;
import com.ubo.iptv.manage.response.StrategyInfoVO;
import com.ubo.iptv.manage.response.StrategyManualVO;
import com.ubo.iptv.manage.service.SceneService;
import com.ubo.iptv.manage.service.StrategyService;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author: xuning
 * @Date: 2020-09-30
 */
@RestController
public class StrategyController extends AbstractController implements StrategyApi {

    @Autowired
    private StrategyService strategyService;
    @Autowired
    private SceneService sceneService;

    /**
     * 策略列表
     *
     * @param ispId
     * @param type
     * @param page
     * @param size
     * @return
     */
    @Override
    public CommonResponse<StrategyInfoVO> listStrategy(@ApiParam(value = "运营商id", required = true) @RequestParam(value = "ispId") String ispId,
                                                       @ApiParam(value = "类型", required = true) @RequestParam(value = "type") Integer type,
                                                       @ApiParam(value = "当前页") @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                       @ApiParam(value = "每页数量") @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        return strategyService.listStrategy(ispId, type, new Page(page, size));
    }


    /**
     * 策略详情
     *
     * @param strategyId
     * @return
     */
    @Override
    public CommonResponse<StrategyDetailVO> getStrategyDetail(@ApiParam(value = "策略id", required = true) @RequestParam(value = "strategyId") Long strategyId) {
        return strategyService.getStrategyDetail(strategyId);
    }

    /**
     * 保存策略详情
     *
     * @param dto
     * @return
     */
    @Override
    public CommonResponse saveStrategyDetail(@ApiParam(value = "参数", required = true) @RequestBody @Valid StrategyDetailSaveDTO dto) {
        CommonResponse response = strategyService.saveStrategyDetail(dto);
        if (response._isOk()) {
            sceneService.cacheScene();
        }
        return response;
    }

    /**
     * 人工干预媒资列表
     *
     * @param strategyId
     * @return
     */
    @Override
    public CommonResponse<List<StrategyManualVO>> listManual(@ApiParam(value = "策略id", required = true) @RequestParam(value = "strategyId") Long strategyId) {
        return strategyService.listManual(strategyId);
    }

    @Override
    public CommonResponse<List<StrategyManualVO>> searchMedia(@ApiParam(value = "策略id", required = true) @RequestParam(value = "strategyId") Long strategyId,
                                                              @ApiParam(value = "媒资名称/id", required = true) @RequestParam(value = "content") String content) {
        return strategyService.searchMedia(strategyId, content);
    }

    /**
     * 添加人工干预媒资
     *
     * @param dto
     * @return
     */
    @Override
    public CommonResponse addManual(@ApiParam(value = "参数", required = true) @RequestBody @Valid StrategyManualAddDTO dto) {
        CommonResponse response = strategyService.addManual(dto);
        if (response._isOk()) {
            sceneService.cacheScene();
        }
        return response;
    }

    /**
     * 取消人工干预媒资
     *
     * @param id
     * @return
     */
    @Override
    public CommonResponse removeManual(@ApiParam(value = "人工干预id", required = true) @RequestParam(value = "id") Long id) {
        CommonResponse response = strategyService.removeManual(id);
        if (response._isOk()) {
            sceneService.cacheScene();
        }
        return response;
    }
}
