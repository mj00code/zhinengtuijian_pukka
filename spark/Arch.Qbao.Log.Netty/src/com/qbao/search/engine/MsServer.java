package com.qbao.search.engine;

import com.qbao.search.conf.Config;
import com.qbao.search.conf.LoadConfig;
import com.qbao.search.engine.service.SparkAlsModelService;
import com.qbao.search.engine.service.SparkAlsModelTrainService;
import com.qbao.search.engine.service.SparkItemSimMatrixService;
import com.qbao.search.logging.ESLogger;
import com.qbao.search.logging.Loggers;
import com.qbao.search.rpc.netty.HttpRequestParser;
import com.qbao.search.rpc.netty.HttpServer;
import util.DateUtil;
import util.Properties;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

/**
 * 用户画像启动容器
 *
 * @author fanyunlong
 */
public class MsServer extends HttpServer {

    private static ESLogger logger = Loggers.getLogger(MsServer.class);

    final protected MsParser LeixsParser;

    public MsServer(boolean shouldRecover) throws Throwable {
        super("LeixsServer", true);
        LeixsParser = new MsParser();

    }

    @Override
    public void stop() throws Exception {
        try {
            super.stop();
        } finally {
        }
    }

    /**
     * @param args
     * @throws Throwable
     */

    public static void main(String[] args) throws Throwable {
        try {
            System.out.println(System.getProperty("user.dir"));
            FileInputStream is = null;
            is = new FileInputStream("D:\\贵州小程序\\iptv-gz\\spark\\conf\\loadConfig.properties");
            System.out.println(System.getProperty("user.dir"));
            Config.get().set("Root.path", new File(System.getProperty("user.dir")).getParentFile().getPath());
            LoadConfig.get();

            Date endDate = DateUtil.stringToDate(Config.get().get("model.load.date"), "yyyy-MM-dd");
            if (endDate != null) {
                String ALSmodel_save_hadoop_path = SparkAlsModelTrainService.get().get_ALSmodel_save_hadoop_path();
                String itemSimMatrix_hadoop_path = SparkAlsModelTrainService.get().get_itemSimMatrix_save_hadoop_path();
                // 加载模型
                SparkAlsModelService.get().load(endDate, ALSmodel_save_hadoop_path);
                SparkItemSimMatrixService.get().load(endDate, itemSimMatrix_hadoop_path);
            }

            // 先加载所有字典到内存
            // initAll.initAllDic();

            // 加载定时任务
            // QuartzTaskManager.getInstance();

            // 日志队理job REDIS
            // new QbaoLogJob().start();

            new MsServer(args.length > 0 && args[0].equals("recover")).start(Properties.port);

            System.out.println("+++++ Engine.port = " + Config.get().get("engine.port"));
            System.out.println("+++++ Engine start ok!");

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    @Override
    protected HttpRequestParser getHttpRequestParser() {
        return LeixsParser;
    }

}