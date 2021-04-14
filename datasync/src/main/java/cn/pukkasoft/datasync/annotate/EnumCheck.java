package cn.pukkasoft.datasync.annotate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = { EnumValidator.class })
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumCheck{
    String message() default "枚举不在范围内";

    Class<?> clazz();


    String method() default "getCode";


    // 这两行不加会报错 javax.validation.ConstraintDefinitionException: HV000074
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
