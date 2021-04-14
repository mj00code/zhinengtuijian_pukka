package com.ubo.iptv.logstash.service;

import com.alibaba.fastjson.JSONObject;

public interface LogService {

    public void historyClean(JSONObject jsonArray);


    public void realtimeClean(JSONObject jsonObject);

    public void recommendSnapshotAdd(JSONObject jsonObject);
}
