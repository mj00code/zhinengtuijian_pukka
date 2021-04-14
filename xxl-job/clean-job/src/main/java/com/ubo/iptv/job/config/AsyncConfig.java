package com.ubo.iptv.job.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableAsync
public class AsyncConfig {

    private int corePoolSize = 10;
    private int maxPoolSize = 20;
    private int keepAliveTime = 20;
    private int workQueueSize = 100;

    private TimeUnit timeUnit = TimeUnit.SECONDS;


    @Bean(name = "fileTaskExecutor")
    public ThreadPoolTaskExecutor bookTaskExecutor() {
        //newFixedThreadPool
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(corePoolSize);
        // 设置最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        // 设置队列容量
        executor.setQueueCapacity(workQueueSize);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(keepAliveTime);
        // 设置默认线程名称
        executor.setThreadNamePrefix("daily_log_job");
        // 设置拒绝策略
        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }
}
