package com.ubo.iptv.recommend.annotation;

import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author: xuning
 * @Date: 2020-07-27
 */
@EnableFeignClients(basePackages = {"com.ubo.iptv.recommend.api"})
@ComponentScan(basePackages = {"com.ubo.iptv.recommend.api.fallback"})
public class RecommendFeignProxy {
    public RecommendFeignProxy() {

    }
}
