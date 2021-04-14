package com.ubo.iptv.job.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Service
@Slf4j
public class LogFileService {
    @Autowired
    private NatsMessageService messageService;
    @Value("${nats.topic.iptv.log.history}")
    private String topic;

    @Async("fileTaskExecutor")
    public void work(File file, LocalDate localDate, CountDownLatch count) {
        log.info("开始处理日志文件:{}", file.getPath());
        if (file.exists()) {
            try {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                BufferedReader br = new BufferedReader(new InputStreamReader(bis, "utf-8"), 5 * 1024 * 1024);
                String line = "";
                List<JSONObject> bulk = new ArrayList<>();
                while ((line = br.readLine()) != null) {
                    JSONObject jsonObject = null;
                    try {
                        line = line.substring(line.indexOf("{"), line.length());
                        jsonObject = JSON.parseObject(line);
                    } catch (Exception e) {
                        log.error("日志格式异常:{}", line);
                        continue;
                    }

                    if (jsonObject != null) {
                        bulk.add(jsonObject);
                        if (bulk.size() >= 1000) {
                            JSONObject message = new JSONObject();
                            message.put("date", localDate.toString());
                            message.put("list", bulk);
                            messageService.publish(topic, message.toJSONString());
                            bulk.clear();
                        }
                    }
                }
                if (bulk.size() > 0) {
                    JSONObject message = new JSONObject();
                    message.put("date", localDate);
                    message.put("list", bulk);
                    messageService.publish(topic, message.toJSONString());
                }
            } catch (Exception e) {
                log.error("处理异常", e);
            }
            log.info("完成日志文件:{}", file.getPath());
        } else {
            log.error("file:{} not exist", file.getPath());
        }
        count.countDown();
    }
}
