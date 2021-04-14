package com.qbao.search.engine.qbaolog;

import com.alibaba.fastjson.JSONObject;
import com.qbao.search.rpc.Server;
import com.qbao.search.rpc.netty.SimpleHttpRequestHandler;
import com.qbao.search.util.CommonUtil;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class User2UserHandler extends SimpleHttpRequestHandler<String> {
	public static Pattern pattern = Pattern.compile("[0-9]*");

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

		System.out.println("user2item参数:" + "userid=" + userId + " itemid=" + programId + " size=" + size + " time=" + time);
		return parms;
	}

	@Override
	protected String doRun() throws Exception {
		HashMap<String, String> parms = getParms();
		JSONObject jason =new JSONObject();
		Map <String,Object> map=new HashMap<>();
		String time = parms.get("time");
		String programId = parms.get("programId");
		String size = parms.get("size");
		String userId = parms.get("userId");
		try {
			//
			if (userId.length() > 0) {
				Matcher isNum = pattern.matcher(userId);
				if (!isNum.matches()) {
					
					return "{\"data\":[],\"msg\":\"error\",\"result\":0,\"status\":700}";
				}
				
				

				ArrayList<Integer> lst =new ArrayList<>();
				lst.add(2222);
				lst.add(2);
				lst.add(3);
				lst.add(4);
				lst.add(5);
				lst.add(6);
				lst.add(7);
				lst.add(100);
				//#todo
				map.put("data", lst);
				map.put("msg", "ok");
				map.put("result", lst.size());
				map.put("status", 300);
				String jsonlist = JSONObject.toJSONString(map);

				return jsonlist;

				
		
			}

		} catch (Exception e) {
			e.printStackTrace();
			
			return "{\"data\":[],\"msg\":\"error\",\"result\":0,\"status\":444}";
		}

		return "{\"data\":[],\"msg\":\"ok\",\"result\":0,\"status\":200}";
	}

	@Override
	public void setServer(Server server) {
	}
}
