package com.ubo.iptv.logstash.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class MainConstants {
    @Value("${producer.thread_count}")
    public int threadCount;
    @Value("${producer.collect_count}")
    public int collectCount;
    @Value("${producer.wait_second}")
    public int waitSecond;
}
