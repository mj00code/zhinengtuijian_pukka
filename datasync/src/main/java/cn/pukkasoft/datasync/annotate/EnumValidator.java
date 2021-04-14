package cn.pukkasoft.datasync.annotate;


import cn.pukkasoft.datasync.advice.ApiResponse;
import cn.pukkasoft.datasync.advice.ApiResponseEnum;
import cn.pukkasoft.datasync.advice.ApiResponseException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * author:magj
 * date:2020/11/5
 */
@Slf4j
public class EnumValidator implements ConstraintValidator<EnumCheck, Object>, Annotation {
    private Class<?> clazz;


    private String method;


    private List<Object> values = new ArrayList<>();

    @Override
    public void initialize(EnumCheck constraintAnnotation) {
        this.clazz = constraintAnnotation.clazz();
        Object[] objects = clazz.getEnumConstants();
        try {
            Method method = clazz.getMethod(constraintAnnotation.method());
            if (Objects.isNull(method)) {

                throw new ApiResponseException(ApiResponseEnum.ERROR.ERROR_PARAMETER, String.format("枚举对象%s缺少名为%s的方法", clazz.getName(), constraintAnnotation.method()));
            }
            Object value;
            for (Object obj : objects) {
                value = method.invoke(obj);
                values.add(value);
            }
        } catch (Exception e) {
            log.error("处理枚举校验异常:{}", e);
        }


    }

    @SneakyThrows
    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext constraintValidatorContext) {

        if (obj instanceof String) {
            String valueStr = (String)obj;
            return StringUtils.isEmpty(valueStr)|| values.contains(obj);
        }
        return Objects.isNull(obj) || values.contains(obj);

    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }
}
