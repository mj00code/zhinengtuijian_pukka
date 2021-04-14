package po;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UboUserLogPo {
	
	/**
	 "uuid": "13899888888",
	  "uid": "13899888888",
	  "pageId": "ub-01",
	  "companyId": "ubo",
	  "productId": "atone",
	  "marketId": "38festival",
	  "channelId": "toutiao",
	  "client": "2",
	  "appName": "ubo-webchat",
	  "appVersion": "1.0.1",
	  "buttonId": "123",
	  "joinCode": "38-toutiao",
	  "windowId": "22",
	  "title": "uno tech ",
	  "event": "1",
	  "stamp": 1543310052169,
	  "data": "[\"E1\",\"E2\",\"E3\"]",
	  "stay": "9",
	  "clientVersion": "mac1.2.3",
	  "referer": "www.ubocare.com/version/1.12.2/getweisio",
	  "ip": "192.168.1.1",
	  "x": 132.23456,
	  "y": 31.22215,
	  "data_time": "2018-11-23 10:12:11"
	
	*/

	
	private String uuid;
	private String uid;
	private String pageId;
	private String companyId;
	private String productId;
	private String marketId;
	private String channelId;
	private String client;
	private String appName;
	private String appVersion;
	private String buttonId;
	private String joinCode;
	private String windowId;
	private String title;
	private String event;
	private String data;
	private String stay;
	private String clientVersion;
	private String referer;
	private String ip;
	
	private float x;
	
	private float y;

	private long stamp;
	
	//private String data_time;  由stamp算计算出来，不需要
	
	
	public String getUuid() {
		return uuid;
	}


	public void setUuid(String uuid) {
		this.uuid = uuid;
	}


	public String getUid() {
		return uid;
	}


	public void setUid(String uid) {
		this.uid = uid;
	}


	public String getPageId() {
		return pageId;
	}


	public void setPageId(String pageId) {
		this.pageId = pageId;
	}


	public String getCompanyId() {
		return companyId;
	}


	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}


	public String getProductId() {
		return productId;
	}


	public void setProductId(String productId) {
		this.productId = productId;
	}


	public String getMarketId() {
		return marketId;
	}


	public void setMarketId(String marketId) {
		this.marketId = marketId;
	}


	public String getChannelId() {
		return channelId;
	}


	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}


	public String getClient() {
		return client;
	}


	public void setClient(String client) {
		this.client = client;
	}


	public String getAppName() {
		return appName;
	}


	public void setAppName(String appName) {
		this.appName = appName;
	}


	public String getAppVersion() {
		return appVersion;
	}


	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}


	public String getButtonId() {
		return buttonId;
	}


	public void setButtonId(String buttonId) {
		this.buttonId = buttonId;
	}


	public String getJoinCode() {
		return joinCode;
	}


	public void setJoinCode(String joinCode) {
		this.joinCode = joinCode;
	}


	public String getWindowId() {
		return windowId;
	}


	public void setWindowId(String windowId) {
		this.windowId = windowId;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getEvent() {
		return event;
	}


	public void setEvent(String event) {
		this.event = event;
	}


	public String getData() {
		return data;
	}


	public void setData(String data) {
		this.data = data;
	}


	public String getStay() {
		return stay;
	}


	public void setStay(String stay) {
		this.stay = stay;
	}


	public String getClientVersion() {
		return clientVersion;
	}


	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}


	public String getReferer() {
		return referer;
	}


	public void setReferer(String referer) {
		this.referer = referer;
	}


	public String getIp() {
		return ip;
	}


	public void setIp(String ip) {
		this.ip = ip;
	}


	public float getX() {
		return x;
	}


	public void setX(float x) {
		this.x = x;
	}


	public float getY() {
		return y;
	}


	public void setY(float y) {
		this.y = y;
	}


	public long getStamp() {
		return stamp;
	}


	public void setStamp(long stamp) {
		this.stamp = stamp;
	}





	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
