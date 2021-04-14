package com.ubo.iptv.recommend.enums;

import com.ubo.iptv.core.annotation.CommonEntity;
import com.ubo.iptv.core.enums.CommonEnum;

/**
 * @Author: xuning
 * @Date: 2020-10-23
 */
public enum MediaShowEnum implements CommonEnum {

    @CommonEntity(value = "0", description = "智能推荐-置顶")
    MANUAL_TOP,

    @CommonEntity(value = "1", description = "智能推荐-人工必推")
    MANUAL_MUST,

    @CommonEntity(value = "2", description = "智能推荐-智能必推")
    IR_MUST,

    @CommonEntity(value = "3", description = "智能推荐-题材偏好推荐")
    IR_TYPE_KIND,

    @CommonEntity(value = "4", description = "智能推荐-明星推荐")
    IR_STAR,

    @CommonEntity(value = "5", description = "智能推荐-协同过滤推荐")
    IR_COLLABORATIVE,

    @CommonEntity(value = "6", description = "冷启动-置顶")
    COLD_BOOT_MANUAL_TOP,

    @CommonEntity(value = "7", description = "冷启动-人工必推")
    COLD_BOOT_MANUAL_MUST,
    
    @CommonEntity(value = "8", description = "冷启动-推荐")
    COLD_BOOT_IR_TYPE_KIND;

    public static String description(Object value) {
        if (value != null) {
            for (MediaShowEnum statusEnum : MediaShowEnum.values()) {
                if (statusEnum.value().equals(value.toString())) {
                    return statusEnum.description();
                }
            }
            return value.toString();
        }
        return null;
    }
}
