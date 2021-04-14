package com.ubo.iptv.recommend.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: xuning
 * @Date: 2020-09-30
 */
@Data
public class CollaborativeFiterUser2ItemRequest {

    @ApiModelProperty(value = "媒资id或者用户id")
    private String userId;

    @ApiModelProperty(value = "需要数量")
    private String size;

    @ApiModelProperty(value = "排除媒资列表")
    private String[] programIds;

}
