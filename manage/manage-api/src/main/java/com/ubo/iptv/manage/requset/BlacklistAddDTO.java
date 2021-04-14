package com.ubo.iptv.manage.requset;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * @Author: xuning
 * @Date: 2020-11-10
 */
@Data
public class BlacklistAddDTO {

    @ApiModelProperty(value = "运营商id")
    @NotBlank
    private String ispId;

    @ApiModelProperty(value = "媒资id")
    @NotNull
    private Integer mediaId;

    @ApiModelProperty(value = "媒资类型")
    @NotNull
    private Integer mediaType;

    @ApiModelProperty(value = "媒资code")
    @NotBlank
    private String mediaCode;

    @ApiModelProperty(value = "媒资名称")
    @NotBlank
    private String mediaName;

    @ApiModelProperty(value = "开始时间：yyyy-MM-dd")
    private String startTime;
    @ApiModelProperty(value = "截至时间：yyyy-MM-dd")
    private String endTime;
}
