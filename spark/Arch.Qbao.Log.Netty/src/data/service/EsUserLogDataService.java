package data.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.qbao.search.conf.Config;
import com.qbao.search.conf.LoadValues;
import com.qbao.search.logging.ESLogger;
import com.qbao.search.logging.Loggers;

import po.UboUserLogPo;
import util.DateUtil;

import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.*;

public class EsUserLogDataService {

	private static ESLogger logger = Loggers.getLogger(EsUserLogDataService.class);
	private static EsUserLogDataService esDataService;
	private static TransportClient client;
	private static Connection conn;
	private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static final EsUserLogDataService getInstance() {
		try {
			if (esDataService == null) {
				synchronized (EsUserLogDataService.class) {
					// -----------------es集群连接--------------------

					Settings settings = Settings.builder().put("cluster.name", Config.get().get("es.cluster.name")) // 设置集群名
							.put("client.transport.ignore_cluster_name", true)// 忽略集群名字验证,打开后集群名字不对也能连接上
							// .put("client.transport.sniff", true) // 自动侦听新增节点
							.build();

					System.out.println(Config.get().get("es.host1") + Config.get().getInt("es.port", 9300));

					client = new PreBuiltTransportClient(settings)
							.addTransportAddress(new TransportAddress(new InetSocketAddress(
									Config.get().get("es.host1"), Config.get().getInt("es.port", 9300))))
							.addTransportAddress(new TransportAddress(new InetSocketAddress(
									Config.get().get("es.host2"), Config.get().getInt("es.port", 9300))))
							.addTransportAddress(new TransportAddress(new InetSocketAddress(
									Config.get().get("es.host3"), Config.get().getInt("es.port", 9300))));
					esDataService = new EsUserLogDataService();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return esDataService;

	}

	/**
	 * 日志存放
	 *
	 * @param paramMapList
	 *            清理过后的日志数据
	 */
	// public void saveQbaoLog(Map paramMap) {
	public void saveUBdevlog(Map paramMap) {
		// String logId = UUID.randomUUID().toString();
		// paramMap.put("_id", logId);
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		bulkRequest
				.add(client.prepareIndex(LoadValues.UB_DEVLOG_INDEX, LoadValues.UB_DEVLOG_INDEX).setSource(paramMap));

		BulkResponse responses = bulkRequest.execute().actionGet();
		// BulkResponse responses = bulkRequest.get();
		if (responses.hasFailures()) {
			int eRow = 0;
			BulkItemResponse[] items = responses.getItems();
			for (BulkItemResponse item : items) {
				if (item.isFailed()) {
					eRow++;
					System.out.println("log saveQbaoLog error message = {}" + item.getFailureMessage());
					logger.error("log saveQbaoLog error message = {}", item.getFailureMessage());
				}
			}
		}
		logger.info("EsDataService.saveQbaoLog,logId=" + paramMap.keySet());
	}

	public void saveLogBatch(String jsonList) {

		try {
			int count = 0;
			JSONArray jsonArray = JSON.parseArray(jsonList);
			// 开启批量插入
			BulkRequestBuilder bulkRequest = client.prepareBulk();
			for (Object json : jsonArray) {
				count++;
				Map paramMap = JSON.parseObject(json.toString(), Map.class);
				//paramMap.put("stamp", paramMap.get("stamp")+"");
				paramMap.put("x", paramMap.get("x")+"");//不转化的es不认
				paramMap.put("y", paramMap.get("y")+"");//不转化的es不认
				dateTimeGet( paramMap);//生成日期
				bulkRequest.add(
						client.prepareIndex(LoadValues.UB_USERLOG_INDEX, LoadValues.UB_USERLOG_INDEX).setSource(paramMap));// 每一千条提交一次
				if (count % 10000 == 0) {
					bulkRequest.execute().actionGet();
					bulkRequest = client.prepareBulk();
					System.out.println("分批提交到：" + count);
				}
				

			}
			bulkRequest.execute().actionGet();
			System.out.println("提交完毕,sum="+count);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 日志存放
	 *
	 * @param paramMapList
	 *            清理过后的日志数据
	 */
	// public void saveQbaoLog(Map paramMap) {
	public void saveQbaoLog(List<Object> paramMapList) {
		BulkRequestBuilder bulkRequest = client.prepareBulk();

		for (Object object : paramMapList) {
			Map paramMap = JSON.parseObject(object.toString(), Map.class);
			String logId = UUID.randomUUID().toString();
			paramMap.put("logId", logId);

			paramMap.forEach((k, v) -> {
				if (!(v instanceof String))
					v = JSON.toJSONString(v);
				paramMap.put(k, v);
			});
			bulkRequest.add(client.prepareIndex(LoadValues.QBAO_LOG_INDEX, LoadValues.QBAO_LOG_INDEX).setId(logId)
					.setSource(paramMap));
		}

		BulkResponse responses = bulkRequest.execute().actionGet();
		if (responses.hasFailures()) {
			int eRow = 0;
			BulkItemResponse[] items = responses.getItems();
			for (BulkItemResponse item : items) {
				if (item.isFailed()) {
					eRow++;
					System.out.println("log saveQbaoLog error message = {}" + item.getFailureMessage());
					logger.error("log saveQbaoLog error message = {}", item.getFailureMessage());
				}
			}
			logger.info("log saveQbaoLog size = {} error save = {}", paramMapList.size(), eRow);
		}
		logger.info("EsDataService.saveQbaoLog");
	}
	
	
	public void dateTimeGet(Map paramMap) {
		// 手转转换时间
		if (paramMap != null && paramMap.get("stamp") != null) {
			try {
				
				paramMap.put("date_time", DateUtil
						.formatDate(new Date(Long.valueOf(paramMap.get("stamp").toString().trim())), "yyyy-MM-dd HH:mm:ss"));
				paramMap.put("date_day", DateUtil
						.formatDate(new Date(Long.valueOf(paramMap.get("stamp").toString().trim())), "yyyy-MM-dd"));
				paramMap.put("date_hour", DateUtil
						.formatDate(new Date(Long.valueOf(paramMap.get("stamp").toString().trim())), "yyyy-MM-dd HH"));
			} catch (Exception e) {
				logger.error(e);
			}

		}
	}
	
	
    
    
}
