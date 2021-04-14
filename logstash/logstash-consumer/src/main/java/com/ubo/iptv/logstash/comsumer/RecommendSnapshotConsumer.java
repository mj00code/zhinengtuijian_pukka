package com.ubo.iptv.logstash.comsumer;

import com.alibaba.fastjson.JSONObject;
import com.ubo.iptv.logstash.service.LogService;
import io.nats.client.Message;
import io.nats.client.MessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RecommendSnapshotConsumer implements MessageHandler {
    @Autowired
    private LogService contentService;

    @Override
    public void onMessage(Message message) {
        log.debug("接受到推荐结果消息" + new String(message.getData()));
        try {
            JSONObject logs = JSONObject.parseObject(new String(message.getData()));
            contentService.recommendSnapshotAdd(logs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.debug("处理完成:" + Thread.currentThread().getId());
    }
}
