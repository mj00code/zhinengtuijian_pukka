package com.ubo.iptv.manage.requset;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author: xuning
 * @Date: 2020-11-10
 */
@Data
public class CacheSwitchDTO {

    @ApiModelProperty(value = "是否开启全局智能推荐")
    @NotNull
    private Boolean isAllIR;
    @ApiModelProperty(value = "运营商编号")
    private String sysId;
    @ApiModelProperty(value = "场景编号")
    private Integer sceneId;
    @NotNull
    @ApiModelProperty(value = "开关")
    private Integer openCloseFlag;
    @ApiModelProperty(value = "自定义项目id")
    private String categoryId;
}
