package com.ubo.iptv.manage.entity;

import com.alibaba.fastjson.JSONObject;
import com.ubo.iptv.core.util.DateUtil;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderedUser {
    private String userId;
    private LocalDateTime date;


    public OrderedUser(String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        this.userId = jsonObject.getString("user_id");
        this.date = DateUtil.parse(jsonObject.getString("create_time"));
    }
}
