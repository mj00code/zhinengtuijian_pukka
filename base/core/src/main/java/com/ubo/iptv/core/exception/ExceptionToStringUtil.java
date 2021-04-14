package com.ubo.iptv.core.exception;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * 该类将某些特殊的异常类型转换为字符串输出
 * ConstraintViolationException
 * @author huangjian
 */
public class ExceptionToStringUtil {
    public static String toString(ConstraintViolationException exception){
        StringBuilder errorsArray = new StringBuilder();
        for (ConstraintViolation cv : exception.getConstraintViolations()) {
            errorsArray.append(cv.getMessage()).append("; ");
        }
        return errorsArray.toString();
    }
}
