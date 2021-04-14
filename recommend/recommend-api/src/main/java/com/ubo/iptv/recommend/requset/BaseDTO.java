package com.ubo.iptv.recommend.requset;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class BaseDTO {
    @ApiModelProperty(value = "appId", example = "appId")
    @NotBlank
    private String appId;
    @ApiModelProperty(value = "设备编号", example = "xxxxxxxx")
    @NotBlank
    private String barcode;
    @ApiModelProperty(value = "验签字符串", example = "xxxxxxxx")
    @NotBlank
    private String sign;
}
