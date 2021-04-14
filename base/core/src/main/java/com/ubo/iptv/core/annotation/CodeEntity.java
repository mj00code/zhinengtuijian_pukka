package com.ubo.iptv.core.annotation;

import java.lang.annotation.*;

/**
 * Created by zhouyu on 2017/4/21.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CodeEntity {
    //主状态代码
    int result();
    //子状态代码
    int status() default 0;
    //异常信息服务器端展示
    String msg();
    //异常信息客户端展示
    String msg_client() default "";
}
