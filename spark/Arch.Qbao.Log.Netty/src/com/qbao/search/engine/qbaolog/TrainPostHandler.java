package com.qbao.search.engine.qbaolog;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qbao.search.engine.service.*;
import com.qbao.search.logging.ESLogger;
import com.qbao.search.logging.Loggers;
import com.qbao.search.rpc.Server;
import com.qbao.search.rpc.netty.SimpleHttpRequestHandler;
import org.apache.spark.mllib.linalg.distributed.MatrixEntry;
import scala.Tuple2;
import util.DateUtil;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class TrainPostHandler extends SimpleHttpRequestHandler<String> {
    public static Pattern pattern = Pattern.compile("[0-9]*");
    public static SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss:SSS");
    private static ESLogger logger = Loggers.getLogger(User2ItemPostHandler.class);

    public HashMap<String, Object> getParms() throws UnsupportedEncodingException {
        String jsonParam = new String(httpRequest.getContent().array()).trim();// 长json报错用trim解决
        JSONObject jsonObject = JSON.parseObject(jsonParam);
        String date = jsonObject.getString("date");
        Date endDate = DateUtil.stringToDate(date, "yyyy-MM-dd");
        if (endDate == null) {
            endDate = new Date();
        }

        HashMap<String, Object> parms = new HashMap<>();
        parms.put("endDate", endDate);
        parms.put("skipUserActionData", jsonObject.getBooleanValue("skipUserActionData"));
        parms.put("skipAlsModel", jsonObject.getBooleanValue("skipAlsModel"));
        parms.put("skipItemSimMatrix", jsonObject.getBooleanValue("skipItemSimMatrix"));
        System.out.println("参数==train:" + "endDate=" + DateUtil.formatDate(endDate, "yyyy-MM-dd") + " time=" + sdf.format(new Date()));

        return parms;
    }

    @Override
    protected String doRun() throws Exception {
        HashMap<String, Object> parms = getParms();
        Date endDate = (Date) parms.get("endDate");
        Boolean skipUserActionData = (Boolean) parms.get("skipUserActionData");
        Boolean skipAlsModel = (Boolean) parms.get("skipAlsModel");
        Boolean skipItemSimMatrix = (Boolean) parms.get("skipItemSimMatrix");

        String log_event_path = SparkAlsModelTrainService.get().get_log_event_hadoop_path();
        int daysWindowsFromToday = SparkAlsModelTrainService.get().get_event_log_days_window_from_endDay();
        HashMap<String, Float> action_rate_map = SparkAlsModelTrainService.get().get_user_item_action_rate();
        SparkAlsModelTrainService.get().get_interest_function_factor_A();
        SparkAlsModelTrainService.get().get_interest_function_factor_B();
        SparkAlsModelTrainService.get().get_interest_function_factor_C();
        SparkAlsModelTrainService.get().get_user2itemMatrix_value_minimum_should_great_than();
        String user_item_rate_path = SparkAlsModelTrainService.get().get_user_movie_data_hadoop_path();

        String ALSmodel_save_hadoop_path = SparkAlsModelTrainService.get().get_ALSmodel_save_hadoop_path();

        String itemSimMatrix_hadoop_path = SparkAlsModelTrainService.get().get_itemSimMatrix_save_hadoop_path();
        float minimunDate = SparkAlsModelTrainService.get().get_iitemSimMatrix_value_minimum_should_great_than();

        HashMap<Integer, Double> factor_map = SparkAlsModelTrainService.get().get_interst_factor_everday_from_endDay();

        // 根据user-item 合并分数，得到一个user-item－rate的坐标矩阵
        if (!skipUserActionData) {
            SparkUserActionDataService.get().train(endDate, daysWindowsFromToday, log_event_path, user_item_rate_path, factor_map, action_rate_map);
        }

        // 开始训练模型
        if (!skipAlsModel) {
            SparkAlsModelTrainService.get().train(endDate, user_item_rate_path, ALSmodel_save_hadoop_path);
            // 加载模型
            SparkAlsModelService.get().load(endDate, ALSmodel_save_hadoop_path);
            // 取样user
            List<Tuple2<Object, double[]>> userRDD = SparkAlsModelService.get().model.userFeatures().toJavaRDD().takeSample(false, 10);
            // 测试user2item模型 "
            for (Tuple2<Object, double[]> user : userRDD) {
                int usr = (int) user._1;
                long start = System.currentTimeMillis();
                SparkAlsModelService.get().user2itemPredict(usr + "", "100");
                System.out.println(usr + "==================model user2item test ok ,time= " + (System.currentTimeMillis() - start));
            }
        }
        if (!skipItemSimMatrix) {
            SparkItemSimMatrixTrainService.get().train(endDate, user_item_rate_path, minimunDate, itemSimMatrix_hadoop_path);
            // 加载模型
            SparkItemSimMatrixService.get().load(endDate, itemSimMatrix_hadoop_path);
            // 取样item
            List<MatrixEntry> productRDD = SparkItemSimMatrixService.ratingRDD.takeSample(false, 10);
            // 测试item2item 模型
            for (MatrixEntry prd : productRDD) {
                long itm = prd.i();
                long start = System.currentTimeMillis();
                SparkItemSimMatrixService.get().item2itemPredict(itm + "", 100);
                System.out.println(itm + "==================model item2item test ok ,time= " + (System.currentTimeMillis() - start));
            }
        }

        // 全量
        // List<Tuple2<Object, double[]>> userRDD
        // =SparkAlsModelService.get().model.userFeatures().toJavaRDD().collect();
        // List<Tuple2<Object, double[]>> productRDD
        // =SparkAlsModelService.get().model.productFeatures().toJavaRDD().collect();

        return "Train Complete";
    }

    @Override
    public void setServer(Server server) {
    }
}
