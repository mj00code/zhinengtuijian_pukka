package com.ubo.iptv.manage.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: xuning
 * @Date: 2020-11-09
 */
@Data
public class TagMediaSummaryVO extends AbstractTagSummeryVO {

    @ApiModelProperty(value = "媒资id")
    private Long mediaId;

    @ApiModelProperty(value = "媒资名称")
    private String mediaName;

    @ApiModelProperty(value = "媒资类型")
    private String mediaType;

    @ApiModelProperty(value = "导演")
    private String directors;

    @ApiModelProperty(value = "演员")
    private String actors;

    @ApiModelProperty(value = "发行年代")
    private String publishTime;

    @ApiModelProperty(value = "是否收费")
    private Boolean isCharge;
}
