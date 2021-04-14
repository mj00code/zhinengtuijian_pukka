package com.ubo.iptv.manage.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: xuning
 * @Date: 2020-11-09
 */
@Data
public class StrategyVO {

    @ApiModelProperty(value = "场景id")
    private Long sceneId;

    @ApiModelProperty(value = "场景名称")
    private String sceneName;

    @ApiModelProperty(value = "策略id")
    private Long strategyId;

    @ApiModelProperty(value = "策略名称")
    private String strategyName;

    @ApiModelProperty(value = "运营商id")
    private String ispId;

    @ApiModelProperty(value = "运营商名称")
    private String ispName;

    @ApiModelProperty(value = "创建方式")
    private String createType;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "开关")
    private Boolean openFlag;

}
