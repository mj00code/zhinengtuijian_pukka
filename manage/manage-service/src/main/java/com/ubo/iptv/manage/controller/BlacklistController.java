package com.ubo.iptv.manage.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubo.iptv.core.AbstractController;
import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.manage.api.BlacklistApi;
import com.ubo.iptv.manage.requset.BlacklistAddDTO;
import com.ubo.iptv.manage.response.BlacklistVO;
import com.ubo.iptv.manage.service.BlacklistService;
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
 * @Date: 2020-11-07
 */
@RestController
public class BlacklistController extends AbstractController implements BlacklistApi {

    @Autowired
    private BlacklistService blacklistService;

    @Override
    public CommonResponse<IPage<BlacklistVO>> listBlacklist(@ApiParam(value = "媒资名称/id") @RequestParam(value = "content", required = false) String content,
                                                            @ApiParam(value = "当前页") @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                            @ApiParam(value = "每页数量") @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        return blacklistService.listBlacklist(content, new Page(page, size));
    }

    @Override
    public CommonResponse<List<BlacklistVO>> searchMedia(@ApiParam(value = "运营商id") @RequestParam(value = "sysId", required = false) String sysId,
                                                         @ApiParam(value = "媒资名称/id", required = true) @RequestParam(value = "content") String content) {
        return blacklistService.searchMedia(sysId, content);
    }


    @Override
    public CommonResponse addBlackList(@ApiParam(value = "参数", required = true) @RequestBody @Valid BlacklistAddDTO dto) {
        CommonResponse response = blacklistService.addBlackList(dto);
        if (response._isOk()) {
            blacklistService.cacheBlacklist();
        }
        return response;
    }

    @Override
    public CommonResponse removeBlackList(@ApiParam(value = "黑名单id", required = true) @RequestParam(value = "id") Long id) {
        CommonResponse response = blacklistService.removeBlackList(id);
        if (response._isOk()) {
            blacklistService.cacheBlacklist();
        }
        return response;
    }
}
