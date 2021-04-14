package com.ubo.iptv.job.config;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @Author: xuning
 * @Date: 2019-04-28
 */
@Configuration
public class HttpClientConfig {

    @Bean
    public CloseableHttpClient httpClient() {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        // 最大连接数
        httpClientBuilder.setMaxConnTotal(100);
        // 每个路由最大连接数
        httpClientBuilder.setMaxConnPerRoute(50);
        // 清理空闲连接频率
        httpClientBuilder.evictIdleConnections(60, TimeUnit.SECONDS);
        return httpClientBuilder.build();
    }
}
