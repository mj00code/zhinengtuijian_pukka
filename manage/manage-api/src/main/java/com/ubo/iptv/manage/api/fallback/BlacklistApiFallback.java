package com.ubo.iptv.manage.api.fallback;

import com.ubo.iptv.manage.api.BlacklistApi;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @Author: xuning
 * @Date: 2020-11-09
 */
@Component
public class BlacklistApiFallback implements FallbackFactory<BlacklistApi> {
    @Override
    public BlacklistApi create(Throwable throwable) {
        return null;
    }
}
