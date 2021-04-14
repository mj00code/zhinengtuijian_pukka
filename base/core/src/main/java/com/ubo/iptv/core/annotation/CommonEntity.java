package com.ubo.iptv.core.annotation;

/**
 * Created by huangjian on 2017/10/23.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CommonEntity {
    //值
    String value();
    //描述
    String description();
    //单位
    String unit() default "";
}
