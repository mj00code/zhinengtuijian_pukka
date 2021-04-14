package com.qbao.search.engine.qbaolog;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author song.j
 * @create 2017-07-11 16:16:52
 **/
public class UboDevLog {

    private String device_id;
    private String ip;
    private String messageid;
    private String method;
    private String device_type;
	//"data": "{'messageid': 'string消息id','method': 'up.hardware.info','device_type': 'string, 设备产品型号','hardware_version': 'string,  固件版本号'} ",
    private String data;
	//timestampe
    private String time;
	//"data_time": "2018-11-23 10:12"
    private String date_time;

    public String getDate_time() {
		return date_time;
	}


	public void setDate_time(String date_time) {
		this.date_time = date_time;
	}


	/**
     * 必填参数
     */
    public static final List<String> requiredData = Arrays.asList(new String[]
            {"device_id", "systime"});


    /**
     * 所有参数list集合
     */
    public static final List<String> elmData = Arrays.asList(new String[]
            {"device_id", "ip", "messageid", "method", "device_type", "data", "time", "data_time"
            });


	public String getDevice_id() {
		return device_id;
	}


	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}


	public String getIp() {
		return ip;
	}


	public void setIp(String ip) {
		this.ip = ip;
	}


	public String getMessageid() {
		return messageid;
	}


	public void setMessageid(String messageid) {
		this.messageid = messageid;
	}


	public String getMethod() {
		return method;
	}


	public void setMethod(String method) {
		this.method = method;
	}


	public String getDevice_type() {
		return device_type;
	}


	public void setDevice_type(String device_type) {
		this.device_type = device_type;
	}


	public String getData() {
		return data;
	}


	public void setData(String data) {
		this.data = data;
	}


	public String getTime() {
		return time;
	}


	public void setTime(String time) {
		this.time = time;
	}


	public static List<String> getRequireddata() {
		return requiredData;
	}


	public static List<String> getElmdata() {
		return elmData;
	}
    
    
    
    


}
