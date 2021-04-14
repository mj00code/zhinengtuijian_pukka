package com.ubo.iptv.recommend.service;

import io.nats.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by uboo on 2019/3/14.
 */
@Service
public class NatsMessageService {
    private Logger logger = LoggerFactory.getLogger(NatsMessageService.class);
    @Autowired
    private Connection connection;
    @Value("${nats.topic.iptv.recommend.snapshot}")
    private String topic;

    public void publish(String topic, String message) {
        try {
            connection.publish(topic, message.getBytes("utf-8"));
//            logger.debug("MQ发送成功! topic={}, content={}", topic, message);
        } catch (Exception e) {
            logger.error("MQ发送失败! topic={}, content={}", topic, message);
        }
    }

    public void publish(String message) {
        this.publish(topic, message);
    }
}
