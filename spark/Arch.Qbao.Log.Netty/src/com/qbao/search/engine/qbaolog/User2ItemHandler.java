package com.qbao.search.engine.qbaolog;

import com.alibaba.fastjson.JSONObject;
import com.qbao.search.engine.service.SparkAlsModelService;
import com.qbao.search.logging.ESLogger;
import com.qbao.search.logging.Loggers;
import com.qbao.search.rpc.Server;
import com.qbao.search.rpc.netty.SimpleHttpRequestHandler;
import com.qbao.search.util.CommonUtil;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class User2ItemHandler extends SimpleHttpRequestHandler<String> {
    private static ESLogger logger = Loggers.getLogger(User2ItemHandler.class);
    public static Pattern pattern = Pattern.compile("[0-9]*");
    public static SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss:SSS");

    public HashMap<String, String> getParms() throws UnsupportedEncodingException {

        HashMap<String, String> parms = new HashMap<String, String>();
        String programId = CommonUtil.getParam(httpRequest, "programId");
        String userId = CommonUtil.getParam(httpRequest, "userId");
        String size = CommonUtil.getParam(httpRequest, "size");
        String time = CommonUtil.getParam(httpRequest, "time");

        if (programId == null || programId.isEmpty())
            programId = "";
        if (userId == null || userId.isEmpty()) {
            userId = "";
        } else {
            userId = userId.trim();
        }
        if (size == null || size.isEmpty())
            size = "20";
        if (time == null || time.isEmpty())
            time = "";

        parms.put("userId", userId);
        parms.put("programId", programId);
        parms.put("size", size);
        parms.put("time", time);

//        System.out.println("参数==user2item:" + "userid=" + userId + " size=" + size + " time=" + sdf.format(new Date()));
        return parms;
    }

    @Override
    protected String doRun() throws Exception {
        HashMap<String, String> parms = getParms();
        Map<String, Object> map = new HashMap<>();
        String size = parms.get("size");
        String userId = parms.get("userId");
        ArrayList<String> lst = new ArrayList<>();
        long start = System.currentTimeMillis();
        try {

            if (userId.length() > 0) {
                Matcher isNum = pattern.matcher(userId);

                if (!isNum.matches()) {

                    return "{\"data\":[],\"msg\":\"error\",\"result\":0,\"status\":400}";
                }

                lst = SparkAlsModelService.get().user2itemPredict(userId, size);

            }

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"data\":[],\"msg\":\"error\",\"result\":0,\"status\":500}";
        }
        map.put("userId", userId);
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
