package com.ubo.iptv.manage.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: xuning
 * @Date: 2020-11-09
 */
@Data
public class TagDaySummary {

    @ApiModelProperty(value = "标签id")
    private Long tagId;
    @ApiModelProperty(value = "标签名称")
    private String tagName;
    @ApiModelProperty(value = "统计数量")
    private Integer summaryCount;
    @ApiModelProperty(value = "标签占比")
    private BigDecimal rate;
    @ApiModelProperty(value = "统计时间")
    private String summaryDate;
    @ApiModelProperty(value = "统计时间下所有tag总数量")
    private Integer allCount;

}
