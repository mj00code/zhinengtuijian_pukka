package com.ubo.iptv.logstash.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ubo.iptv.logstash.config.MainConstants;
import com.ubo.iptv.logstash.service.MessageService;
import com.ubo.iptv.logstash.task.LogPushTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by uboo on 2019/3/14.
 */
@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

    AtomicLong id = new AtomicLong(0);
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Autowired
    private MainConstants mainConstants;
    @Autowired
    private NatsMessageServiceImpl natsMessageService;


    @Override
    public void publish(JSONObject json) {
        try {
            int size = LOG_QUEUE.size();
            LOG_QUEUE.get((int) (id.getAndIncrement() % size)).put(json);
        } catch (InterruptedException e) {
            id = new AtomicLong(0);
            log.error("", e);
        }
    }

    @PostConstruct
    public void initThread() {
        for (int i = 0; i < mainConstants.getThreadCount(); i++) {
            BlockingQueue<JSONObject> q = new ArrayBlockingQueue<JSONObject>(99999);
            LOG_QUEUE.add(q);
            threadPoolTaskExecutor.execute(new LogPushTask(i, q, mainConstants, natsMessageService));
        }

    }
}
