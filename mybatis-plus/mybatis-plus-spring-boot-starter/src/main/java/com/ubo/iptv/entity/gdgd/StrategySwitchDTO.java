package com.ubo.iptv.entity.gdgd;

import lombok.Data;

/**
 * @Author: xuning
 * @Date: 2020-09-30
 */
@Data
public class StrategySwitchDTO {
    //是否开启 1是，2否
    private Integer openCloseFlag;
    //栏目ID
    private String categoryId;
}
