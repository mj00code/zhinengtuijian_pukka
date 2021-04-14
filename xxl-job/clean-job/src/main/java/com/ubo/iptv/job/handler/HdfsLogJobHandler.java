package com.ubo.iptv.job.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ubo.iptv.job.config.HadoopConfig;
import com.ubo.iptv.job.service.EsService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.util.ShardingUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@JobHandler(value = "HdfsLogJobHandler")
@Slf4j
public class HdfsLogJobHandler extends IJobHandler {

    @Value("${hdfs.logpath}")
    private String logPath;
    @Value("${iptv.spark.train.uri}")
    private String trainUri;
    @Autowired
    private EsService esService;
    @Autowired
    private HadoopConfig config;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        long begin = System.currentTimeMillis();
        // 分片参数
        ShardingUtil.ShardingVO shardingVO = ShardingUtil.getShardingVo();
        XxlJobLogger.log("分片参数: 当前分片序号={}, 总分片数={}", shardingVO.getIndex(), shardingVO.getTotal());

        XxlJobLogger.log("开始读取es写入hdfs");
        if (StringUtils.isBlank(param)) {
            //LocalDate date = LocalDate.now().minusDays(1);
            LocalDate date = LocalDate.now();
            sync(date);
        } else if (param.contains("~")) {
            LocalDate start = LocalDate.parse(param.split("~")[0]);
            LocalDate end = LocalDate.parse(param.split("~")[1]);
            while (!start.isAfter(end)) {
                sync(start);
                start = start.plusDays(1);
            }
        } else {
            LocalDate date = LocalDate.parse(param);
            sync(date);
        }
        XxlJobLogger.log("完成读取es写入hdfs，共耗时 {}秒", (System.currentTimeMillis() - begin) / 1000);

        XxlJobLogger.log("开始模型训练");
        if (StringUtils.isBlank(param)) {
            LocalDate date = LocalDate.now();
            train(date);
        } else {
            LocalDate date = LocalDate.parse(param);
            train(date);
        }
        XxlJobLogger.log("完成模型训练，共耗时 {}秒", (System.currentTimeMillis() - begin) / 1000);

        return SUCCESS;
    }

    private void sync(LocalDate date) throws Exception {
        Configuration conf = config.getConfiguration();
        FileSystem fs = FileSystem.get(conf);
        FSDataOutputStream outputStream = null;

        try {

            String path = logPath + "/" + date.toString() + ".txt";
            Path dst = new Path(path);

            if (fs.exists(dst)) {
                outputStream = fs.append(dst);
            } else {
                outputStream = fs.create(dst);
            }
            XxlJobLogger.log("连接hdfs成功");

            SearchResponse response = esService.search(date);
            while (true) {
                if (response == null || response.getHits().getHits().length == 0) {
                    break;
                }
                SearchHits hits = response.getHits();
                List<String> list = new ArrayList<>();
                hits.forEach(hit -> {
                    String json = hit.getSourceAsString();
                    JSONObject jsonObject = JSON.parseObject(json);
                    List<String> line = new ArrayList<>();
                    line.add(jsonObject.getString("user_db_id"));
                    line.add(jsonObject.getString("media_id"));
                    line.add(jsonObject.getString("event_type"));
                    line.add(jsonObject.getString("log_time"));
                    line.add(jsonObject.getString("create_time"));
                    list.add(String.join(",", line));
                });
                if (list.size() > 0) {
                    outputStream.writeBytes(String.join(System.lineSeparator(), list));
                    outputStream.writeBytes(System.lineSeparator());
                    XxlJobLogger.log("批量写入hdfs");
                }
                String scrollId = response.getScrollId();
                response = esService.search(scrollId);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
            fs.close();
        }

    }

    private void train(LocalDate date) {
        JSONObject post = new JSONObject();
        post.put("date", date.toString());
        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(trainUri, post, String.class);
            if (responseEntity != null) {
                XxlJobLogger.log(responseEntity.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
