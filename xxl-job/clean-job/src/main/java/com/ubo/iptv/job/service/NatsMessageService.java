package com.ubo.iptv.job.service;

import io.nats.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by uboo on 2019/3/14.
 */
@Service("natsMessage")
public class NatsMessageService {
    private Logger logger = LoggerFactory.getLogger(NatsMessageService.class);
    @Autowired
    private Connection connection;
    @Value("${nats.topic.iptv.log.history}")
    private String topic;

    public void publish(String topic, String message) {
        try {
            connection.publish(topic, message.getBytes("utf-8"));
//            logger.debug("MQ发送成功! topic={}, content={}", topic, message);
        } catch (Exception e) {
            logger.error(String.format("MQ发送失败! topic=%s, content=%s", topic, message), e);
        }
    }

    public void publish(String message) {
        this.publish(topic, message);
    }
}
