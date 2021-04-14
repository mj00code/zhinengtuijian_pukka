package com.qbao.search.engine.qbaolog;

import com.alibaba.fastjson.JSON;

import data.service.EsDataService_1;
import data.service.EsDataService;
 

import java.util.Map;

/**
 * 日志数据服务
 *
 * @author song.j
 * @create 2017-07-11 15:15:37
 **/
public class LogService {
	// public static RedisUtil redisUtil = new RedisUtil();
	public boolean saveLog(Map paramMap) {

		// redisUtil.rpush(RedisConst.TEGNRONG_ONEDAY_QBAOLOG_QUEUE.key,
		// JSON.toJSONString(paramMap));

		EsDataService.getInstance().saveUBdevlog(paramMap);

		// EsDataService_1 es = new EsDataService_1();
		// es.connect();
		// es.saveUBdevlog(paramMap);

		return true;
	}
	
	public boolean saveLogbatch(String paramMap) {
		EsDataService.getInstance().saveLogBatch(paramMap);
		

		return true;
	}
}
