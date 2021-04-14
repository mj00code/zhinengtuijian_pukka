package com.ubo.iptv.logstash.service;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by uboo on 2019/3/14.
 */
public interface MessageService {

    //使用阻塞队列，防止日志太多，导致服务内存爆掉
    public List<BlockingQueue<JSONObject>> LOG_QUEUE = new ArrayList<BlockingQueue<JSONObject>>();

    public void publish(JSONObject message);
}
