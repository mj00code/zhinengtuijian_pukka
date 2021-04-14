package com.ubo.iptv.manage.annotation;

import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author: xuning
 * @Date: 2020-07-27
 */
@EnableFeignClients(basePackages = {"com.ubo.iptv.manage.api"})
@ComponentScan(basePackages = {"com.ubo.iptv.manage.api.fallback"})
public class ManageFeignProxy {
    public ManageFeignProxy() {

    }
}
