package com.ubo.iptv.recommend.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: xuning
 * @Date: 2020-09-30
 */
@Data
public class SearchMediaSort {

    @ApiModelProperty(value = "媒资类型Id")
    private String mediaType;

    @ApiModelProperty(value = "媒资类型")
    private String mediaTypeName;

    @ApiModelProperty(value = "媒资id")
    private Integer mediaId;

    @ApiModelProperty(value = "媒资code")
    private String mediaCode;

    @ApiModelProperty(value = "媒资名称")
    private String mediaName;

    @ApiModelProperty(value = "媒资显示类型 0:置顶 1:人工必推 2:智能必推 3:题材偏好推荐 4:明星推荐 5:协同过滤推荐 6:冷启动置顶 7:冷启动人工必推 8:冷启动题材偏好")
    private Integer showType;

    @ApiModelProperty(value = "运营商id")
    private String sysId;

    @ApiModelProperty(value = "推荐快照编号")
    private String strategyLogId;

    @ApiModelProperty(value = "评分")
    private Float score;

}
