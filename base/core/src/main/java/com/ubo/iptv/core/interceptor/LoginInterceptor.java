package com.ubo.iptv.core.interceptor;

import com.ubo.iptv.core.annotation.LoginRequired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author 周钰
 * 2018-03-08
 * 目前，登录校验是通过在url中添加/u/标记实现，该方案不够优雅，如果调整了url的访问权限，会导致前端需要跟着修改，牵涉较多
 * 基于此背景设定了新的解决方案：根据url获取到对应的实现类和方法，分析类和方法上的注解，来决定是否允许后续操作
 *
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Method method = ((HandlerMethod)applicationContext.getBean(RequestMappingHandlerMapping.class).getHandler(request).getHandler()).getMethod();

        Class[] classes = ((HandlerMethod)applicationContext.getBean(RequestMappingHandlerMapping.class).getHandler(request).getHandler()).getBeanType().getInterfaces();
        Annotation annotation = null;
        for(Class clazz : classes){
            Method method1 = clazz.getMethod(method.getName(), method.getParameterTypes());
            if(method1 != null){
                annotation = method1.getAnnotation(LoginRequired.class);
                if(annotation != null){
                    break;
                }
            }
        }

        log.info("current user id is "+request.getHeader("CURRENT_USER_ID"));
        if(annotation == null || ((LoginRequired)annotation).value() == true){  //必须登录
            //尝试获取用户信息
            try{
                Long.parseLong(request.getHeader("CURRENT_USER_ID"));
                return true;
            }catch (Throwable e){
                response.setStatus(405);
                return false;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
