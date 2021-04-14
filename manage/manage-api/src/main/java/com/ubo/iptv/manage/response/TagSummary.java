package com.ubo.iptv.manage.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: xuning
 * @Date: 2020-11-09
 */
@Data
public class TagSummary {
    @ApiModelProperty(value = "tag结算信息")
    private List<TagDaySummary> tagAllDaysSummarys;

}
