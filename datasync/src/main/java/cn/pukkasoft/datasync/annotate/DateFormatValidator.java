package cn.pukkasoft.datasync.annotate;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class DateFormatValidator implements ConstraintValidator<DateFormat, String> {
    private String[] patterns;

    @Override
    public void initialize(DateFormat constraintAnnotation) {
        this.patterns = constraintAnnotation.patterns();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean result = true;
        if (StringUtils.isEmpty(value)) {
            return result;
        }

        try {
            Date date = DateUtils.parseDate(value, Locale.ENGLISH,
                    patterns);
            return true;
        } catch (ParseException e) {
            result = false;
        }

        return result;
    }
}