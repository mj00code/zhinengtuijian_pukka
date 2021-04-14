package com.ubo.iptv.manage.controller;

import com.ubo.iptv.core.AbstractController;
import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.manage.api.ConfigApi;
import com.ubo.iptv.manage.requset.CacheSwitchDTO;
import com.ubo.iptv.manage.service.BlacklistService;
import com.ubo.iptv.manage.service.SceneService;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Author: xuning
 * @Date: 2020-09-30
 */
@RestController
public class ConfigController extends AbstractController implements ConfigApi {

    @Autowired
    private SceneService sceneService;
    @Autowired
    private BlacklistService blacklistService;

    @Override
    public CommonResponse cacheScene() {
        return sceneService.cacheScene();
    }

    @Override
    public CommonResponse cacheBlacklist() {
        return blacklistService.cacheBlacklist();
    }

    @Override
    public CommonResponse cacheSwitch(@ApiParam(value = "参数", required = true) @RequestBody @Valid CacheSwitchDTO dto) {
        return sceneService.cacheSwitch(dto);
    }
}
