package com.qbao.search.engine.qbaolog;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.qbao.search.engine.result.ResultView;
import com.qbao.search.logging.ESLogger;
import com.qbao.search.logging.Loggers;
import com.qbao.search.rpc.Server;
import com.qbao.search.rpc.netty.EditResponseHttpHandler;
import com.qbao.search.util.CommonUtil;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * 日志请求处理器
 * 
 * 
 *
 * @author song.j
 * @create 2017-07-11 15:15:37
 **/
public class UserLogHandler extends EditResponseHttpHandler<ResultView> {

	private static ESLogger logger = Loggers.getLogger(UserLogHandler.class);
	public static long count = 1;

	@Override
	protected ResultView doRun() throws Exception {

		String method = httpRequest.getMethod().getName();
		Map paramMap = null;
		JSONArray jsonArray = null;
		// 判断请求方法类型。进行数据处理
		if (method.equals("GET")) {

			paramMap = CommonUtil.getParamMap(httpRequest);

		} else if (method.equals("POST")) {
			try {
				logger.info(count + "  ===userlog========" + new Date() + "  ============ post http!!!");
				// System.out.println(count+" ==========="+ new
				// Date()+"============ post http!!!");
				count++;

				String jsonParam = new String(httpRequest.getContent().array()).trim();// 长json报错用trim解决
				// logger.info("＋＋＋＋＋＋"+jsonParam);

				// jsonLIST插入
				putInJsonList( jsonParam);

				// 单个json插入
				jsonArray = JSON.parseArray(jsonParam);
				// for (Object json : jsonArray) {
				// paramMap = JSON.parseObject(json.toString(), Map.class);
				// putIn(paramMap);
				//
				// }

				System.out.println("Userlog post-----" + count + "+++" + new Date() );
				//util.FileUtil.inputTxt("/Volumes/D/jsondemo.txt", "-------------MSserver++++++sum=" + count+ "----------------, " + new Date().toString() + "+++" + "\r\n");
			} catch (Exception e) {
				logger.info(e.getMessage());
				return ResultView.fail(e.getMessage(), HttpResponseStatus.BAD_REQUEST);
			}

		}

		return ResultView.succ();
	}

//	private void putIn(Map paramMap) throws Exception {
//		LogService logService = new LogService();
//
//		invokeParam(paramMap);
//
//		dateTimeGet(paramMap);
//
//		logService.saveLog(paramMap);
//
//	}

	private void putInJsonList(String jsonParam) throws Exception {
		UserLogService logService = new UserLogService();
		logService.saveLogbatch(jsonParam);
	}

	@Override
	protected void editResponse(HttpResponse response) {
		response.setHeader("Content-Type", "application/json; charset=UTF-8");

		// 支持跨域请求
		response.addHeader("access-control-allow-origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "OPTIONS,POST,GET");
		response.addHeader("Access-Control-Allow-Headers", "x-requested-with,content-type");
	}

	/**
	 * 参数校检
	 *
	 * @throws IllegalAccessException
	 *             异常参数返回
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

//	public void dateTimeGet(Map paramMap) {
//		// 手转转换时间
//		if (paramMap != null && paramMap.get("stamp") != null) {
//			try {
//				paramMap.put("date_time", DateUtil
//						.formatDate(new Date(Long.valueOf(paramMap.get("stamp").toString().trim())), "yyyy-MM-dd HH:mm:ss"));
//			} catch (Exception e) {
//				logger.error(e);
//			}
//
//		}
//	}

	@Override
	public void setServer(Server server) {

	}
}
