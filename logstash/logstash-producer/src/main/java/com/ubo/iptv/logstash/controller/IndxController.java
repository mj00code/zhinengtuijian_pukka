package com.ubo.iptv.logstash.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.logstash.service.impl.MessageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class IndxController {

    @Autowired
    private MessageServiceImpl messageService;


    @GetMapping("/index")
    public Mono<String> index() {
        return Mono.just(System.currentTimeMillis() + "");
    }

    @PostMapping("/log/data")
    public Mono<CommonResponse> saveLog(@RequestBody JSONArray lines) throws Exception {
        for (int i = 0; i < lines.size(); i++) {
            try {
                JSONObject jsonObject = lines.getJSONObject(i);
                log.debug(jsonObject.toJSONString());
                messageService.publish(jsonObject);
            } catch (Exception e) {
                log.error("日志格式异常:{}", lines.toJSONString());
            }
        }
        return Mono.just(CommonResponse.SUCCESS);
    }


}
