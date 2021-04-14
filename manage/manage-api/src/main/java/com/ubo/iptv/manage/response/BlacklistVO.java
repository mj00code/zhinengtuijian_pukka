package com.ubo.iptv.manage.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: xuning
 * @Date: 2020-11-09
 */
@Data
public class BlacklistVO {

    @ApiModelProperty(value = "黑名单id")
    private Long id;

    @ApiModelProperty(value = "运营商id")
    private String ispId;

    @ApiModelProperty(value = "运营商名称")
    private String ispName;

    @ApiModelProperty(value = "媒资类型")
    private Integer mediaType;

    @ApiModelProperty(value = "媒资类型")
    private String mediaTypeName;

    @ApiModelProperty(value = "媒资id")
    private Integer mediaId;

    @ApiModelProperty(value = "媒资code")
    private String mediaCode;

    @ApiModelProperty(value = "媒资名称")
    private String mediaName;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "黑名单生效时间")
    private String startTime;

    @ApiModelProperty(value = "黑名单失效时间")
    private String endTime;
}
