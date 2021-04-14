package com.ubo.iptv.recommend.enums;

import com.ubo.iptv.core.annotation.CommonEntity;
import com.ubo.iptv.core.enums.CommonEnum;

/**
 * @Author: xuning
 * @Date: 2020-10-23
 */
public enum MediaChargeFreeEnum implements CommonEnum {

    @CommonEntity(value = "0", description = "免费")
    FREE,

    @CommonEntity(value = "1", description = "收费")
    CHARGE,

}
