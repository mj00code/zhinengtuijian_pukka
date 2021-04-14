package com.ubo.iptv.recommend.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class AOPLogConfig {


    @Pointcut("execution(* com.ubo.iptv.recommend.service.impl.IPTVServiceImpl.recommendPrograms(..)))")
    public void pointCut() {
    }


    @Around(value = "pointCut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        String method = proceedingJoinPoint.getTarget().getClass().getName() + ":" + signature.getName();
        String aggs = Arrays.toString(proceedingJoinPoint.getArgs());
        Object ret = null;
        LocalDateTime requestTime = LocalDateTime.now();
        try {
            ret = proceedingJoinPoint.proceed();
            LocalDateTime responseTime = LocalDateTime.now();
//            log.info("\r\n方法:{}\r\n请求:{}\r\n返回:{}", method, JSONObject.toJSONString(aggs), JSONObject.toJSONString(ret));
//            log.info("\r\n请求时间:{},响应时间:{},耗时:{}毫秒", requestTime, responseTime, Duration.between(requestTime, responseTime).toMillis());
            log.info("\r\n方法:{},耗时:{}毫秒", method, Duration.between(requestTime, responseTime).toMillis());
            return ret;
        } catch (Throwable throwable) {
            LocalDateTime responseTime = LocalDateTime.now();
            log.info("\r\n方法:{},耗时:{}毫秒", method, Duration.between(requestTime, responseTime).toMillis());
            log.error(throwable.getMessage());
            throw throwable;
        }
    }

}
