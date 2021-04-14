package com.ubo.iptv.core.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.core.annotation.APIType;
import com.ubo.iptv.core.enums.APITypeEnum;
import com.ubo.iptv.core.enums.CommonCodeEnum;
import com.ubo.iptv.core.enums.GatewayTypeEnum;
import com.ubo.iptv.core.enums.HeaderEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 周钰
 * 2018-03-08
 * 目前，url访问控制是通过/i/标识是内网api，通过/u/标记必须登录的方案实现，该方案不够优雅，此外，如果调整了url的访问权限，会导致前端需要跟着修改，牵涉较多
 * 基于此背景设定了新的解决方案：根据url获取到对应的实现类和方法，分析类和方法上的注解，来决定是否允许后续操作
 */
@Slf4j
public class APIInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果外网网关，不可以调用内网接口
        String gatewayType = request.getHeader(HeaderEnum.gateway_type.name());
        if (GatewayTypeEnum.PUBLIC.name().equals(gatewayType)) {
            Class[] classes = ((HandlerMethod) handler).getBeanType().getInterfaces();
            for (Class clazz : classes) {
                if (clazz.isAnnotationPresent(APIType.class)) {
                    APIType apiType = (APIType) clazz.getAnnotation(APIType.class);
                    if (APITypeEnum.INNER.equals(apiType.value())) {
                        log.info("public gateway cannot call inner api: {}", request.getRequestURI());
                        response.setStatus(403);
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().print(JSONObject.toJSONString(CommonResponse.build(CommonCodeEnum.GLOBAL_NO_PERMISSION)));
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
