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

@Service
@Slf4j
public class LogFileService {
    @Autowired
    private NatsMessageService messageService;
    @Value("${nats.topic.iptv.log.history}")
    private String topic;

    @Async("fileTaskExecutor")
    public void work(String sysId, File file, LocalDate localDate) {
        log.info("开始处理日志文件:{}", file.getPath());
        if (file.exists()) {
            try {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                BufferedReader br = new BufferedReader(new InputStreamReader(bis, "utf-8"), 5 * 1024 * 1024);
                String line = "";
                List<JSONObject> bulk = new ArrayList<>();
                while ((line = br.readLine()) != null) {
                    line = line.substring(line.indexOf("{"), line.length());
                    JSONObject jsonObject = JSON.parseObject(line);


                    bulk.add(jsonObject);
                    if (bulk.size() > 10) {
                        JSONObject message = new JSONObject();
                        message.put("sysId", sysId);
                        message.put("date", localDate.toString());
                        message.put("list", bulk);
                        messageService.publish(topic, message.toJSONString());
                        bulk.clear();
                    }


                }

                if (bulk.size() > 0) {
                    JSONObject message = new JSONObject();
                    message.put("sysId", sysId);
                    message.put("date", localDate);
                    message.put("list", bulk);
                    messageService.publish(topic, message.toJSONString());
                }
                log.info("完成日志文件:{}", file.getPath());


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            log.error("file:{} not exist", file.getPath());
        }

    }
}
