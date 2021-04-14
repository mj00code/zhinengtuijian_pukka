package com.qbao.search.engine.qbaolog;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qbao.search.engine.service.SparkItemSimMatrixService;
import com.qbao.search.logging.ESLogger;
import com.qbao.search.logging.Loggers;
import com.qbao.search.rpc.Server;
import com.qbao.search.rpc.netty.SimpleHttpRequestHandler;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Item2ItemPostHandler extends SimpleHttpRequestHandler<String> {
    public static Pattern pattern = Pattern.compile("[0-9]*");
    public static SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss:SSS");
    private static ESLogger logger = Loggers.getLogger(User2ItemPostHandler.class);

    public HashMap<String, String> getParms() throws UnsupportedEncodingException {
        String jsonParam = new String(httpRequest.getContent().array()).trim();// 长json报错用trim解决
        JSONObject jsonObject = JSON.parseObject(jsonParam);
        String programId = jsonObject.getString("programId");
        String size = jsonObject.getString("size");
        String programIds = jsonObject.getJSONArray("programIds").toString();// 需要过滤的媒资id列表
        HashMap<String, String> parms = new HashMap<String, String>();

        if (programIds == null || programIds.isEmpty())
            programIds = "";
        if (programId == null || programId.isEmpty()) {
            programId = "";
        } else {
            programId = programId.trim();
        }
        if (size == null || size.isEmpty())
            size = "20";
        parms.put("programId", programId);
        parms.put("programIds", programIds);
        parms.put("size", size);
//        System.out.println("参数==item2item:" + "programId=" + programId + " size=" + size + ",programIds = " + programIds + " time=" + sdf.format(new Date()));

        return parms;
    }

    @Override
    protected String doRun() throws Exception {
        HashMap<String, String> parms = getParms();
        Map<String, Object> map = new HashMap<>();
        String size = parms.get("size");
        String programId = parms.get("programId");
        String programIds = parms.get("programIds");
        programIds = programIds.replace("\"", "").replace("[", ",").replace("]", ",");
        String[] programIdlst = programIds.split(",");
        ArrayList<String> lst = new ArrayList<>();
        long start = System.currentTimeMillis();
        try {

            if (programId.length() > 0) {
                Matcher isNum = pattern.matcher(programId);

                if (!isNum.matches()) {

                    return "{\"data\":[],\"msg\":\"error\",\"result\":0,\"status\":400}";
                }

                for (String id : programIdlst) {
                    isNum = pattern.matcher(id);
                    if (!isNum.matches()) {

                        return "{\"data\":[],\"msg\":\"error\",\"result\":0,\"status\":400}";
                    }
                }

                lst = SparkItemSimMatrixService.get().item2itemPredict(programId, Integer.parseInt(size.trim()), programIds);


            }

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"data\":[],\"msg\":\"error\",\"result\":0,\"status\":500}";
        }
        map.put("programId", programId);
        map.put("top", size);
        map.put("data", lst);
        map.put("result", lst.size());
        map.put("time", System.currentTimeMillis() - start);
        map.put("msg", "ok");
        map.put("status", 200);
        String jsonlist = JSONObject.toJSONString(map);
//        System.out.println("=======响应:" + this.getClass().getName() + "\r\n" + jsonlist);
        return jsonlist;
    }

    @Override
    public void setServer(Server server) {
    }
}
