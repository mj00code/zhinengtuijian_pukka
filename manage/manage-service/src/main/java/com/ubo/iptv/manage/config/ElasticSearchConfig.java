package com.ubo.iptv.manage.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ElasticSearchConfig {

    @Value("192.168.2.44,192.168.2.63,192.168.2.84")
    private String[] urls;

    @Bean
    public RestHighLevelClient client() throws Exception {
        List<HttpHost> hosts = new ArrayList<>();
        for (String url : urls) {
            hosts.add(new HttpHost(url, 9200, "http"));
        }
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(hosts.toArray(new HttpHost[]{})));
        return client;
    }
}
