package com.ubo.iptv.logstash.entity;

import com.ubo.iptv.core.util.DateUtil;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
public class IptvLog {
    private String id;//uid+log_time+seqid+mediaId

    //公共字段
    private String sys_id;
    private String user_id;
    private Long log_time;//ms
    private String seqid;
    private String event_type;

    private Integer media_id;
    private String media_name;
    private Integer media_type;
    private Integer media_type_id;
    private String media_type_name;
    private String[] media_kind_id;
    private String[] media_kind_name;

    private Integer user_db_id;
    private Long scene_id;
    private String scene_name;
    private String create_time;
    private String create_day;
    /**
     * 演员
     */
    private String[] actor;

    /**
     * 导演
     */
    private String[] director;
    /**
     * 凌晨：0~6;早上：6~9；中：9~12；下午：12~18；晚上：18~24
     */
    private int part_of_day;

    private String update_time = DateUtil.format(LocalDateTime.now());

    public void compose() {
        this.id = String.join("-", sys_id, user_id, log_time.toString(), seqid);
        if (log_time != null) {
            LocalDateTime dateTime = LocalDateTime.ofEpochSecond(log_time / 1000, 0, ZoneOffset.ofHours(8));
            this.create_time = DateUtil.format(dateTime);
            if (dateTime.isAfter(LocalDate.of(2020, 10, 1).atStartOfDay())) {
                this.create_day = DateUtil.format(dateTime, "yyyyMMdd");
            }
            int hour = dateTime.getHour();
            if (hour < 6) this.part_of_day = 1;
            else if (hour < 9) this.part_of_day = 2;
            else if (hour < 12) this.part_of_day = 3;
            else if (hour < 18) this.part_of_day = 4;
            else if (hour < 24) this.part_of_day = 5;
        }
    }
}
