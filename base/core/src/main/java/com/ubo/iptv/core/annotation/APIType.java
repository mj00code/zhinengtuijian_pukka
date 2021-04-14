package com.ubo.iptv.core.annotation;


import com.ubo.iptv.core.enums.APITypeEnum;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface APIType {
    APITypeEnum value() default APITypeEnum.PUBLIC;
}
