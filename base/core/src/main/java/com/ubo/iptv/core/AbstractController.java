package com.ubo.iptv.core;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.ubo.iptv.core.annotation.ResourceFilter;
import com.ubo.iptv.core.enums.CodeEnum;
import com.ubo.iptv.core.enums.CommonCodeEnum;
import com.ubo.iptv.core.enums.HeaderEnum;
import com.ubo.iptv.core.exception.ExceptionToStringUtil;
import com.ubo.iptv.core.exception.UncheckedApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.naming.NoPermissionException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.List;

/**
 * 统一的异常处理，代码里面只关心业务处理。对于业务层面的异常，尽量针对每种业务层面的错误，添加详细描述。
 *
 * @author zhouyu
 * @date 2017/10/10
 */
@ResourceFilter
@Slf4j
public abstract class AbstractController {

    @ExceptionHandler
    @ResponseBody
    protected CommonResponse exceptionHandler(HttpServletRequest request, Exception exception) {

        if (exception instanceof ConstraintViolationException) {
            //数据校验异常处理
            String errorInfo = ExceptionToStringUtil.toString((ConstraintViolationException) exception);
            return CommonResponse.builder().status(CommonCodeEnum.GLOBAL_FAIL.status()).result(CommonCodeEnum.GLOBAL_FAIL.result()).msg(errorInfo).build();
        }

        if (exception instanceof UndeclaredThrowableException && ((UndeclaredThrowableException) exception).getUndeclaredThrowable() instanceof NoPermissionException) {
            return CommonResponse.builder().status(CommonCodeEnum.GLOBAL_FAIL.status()).result(CommonCodeEnum.GLOBAL_FAIL.result()).msg("无权限访问").build();
        }
        if (exception instanceof MethodArgumentTypeMismatchException) {
            MethodArgumentTypeMismatchException e = (MethodArgumentTypeMismatchException) exception;
            String name = e.getParameter().getParameterName();
            String requiredType = e.getRequiredType().getName();
            return CommonResponse.builder().status(CommonCodeEnum.GLOBAL_FAIL.status()).result(CommonCodeEnum.GLOBAL_FAIL.result()).msg(String.format("Parameter type mismatch, [%s] must be [%s]!", name, requiredType)).build();
        }

        if (exception instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException e = (MethodArgumentNotValidException) exception;
            List<String> messageList = new ArrayList<>();
            for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
                messageList.add(fieldError.getField() + " " + fieldError.getDefaultMessage());
            }
            return CommonResponse.builder().status(CommonCodeEnum.GLOBAL_FAIL.status()).result(CommonCodeEnum.GLOBAL_FAIL.result()).msg(String.format("参数有误: %s!", messageList.toString())).build();
        }

        if (exception instanceof MissingServletRequestParameterException) {
            MissingServletRequestParameterException e = (MissingServletRequestParameterException) exception;
            String name = e.getParameterName();
            return CommonResponse.builder().status(CommonCodeEnum.GLOBAL_FAIL.status()).result(CommonCodeEnum.GLOBAL_FAIL.result()).msg(String.format("Parameter [%s] is expected, but missed!", name)).build();
        }

        if (exception instanceof HttpMessageNotReadableException) {
            if (exception.getCause() instanceof JsonProcessingException) {
                JsonProcessingException e = (JsonProcessingException) exception.getCause();
                try {
                    String key = ((JsonParser) e.getProcessor()).getCurrentName();
                    return CommonResponse.builder().status(CommonCodeEnum.GLOBAL_FAIL.status()).result(CommonCodeEnum.GLOBAL_FAIL.result()).msg(String.format("Parameter[%s]'s value is invalid!", key)).build();
                } catch (IOException ioe) {
                    log.error("JsonMappingException", ioe);
                }
            }
        }

        if (exception instanceof UncheckedApplicationException) {
            //自定义异常处理
            CodeEnum codeEnum = ((UncheckedApplicationException) exception).getCodeEnum();
            return CommonResponse.builder().result(codeEnum.result()).status(codeEnum.status()).msg(exception.getMessage()).build();
        }

        log.error("error occurred, cause with exception:", exception);
        //这是兜底方案，可以继续细化
        return CommonResponse.build(CommonCodeEnum.GLOBAL_FAIL);
    }

    protected String getHeader(String header) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes.getRequest().getHeader(header);
    }

    /**
     * 从header中获取用户id
     *
     * @param throwException 是否抛出异常
     * @return
     */
    protected Long getUserId(boolean throwException) {
        try {
            String userId = getHeader(HeaderEnum.current_user_id.name());
            return Long.valueOf(userId);
        } catch (Exception e) {
            if (throwException) {
                throw new UncheckedApplicationException(CommonCodeEnum.GLOBAL_NO_LOGIN);
            }
        }
        log.warn("unknown user");
        return null;
    }

    /**
     * 从header中获取用户id(失败抛出未登陆异常)
     */
    protected Long getUserId() {
        return getUserId(true);
    }
}
