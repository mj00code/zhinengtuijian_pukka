package com.ubo.iptv.logstash.config;

import com.ubo.iptv.logstash.comsumer.HistoryLogConsumer;
import com.ubo.iptv.logstash.comsumer.RealTimeLogConsumer;
import com.ubo.iptv.logstash.comsumer.RecommendSnapshotConsumer;
import io.nats.client.Connection;
import io.nats.client.ConnectionFactory;
import io.nats.client.MessageHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private HistoryLogConsumer historyLogConsumer;
    @Autowired
    private RealTimeLogConsumer realTimeLogConsumer;
    @Autowired
    private RecommendSnapshotConsumer recommendSnapshotConsumer;
    @Value("${nats.topic.iptv.log.history}")
    private String historyTopic;
    @Value("${nats.topic.iptv.log.realtime}")
    private String realtimeTopic;
    @Value("${nats.topic.iptv.recommend.snapshot}")
    private String recommendSnapshotTopic;
    @Value("${nats.threadcount}")
    private int threadCount;

    @Bean
    public ConnectionFactory nastConnect() {
        try {
            ConnectionFactory cf = new ConnectionFactory(address);
            for (int i = 0; i < threadCount; i++) {
                subscribe(cf, historyTopic, "history", historyLogConsumer);
                subscribe(cf, realtimeTopic, "realtime", realTimeLogConsumer);
                subscribe(cf, recommendSnapshotTopic, "snapshot", recommendSnapshotConsumer);
            }
            return cf;
        } catch (Exception e) {
            logger.error("NatsMQProducer初始化失败!!!!!!", e);
        }
        return null;
    }

    public void subscribe(ConnectionFactory cf, String topic, String group, MessageHandler messageHandler) throws Exception {
        Connection nc = cf.createConnection();
        if (StringUtils.isNotBlank(group)) {
            nc.subscribe(topic, group, messageHandler);
        } else {
            nc.subscribe(topic, messageHandler);
        }
        nc.flush(10 * 1000);
        logger.debug("NatsMQProducer初始化成功!!!!!!");
    }
}
