package com.ubo.iptv.manage.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: xuning
 * @Date: 2020-09-30
 */
@Data
public class AccountUserVO implements TokenUser {

    @ApiModelProperty(value = "用户ID", example = "1234")
    private Long id;

    @ApiModelProperty(value = "账号", example = "100001")
    private String account;

    @ApiModelProperty(value = "用户名", example = "100001")
    private String name;

}
