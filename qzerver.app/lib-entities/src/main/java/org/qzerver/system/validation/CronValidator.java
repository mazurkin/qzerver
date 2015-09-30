package org.qzerver.system.validation;

import org.quartz.CronExpression;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.text.ParseException;

public class CronValidator implements ConstraintValidator<Cron, Object> {

    @Override
    public void initialize(Cron constraintAnnotation) {
        // nothing to inititalize
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            new CronExpression((String) value);
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

}
