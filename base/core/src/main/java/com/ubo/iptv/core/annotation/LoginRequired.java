package com.ubo.iptv.core.annotation;

import java.lang.annotation.*;

/**
 * @author huangjian
 * @Date 2017/10/23
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})//该注解修饰类中的方法
@Inherited
public @interface LoginRequired {
    boolean value() default true;
}
