package com.ubo.iptv.core.enums;

import com.ubo.iptv.core.annotation.CommonEntity;

import java.util.Arrays;

/**
 * @author huangjian
 * @Date 2017/10/23
 */
public interface CommonEnum {

    default CommonEntity commonEntity() {
        try {
            return this.getClass().getField(this.toString()).getAnnotation(CommonEntity.class);
        } catch (NoSuchFieldException var2) {
            return null;
        }
    }

    default String value() {
        return this.commonEntity().value();
    }

    default String description() {
        return this.commonEntity().description();
    }

    default String unit() {
        return this.commonEntity().unit();
    }

    default Integer intValue() {
        try {
            return Integer.valueOf(this.commonEntity().value());
        } catch (Exception e) {
            return null;
        }
    }

    static String toJsonStr(Class<? extends CommonEnum> codeEnum) {
        StringBuffer buffer = new StringBuffer();
        Arrays.asList(codeEnum.getEnumConstants()).forEach((every) -> {
            CommonEntity entity = every.commonEntity();
            buffer.append(entity.value()).append(";\r\n");
        });
        return buffer.toString();
    }
}
