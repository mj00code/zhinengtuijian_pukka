package com.ubo.iptv.manage.enums;

import com.ubo.iptv.core.annotation.CommonEntity;
import com.ubo.iptv.core.enums.CommonEnum;

/**
 * @Author: xuning
 * @Date: 2020-10-23
 */
public enum StatusEnum implements CommonEnum {

    @CommonEntity(value = "1", description = "生效")
    EFFECT,

    @CommonEntity(value = "0", description = "失效")
    INVALID,


}
