package com.ubo.iptv.manage.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: xuning
 * @Date: 2020-11-10
 */
@Data
public class StrategyManualVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "场景名称")
    private String sceneName;

    @ApiModelProperty(value = "媒资id")
    private Integer mediaId;

    @ApiModelProperty(value = "媒资类型")
    private Integer mediaType;

    @ApiModelProperty(value = "媒资类型")
    private String mediaTypeName;

    @ApiModelProperty(value = "媒资名称")
    private String mediaName;

    @ApiModelProperty(value = "媒资code")
    private String mediaCode;

    @ApiModelProperty(value = "状态：1-置顶，0-必推 -1:拉黑")
    private Integer status;

    @ApiModelProperty(value = "状态：1-置顶，0-必推 -1:拉黑")
    private String statusValue;

    @ApiModelProperty(value = "开始时间")
    private String startTime;
    @ApiModelProperty(value = "截止时间")
    private String endTime;

    @ApiModelProperty(value = "海报图片url")
    private String posterUrl;

    @ApiModelProperty(value = "媒资所属CP")
    private String cpName;

}
