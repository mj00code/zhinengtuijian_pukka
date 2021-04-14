package com.ubo.iptv.job.daily;

import com.ubo.iptv.job.service.LogFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDate;

@Component
@Slf4j
public class HistoryLogJob {
    @Autowired
    private LogFileService logFileService;



    @Scheduled(cron = "0 0 2 * * ?")
    public void cmccHistory() {
        LocalDate localDate = LocalDate.now().minusDays(1);

        File file = new File("/home/lianping/log/cmcc/" + localDate);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                logFileService.work("m", f, localDate);
            }
        }
    }


    @Scheduled(cron = "0 0 2 * * ?")
    public void ctccHistory() {
        LocalDate localDate = LocalDate.now().minusDays(1);

        File file = new File("/home/lianping/log/ctcc/" + localDate);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                logFileService.work("t", f, localDate);
            }
        }
    }


    @Scheduled(cron = "0 0 2 * * ?")
    public void cuccHistory() {
        LocalDate localDate = LocalDate.now().minusDays(1);

        File file = new File("/home/lianping/log/cucc/" + localDate);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                logFileService.work("u", f, localDate);
            }
        }
    }


}
