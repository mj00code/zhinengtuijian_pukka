package com.ubo.iptv.job.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class ElasticSearchConfig {

    @Bean
    public RestHighLevelClient client() throws Exception {
        List<HttpHost> hosts = new ArrayList<>();
        hosts.add(new HttpHost("192.168.76.20", 9200, "http"));
        hosts.add(new HttpHost("192.168.76.21", 9200, "http"));
        hosts.add(new HttpHost("192.168.76.22", 9200, "http"));
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(hosts.toArray(new HttpHost[]{})));
        return client;
    }


}
