package com.ubo.iptv.core.enums;

import com.ubo.iptv.core.annotation.CommonEntity;

public enum CreateTypeEnum implements CommonEnum {
    @CommonEntity(value = "0", description = "系统默认")
    DEFAULT;

    /**
     * 根据value获取description
     *
     * @param value
     * @return
     */
    public static String description(Object value) {
        if (value != null) {
            for (CreateTypeEnum sysEnum : CreateTypeEnum.values()) {
                if (sysEnum.value().equals(value.toString())) {
                    return sysEnum.description();
                }
            }
            return value.toString();
        }
        return null;
    }
}
