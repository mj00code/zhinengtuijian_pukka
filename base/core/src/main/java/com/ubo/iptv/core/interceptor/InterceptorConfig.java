package com.ubo.iptv.core.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@Slf4j
public class InterceptorConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 调用来源拦截，内外网关区分
        registry.addInterceptor(new APIInterceptor()).addPathPatterns("/**");
        super.addInterceptors(registry);
    }
}