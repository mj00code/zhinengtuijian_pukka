package com.ubo.iptv.logstash.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ubo.iptv.logstash.config.MainConstants;
import com.ubo.iptv.logstash.service.impl.NatsMessageServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;

@Slf4j
public class LogPushTask extends Thread {


    BlockingQueue<JSONObject> queue;
    int id = 0;
    MainConstants mainConstants;
    NatsMessageServiceImpl nats;

    public LogPushTask(int id, BlockingQueue<JSONObject> queue, MainConstants mainConstants, NatsMessageServiceImpl nats) {
        this.id = id;
        this.queue = queue;
        this.mainConstants = mainConstants;
        this.nats = nats;
    }

    public void run() {
        log.info(id + " LogTask is start .......");
        int sleepCount = 0;
        JSONArray jsonArray = new JSONArray();
        while (true) {
            try {
                JSONObject message = queue.poll();
                if (null != message) {
                    jsonArray.add(message);
                    if (jsonArray.size() >= mainConstants.getCollectCount()) {//数量满时，批量入库
                        sendMessage(jsonArray);
                        sleepCount = 0;
                        jsonArray.clear();
                    }
                } else {// 取不到数据 就休眠

                    try {
                        Thread.sleep(1000);
                        sleepCount++;
                    } catch (InterruptedException e) {//　休眠中断了继续
                    }

                    if (sleepCount > 20 && jsonArray.size() > 0) {//20秒后，list没有满也插入
                        sendMessage(jsonArray);
                        sleepCount = 0;
                        jsonArray.clear();
                    }
                }
            } catch (Exception e) { //异常后，可以继续处理
                log.error("", e);
                log.error("miss data: " + jsonArray.toJSONString());    //数据打印日志，然后清理掉
                jsonArray.clear();//错误数据会导致系统崩溃。
            }

        }
    }

    private void sendMessage(JSONArray array) {
        log.info("send batch message");
        JSONObject message = new JSONObject();
        message.put("list", array);
        nats.publish(message.toJSONString());
    }

}
