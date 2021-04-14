package com.ubo.iptv.manage.api.fallback;

import com.ubo.iptv.manage.api.ConfigApi;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @Author: xuning
 * @Date: 2020-11-07
 */
@Component
public class ConfigApiFallback implements FallbackFactory<ConfigApi> {

    @Override
    public ConfigApi create(Throwable throwable) {
        return null;
    }
}
