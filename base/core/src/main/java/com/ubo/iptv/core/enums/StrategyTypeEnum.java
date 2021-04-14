package com.ubo.iptv.core.enums;

import com.ubo.iptv.core.annotation.CommonEntity;

/**
 * @Author: xuning
 * @Date: 2020-10-23
 */
public enum StrategyTypeEnum implements CommonEnum {

    @CommonEntity(value = "0", description = "冷启动")
    COLD_BOOT,

    @CommonEntity(value = "1", description = "智能推荐")
    INTELLIGENT,

}
