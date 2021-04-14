package com.ubo.iptv.manage.api;

import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.core.annotation.APIType;
import com.ubo.iptv.core.enums.APITypeEnum;
import com.ubo.iptv.manage.api.fallback.ConfigApiFallback;
import com.ubo.iptv.manage.requset.CacheSwitchDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

/**
 * @Author: xuning
 * @Date: 2020-11-07
 */
@FeignClient(value = "iptv-manage", fallbackFactory = ConfigApiFallback.class)
@Api(value = "config api", tags = "配置", description = "配置相关接口")
@APIType(APITypeEnum.PUBLIC)
public interface ConfigApi {

    /**
     * 缓存场景信息
     *
     * @return
     */
    @RequestMapping(value = "/v1/config/cache/scene", method = RequestMethod.PUT)
    CommonResponse cacheScene();

    /**
     * 缓存黑名单媒资
     *
     * @return
     */
    @RequestMapping(value = "/v1/config/cache/blacklist", method = RequestMethod.PUT)
    CommonResponse cacheBlacklist();

    /**
     * 缓存场景智能开关状态
     *
     * @return
     */
    @RequestMapping(value = "/v1/config/cache/switch", method = RequestMethod.POST)
    CommonResponse cacheSwitch(@ApiParam(value = "参数", required = true) @RequestBody @Valid CacheSwitchDTO dto);
}
