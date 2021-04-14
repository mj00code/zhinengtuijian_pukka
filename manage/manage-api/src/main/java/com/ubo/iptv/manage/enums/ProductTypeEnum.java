package com.ubo.iptv.manage.enums;

import com.ubo.iptv.core.annotation.CommonEntity;
import com.ubo.iptv.core.enums.CommonEnum;

/**
 * @Author: xuning
 * @Date: 2020-10-23
 */
public enum ProductTypeEnum implements CommonEnum {
    @CommonEntity(value = "1", description = "包年")
    YEAR,
    @CommonEntity(value = "2", description = "包半年")
    HALF_YEAR,
    @CommonEntity(value = "3", description = "包季")
    QUARTER,
    @CommonEntity(value = "4", description = "包月")
    MONTH,
    @CommonEntity(value = "5", description = "PPV")
    PPV;


    public static String description(Object value) {
        if (value != null) {
            for (ProductTypeEnum statusEnum : ProductTypeEnum.values()) {
                if (statusEnum.value().equals(value.toString())) {
                    return statusEnum.description();
                }
            }
            return value.toString();
        }
        return "未知";
    }
}
