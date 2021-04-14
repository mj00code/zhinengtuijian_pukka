package com.ubo.iptv.mybatis;

import lombok.Data;

/**
 * @Author SHAWN LIAO
 * @ClassName LogBody
 * @Date 2021/3/24 10:54
 * @Description nats入参
 */
@Data
public class LogBody {
    private String de_ua;
    private String log_time;
    private String sdk_version;
    private String user_id;
    private String nick;
    private String user_type;
    private String nettype;
    private String sys_id;
    private String ostype;
    private String osversion;
    private String source_channel;
    private String app_version;
    private String event_type;
    private String seqid;
    private String media_code;
    private String area_num;
    private String content_type;
    private String strategy_log_id;
    private String searchkey;
    private String page_id;
    private String refer_page_id;
    private String page_url;
    private String refer_page_url;
}
