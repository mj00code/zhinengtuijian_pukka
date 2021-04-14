package com.ubo.iptv.recommend.config;

import io.nats.client.Connection;
import io.nats.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by uboo on 2019/3/14.
 */
@Configuration
public class NatsConfig {
    private final static Logger logger = LoggerFactory.getLogger(NatsConfig.class);

    @Value("${nats.address}")
    private String[] address;

    @Bean
    public Connection nastConnect() {
        try {
            ConnectionFactory cf = new ConnectionFactory(address);
            Connection nc = cf.createConnection();
            nc.flush(10 * 1000);
            logger.debug("NatsMQProducer初始化成功!!!!!!");
            return nc;
        } catch (Exception e) {
            logger.error("NatsMQProducer初始化失败!!!!!!", e);
        }
        return null;
    }
}
