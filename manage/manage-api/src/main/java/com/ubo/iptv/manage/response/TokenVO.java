package com.ubo.iptv.manage.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: xuning
 * @Date: 2020-09-30
 */
@Data
public class TokenVO {

    @ApiModelProperty(value = "登陆凭证", example = "sdffdss2e")
    private String token;

    @ApiModelProperty(value = "用户信息", example = "")
    private TokenUser user;
}
