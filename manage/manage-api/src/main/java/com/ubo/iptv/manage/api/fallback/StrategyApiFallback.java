package com.ubo.iptv.manage.api.fallback;

import com.ubo.iptv.manage.api.StrategyApi;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @Author: xuning
 * @Date: 2020-11-09
 */
@Component
public class StrategyApiFallback implements FallbackFactory<StrategyApi> {
    @Override
    public StrategyApi create(Throwable throwable) {
        return null;
    }
}
