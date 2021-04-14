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

//开发步骤：
// 1、继承“IJobHandler”:"com.xxl.job.core.handler.IJobHandler"
// 2、注册到spring容器：添加“@Component”注解，被Spring容器扫描为Bean实例；
// 3、注册到执行器工厂：添加“@JobHandler(value=“自定义jobhandler名称”)”注解，注解value值对应的是
//   调度中心新建任务的JobHandler属性的值。
// 4、执行日志：需要通过“XxlJobLogger.log” 打印执行日志；
@Service
//定时任务名称
@JobHandler(value = "TestJobHandler")
@Slf4j
public class TestJobHandler extends IJobHandler {

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        long begin = System.currentTimeMillis();
        // 分片参数
        ShardingUtil.ShardingVO shardingVO = ShardingUtil.getShardingVo();
        XxlJobLogger.log("分片参数: 当前分片序号={}, 总分片数={}", shardingVO.getIndex(), shardingVO.getTotal());

        XxlJobLogger.log("开始读取es写入hdfs");
        if (StringUtils.isBlank(param)) {LocalDate date = LocalDate.now().minusDays(1);
            System.out.println(date + "---》测试成功");}
        return SUCCESS;
    }
}
//启动并登录 XXL-JOB-admin
//web界面配置任务流程
//1、配置执行器,自动注册，配置任务aap-name,自动导入
//2、进入任务管理页面，选择配置的执行器，点击新增任务，配置对应的相关参数
//3、返回任务管理页面,点击执行，测试执行结果