package com.ubo.iptv.job.handler;

import com.ubo.iptv.job.service.EsService;
import com.ubo.iptv.job.service.LogFileService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.util.ShardingUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Service
@JobHandler(value = "historyLogJobHandler")
@Slf4j
public class HistoryLogJobHandler extends IJobHandler {

    @Autowired
    private LogFileService logFileService;
    @Autowired
    private EsService esService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        long begin = System.currentTimeMillis();
        // 分片参数
        ShardingUtil.ShardingVO shardingVO = ShardingUtil.getShardingVo();
        XxlJobLogger.log("分片参数: 当前分片序号={}, 总分片数={}", shardingVO.getIndex(), shardingVO.getTotal());


        esService.alias();
        log.info("完成设置别名");
        if (StringUtils.isBlank(param)) {
            LocalDate date = LocalDate.now().minusDays(1);
            historyClean(date);
        } else if (param.contains("~")) {
            LocalDate start = LocalDate.parse(param.split("~")[0]);
            LocalDate end = LocalDate.parse(param.split("~")[1]);
            while (!start.isAfter(end)) {
                historyClean(start);
                start = start.plusDays(1);
            }
        } else {
            LocalDate date = LocalDate.parse(param);
            historyClean(date);
        }

        log.info("完成处理昨日日志");
        XxlJobLogger.log("任务完成：共耗时 {}秒", (System.currentTimeMillis() - begin) / 1000);
        return SUCCESS;
    }

    private void historyClean(LocalDate localDate) {
        log.info("开始处理昨日日志:" + localDate.toString());
        List<File> paths = new ArrayList<>();
        paths.add(new File("/home/lianping/log/cmcc/part1/" + localDate));
        paths.add(new File("/home/lianping/log/cmcc//part2/" + localDate));
        paths.add(new File("/home/lianping/log/ctcc/" + localDate));
        paths.add(new File("/home/lianping/log/cucc/" + localDate));


        List<File> fileList = new ArrayList<>();
        for (File path : paths) {
            if (path.exists() && path.isDirectory()) {
                fileList.addAll(Arrays.asList(path.listFiles()));
            }
        }
        final CountDownLatch count = new CountDownLatch(fileList.size());
        for (File f : fileList) {
            logFileService.work(f, localDate, count);
            try {
                //文件数量太多comsumer并发处理不过来
                Thread.currentThread().sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            count.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
