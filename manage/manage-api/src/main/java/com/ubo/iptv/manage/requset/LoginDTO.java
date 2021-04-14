package com.ubo.iptv.manage.requset;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author: xuning
 * @Date: 2020-09-30
 */
@Data
public class LoginDTO {

    @ApiModelProperty(value = "账号", example = "123")
    @NotBlank(message = "账号不能为空")
    private String username;

    @ApiModelProperty(value = "密码", example = "2342")
    @NotBlank(message = "密码不能为空")
    private String password;
}
