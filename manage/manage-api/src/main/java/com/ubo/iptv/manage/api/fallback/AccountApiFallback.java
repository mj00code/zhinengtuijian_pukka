package com.ubo.iptv.manage.api.fallback;

import com.ubo.iptv.manage.api.AccountApi;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @Author: xuning
 * @Date: 2020-11-09
 */
@Component
public class AccountApiFallback implements FallbackFactory<AccountApi> {
    @Override
    public AccountApi create(Throwable throwable) {
        return null;
    }
}
