package data.service;

import com.alibaba.fastjson.JSON;
import com.qbao.search.conf.Config;
import com.qbao.search.conf.LoadValues;
import com.qbao.search.logging.ESLogger;
import com.qbao.search.logging.Loggers;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.*;

public class EsDataService_1 {

	private static ESLogger logger = Loggers.getLogger(EsDataService_1.class);

	private static TransportClient client;
	private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public void connect() {
		try {
		
			 
					// -----------------es集群连接--------------------
					
					Settings settings = Settings.builder().put("cluster.name", Config.get().get("es.cluster.name")) // 设置集群名
							.put("client.transport.ignore_cluster_name", true)// 忽略集群名字验证,打开后集群名字不对也能连接上
							// .put("client.transport.sniff", true) // 自动侦听新增节点
							.build();

					logger.info(df.format(new Date()) +"  ++  " +Config.get().get("es.host1")+":"+Config.get().getInt("es.port", 9300));
					//System.out.println(df.format(new Date()) +"  ++  " +Config.get().get("es.host1")+":"+Config.get().getInt("es.port", 9300));
					
		
		
					client = new PreBuiltTransportClient(settings).addTransportAddress(new TransportAddress(new InetSocketAddress(Config.get().get("es.host1"), Config.get().getInt("es.port", 9300))))
							.addTransportAddress(new TransportAddress(new InetSocketAddress(Config.get().get("es.host2"), Config.get().getInt("es.port", 9300))))
							.addTransportAddress(new TransportAddress(new InetSocketAddress(Config.get().get("es.host3"), Config.get().getInt("es.port", 9300))))
							;
				
				
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	

	}

	
	/**
	 * 日志存放
	 *
	 * @param paramMapList
	 * 清理过后的日志数据
	 */
	// public void saveQbaoLog(Map paramMap) {
	public void saveUBdevlog(Map paramMap) {
		//String logId = UUID.randomUUID().toString();
		//paramMap.put("_id", logId);
		BulkRequestBuilder bulkRequest = client.prepareBulk();
			bulkRequest.add(client.prepareIndex(LoadValues.UB_DEVLOG_INDEX, LoadValues.UB_DEVLOG_INDEX)
					.setSource(paramMap));
		BulkResponse responses = bulkRequest.get();
		if (responses.hasFailures()) {
			int eRow = 0;
			BulkItemResponse[] items = responses.getItems();
			for (BulkItemResponse item : items) {
				if (item.isFailed()) {
					eRow++;
					System.out.println("log saveQbaoLog error message = {}"+item.getFailureMessage());
					logger.error("log saveQbaoLog error message = {}", item.getFailureMessage());
				}
			}
		}
		logger.info("EsDataService.saveQbaoLog,logId="+paramMap.keySet());
		//client.close();
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
					System.out.println("log saveQbaoLog error message = {}"+item.getFailureMessage());
					logger.error("log saveQbaoLog error message = {}", item.getFailureMessage());
				}
			}
			logger.info("log saveQbaoLog size = {} error save = {}", paramMapList.size(), eRow);
		}
		logger.info("EsDataService.saveQbaoLog");
	}
}
