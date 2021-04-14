package com.ubo.iptv.manage.requset;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * @Author: xuning
 * @Date: 2020-11-10
 */
@Data
public class StrategyManualAddDTO {

    @ApiModelProperty(value = "策略id")
    @NotNull
    private Long strategyId;

    @ApiModelProperty(value = "媒资id")
    @NotNull
    private Integer mediaId;

    @ApiModelProperty(value = "媒资类型")
    @NotNull
    private Integer mediaType;

    @ApiModelProperty(value = "媒资名称")
    @NotEmpty
    private String mediaName;

    @ApiModelProperty(value = "媒资code")
    @NotEmpty
    private String mediaCode;

    @ApiModelProperty(value = "状态：1-置顶，0-必推")
    @NotNull
    private Integer status;

    @ApiModelProperty(value = "开始时间：yyyy-MM-dd")
    private String startTime;
    @ApiModelProperty(value = "截至时间：yyyy-MM-dd")
    private String endTime;
}
