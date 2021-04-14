package com.ubo.iptv.recommend;

import com.ubo.iptv.recommend.util.SpringContextUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ApplicationContext;

/**
 * @Author: xuning
 * @Date: 2020-07-27
 */
@SpringBootApplication
@EnableEurekaClient
public class RecommendApplication {
    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(RecommendApplication.class, args);
        SpringContextUtil.setApplicationContext(applicationContext);
    }
}
