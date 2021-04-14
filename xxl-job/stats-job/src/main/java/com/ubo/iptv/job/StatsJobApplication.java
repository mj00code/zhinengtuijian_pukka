package com.ubo.iptv.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author xuxueli 2018-10-28 00:38:13
 */
@SpringBootApplication
@EnableEurekaClient
public class StatsJobApplication {

    public static void main(String[] args) {
        SpringApplication.run(StatsJobApplication.class, args);
    }
}