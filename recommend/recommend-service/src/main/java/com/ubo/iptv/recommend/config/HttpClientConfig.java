package com.ubo.iptv.recommend.config;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class HttpClientConfig {

    @Bean
    public HttpClient httpClient() {
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
