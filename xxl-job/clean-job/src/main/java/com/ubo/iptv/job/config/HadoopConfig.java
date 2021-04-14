package com.ubo.iptv.job.config;

import lombok.Data;
import org.apache.hadoop.conf.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@ConfigurationProperties(prefix = "hdfs")
@Component
@Data
public class HadoopConfig {
    private String nameservices;
    private String[] namenodesAddr;
    private String[] namenodes;

    private static Configuration configuration;

    public void init() {
        configuration = new Configuration(false);
//        String nameservices = "mycluster";
//        String[] namenodesAddr = {"192.168.79.90:8020", "192.168.79.91:8020"};
//        String[] namenodes = {"nn1", "nn2"};
        configuration.set("fs.defaultFS", "hdfs://" + nameservices);
        configuration.set("dfs.nameservices", nameservices);
        configuration.set("dfs.ha.namenodes." + nameservices, namenodes[0] + "," + namenodes[1]);
        configuration.set("dfs.namenode.rpc-address." + nameservices + "." + namenodes[0], namenodesAddr[0]);
        configuration.set("dfs.namenode.rpc-address." + nameservices + "." + namenodes[1], namenodesAddr[1]);
        configuration.set("dfs.client.failover.proxy.provider." + nameservices, "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider");
        configuration.setBoolean("dfs.support.append", true);
    }

    public Configuration getConfiguration() {
        if (configuration == null) {
            init();
        }
        return configuration;
    }
}
