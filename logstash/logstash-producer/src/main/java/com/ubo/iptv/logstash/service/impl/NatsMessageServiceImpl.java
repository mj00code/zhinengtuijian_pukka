package com.ubo.iptv.logstash.service.impl;

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
public class NatsMessageServiceImpl {
    private Logger logger = LoggerFactory.getLogger(NatsMessageServiceImpl.class);
    @Autowired
    private Connection connection;
    @Value("${nats.topic.iptv.log.realtime}")
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
