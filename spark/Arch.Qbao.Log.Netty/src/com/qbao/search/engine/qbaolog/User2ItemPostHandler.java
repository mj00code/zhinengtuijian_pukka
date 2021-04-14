package com.qbao.search.engine.qbaolog;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qbao.search.engine.service.SparkAlsModelService;
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

/**
 * 日志请求处理器
 *
 * @author song.j
 * @create 2017-07-11 15:15:37
 **/
public class User2ItemPostHandler extends SimpleHttpRequestHandler<String> {

    private static ESLogger logger = Loggers.getLogger(User2ItemPostHandler.class);
    public static Pattern pattern = Pattern.compile("[0-9]*");
    public static SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss:SSS");

    public HashMap<String, String> getParms() throws UnsupportedEncodingException {
        String jsonParam = new String(httpRequest.getContent().array()).trim();// 长json报错用trim解决
        JSONObject jsonObject = JSON.parseObject(jsonParam);
        String userId = jsonObject.getString("userId");
        String size = jsonObject.getString("size");
        String programIds = jsonObject.getJSONArray("programIds").toString();// 需要过滤的媒资id列表
        HashMap<String, String> parms = new HashMap<String, String>();

        if (programIds == null || programIds.isEmpty())
            programIds = "";
        if (userId == null || userId.isEmpty()) {
            userId = "";
        } else {
            userId = userId.trim();
        }
        if (size == null || size.isEmpty())
            size = "20";
        parms.put("userId", userId);
        parms.put("programIds", programIds);
        parms.put("size", size);
//        System.out.println("参数==user2item:" + "userid=" + userId + " size=" + size + ",programIds = " + programIds + " time=" + sdf.format(new Date()));
        return parms;
    }

    @Override
    protected String doRun() throws Exception {
        HashMap<String, String> parms = getParms();
        Map<String, Object> map = new HashMap<>();
        String size = parms.get("size");
        String userId = parms.get("userId");
        String programIds = parms.get("programIds");
        programIds = programIds.replace("\"", "").replace("[", ",").replace("]", ",");
        String[] programIdlst = programIds.split(",");
        ArrayList<String> lst = new ArrayList<>();
        long start = System.currentTimeMillis();
        try {

            if (userId.length() > 0) {
                Matcher isNum = pattern.matcher(userId);

                if (!isNum.matches()) {

                    return "{\"data\":[],\"msg\":\"error\",\"result\":0,\"status\":400}";
                }

                for (String id : programIdlst) {
                    isNum = pattern.matcher(id);
                    if (!isNum.matches()) {

                        return "{\"data\":[],\"msg\":\"error\",\"result\":0,\"status\":400}";
                    }
                }

                lst = SparkAlsModelService.get().user2itemPredict(userId, size, programIds);

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

    @SuppressWarnings("unused")
    private void putInJsonList(String jsonParam) throws Exception {
        UserLogService logService = new UserLogService();
        logService.saveLogbatch(jsonParam);
    }

    /**
     * 参数校检
     *
     * @throws IllegalAccessException 异常参数返回
     */
    public void invokeParam(Map paramMap) throws IllegalAccessException {

        Object[] params = paramMap.keySet().toArray();

        int required = 0;
        for (Object paramKey : params) {
            // if (!UboDevLog.elmData.contains(paramKey)) {
            // throw new IllegalAccessException("ubo-devlog无效的参数:" +
            // paramKey.toString());
            // }

            // 只有"device_id", "systime"必须要，其他都是可选
            if (UboDevLog.requiredData.contains(paramKey)) {
                required++;
            }
        }

        if (required < UboDevLog.requiredData.size()) {
            throw new IllegalAccessException("ubo-devlog必填参数缺失" + params.toString());
        }

    }


    @Override
    public void setServer(Server server) {

    }
}
