package com.ubo.iptv.manage.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserBasicInfo {
    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "省市区")
    private String address;

    @ApiModelProperty(value = "运营商")
    private String sysId;
}
