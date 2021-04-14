package com.ubo.iptv.job.entity;

import lombok.Data;

@Data
public class UserBlackItem {
    private String sysId;
    private Integer userDbId;
    private Integer sceneId;
    private Integer mediaId;
    private Integer days;
    private String daysValue;

}
