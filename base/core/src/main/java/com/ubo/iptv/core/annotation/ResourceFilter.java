package com.ubo.iptv.core.annotation;

import com.ubo.iptv.core.interceptor.InterceptorConfig;
import org.springframework.context.annotation.Import;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)//该注解修饰类中的方法
@Inherited
@Import({InterceptorConfig.class})
public @interface ResourceFilter {
}
