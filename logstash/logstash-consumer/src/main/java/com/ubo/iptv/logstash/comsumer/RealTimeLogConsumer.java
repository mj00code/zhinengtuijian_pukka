package com.ubo.iptv.logstash.comsumer;

import com.alibaba.fastjson.JSONObject;
import com.ubo.iptv.logstash.service.LogService;
import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.MessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RealTimeLogConsumer implements MessageHandler {
    @Autowired
    private LogService contentService;


    @Override
    public void onMessage(Message message) {
        log.debug("接受到实时日志消息" + new String(message.getData()));
        try {
            JSONObject logs = JSONObject.parseObject(new String(message.getData()));
            contentService.realtimeClean(logs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.debug("处理完成:" + Thread.currentThread().getId());
    }
}
