/**
 * Copyright 2020 bejson.com
 */
package com.ubo.iptv.recommend.entity;

import lombok.Data;

import java.util.List;

/**
 * Auto-generated: 2020-12-16 10:23:59
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class CollaborativeFiterResponse {

    private int result;
    private String msg;
    private String top;
    private List<String> data;
    private int time;
    private String programId;
    private int status;
}