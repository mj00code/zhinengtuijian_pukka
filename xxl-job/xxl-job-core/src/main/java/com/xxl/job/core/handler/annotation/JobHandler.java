package com.xxl.job.core.handler.annotation;

/**
 * annotation for job handler
 * @author 2016-5-17 21:06:49
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface JobHandler {

    String value() default "";
    
}
