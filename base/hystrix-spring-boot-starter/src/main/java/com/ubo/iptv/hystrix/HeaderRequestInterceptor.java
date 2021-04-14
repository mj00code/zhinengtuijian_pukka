package com.ubo.iptv.hystrix;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Enumeration;

/**
 * @Author: xuning
 * @Date: 2018/12/13
 */
public class HeaderRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();
            if (request != null) {
                // 传递header信息
                Enumeration<String> headerNames = request.getHeaderNames();
                if (headerNames != null) {
                    while (headerNames.hasMoreElements()) {
                        String headName = headerNames.nextElement();
                        // gateway_type不传递
                        if (!"gateway_type".equalsIgnoreCase(headName)) {
                            Enumeration<String> headers = request.getHeaders(headName);
                            requestTemplate.header(headName, Collections.list(headers));
                        }
                    }
                }
            }
        }
    }
}
