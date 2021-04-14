package com.ubo.iptv.job;

import com.ubo.iptv.manage.annotation.ManageStarter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author xuxueli 2018-10-28 00:38:13
 */
@SpringBootApplication
@EnableEurekaClient
@ManageStarter
public class SyncJobApplication {

    public static void main(String[] args) {
        SpringApplication.run(SyncJobApplication.class, args);
    }
}