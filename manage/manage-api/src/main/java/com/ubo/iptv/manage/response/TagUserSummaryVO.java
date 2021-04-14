package com.ubo.iptv.manage.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: xuning
 * @Date: 2020-11-09
 */
@Data
public class


TagUserSummaryVO extends AbstractTagSummeryVO {

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "省市区")
    private String address;

    @ApiModelProperty(value = "运营商")
    private String sysId;

    @ApiModelProperty(value = "激活时间")
    private String activeTime;

}
