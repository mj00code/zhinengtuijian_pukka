package com.qbao.search.engine;


import com.qbao.search.engine.qbaolog.*;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;

import com.qbao.search.rpc.netty.HttpRequestHandler;
import com.qbao.search.rpc.netty.RestParser;

public class MsParser extends RestParser {

	public MsParser() {

		
		addHandler(HttpMethod.HEAD, "/monitor.jsp", MonitorHandler.class);
		addHandler(HttpMethod.GET, "/monitor.jsp", MonitorHandler.class);
		addHandler(HttpMethod.POST, "/monitor.jsp", MonitorHandler.class);
		addHandler(HttpMethod.POST, "/ub/devlog", LogHandler.class);
		addHandler(HttpMethod.POST, "/ub/userlog", UserLogHandler.class);


		addHandler(HttpMethod.POST, "/v1/iptv/recommend/item2item", Item2ItemPostHandler.class);
		addHandler(HttpMethod.POST, "/v1/iptv/recommend/user2item", User2ItemPostHandler.class);
		addHandler(HttpMethod.POST, "/v1/iptv/recommend/train", TrainPostHandler.class);

		addHandler(HttpMethod.GET, "/v1/iptv/recommend/user2user", User2UserHandler.class);
		addHandler(HttpMethod.GET, "/v1/iptv/recommend/item2item", Item2ItemHandler.class);
		addHandler(HttpMethod.GET, "/v1/iptv/recommend/user2item", User2ItemHandler.class);
		
	}
	
	

	@Override
	public HttpRequestHandler parse(HttpRequest httpRequest) throws Exception {
		if(httpRequest.getUri().contains("%22")) {
			httpRequest.setUri("/favicon.ico");
		}
		HttpRequestHandler handler = super.parse(httpRequest);
		return handler;
	}
}
