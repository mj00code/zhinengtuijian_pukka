package cn.pukkasoft.datasync.annotate;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = { DateFormatValidator.class })
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateFormat {

    String message() default "时间格式不正确";

    String[] patterns();

    // 这两行不加会报错 javax.validation.ConstraintDefinitionException: HV000074
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}