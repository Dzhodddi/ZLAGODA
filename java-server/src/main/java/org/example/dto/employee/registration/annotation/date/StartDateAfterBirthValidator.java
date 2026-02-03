package org.example.dto.employee.registration.annotation.date;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Date;
import org.springframework.beans.BeanWrapperImpl;

public class StartDateAfterBirthValidator
        implements ConstraintValidator<StartDateAfterBirth, Object> {

    private String firstField;
    private String secondField;

    @Override
    public void initialize(StartDateAfterBirth constraintAnnotation) {
        this.firstField = constraintAnnotation.first();
        this.secondField = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        if (object == null) {
            return true;
        }
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(object);
        Object firstValue = beanWrapper.getPropertyValue(firstField);
        Object secondValue = beanWrapper.getPropertyValue(secondField);
        if (firstValue == null) {
            return secondValue == null;
        }
        if (!(firstValue instanceof Date firstDate)
                || !(secondValue instanceof Date secondDate)) {
            return false;
        }
        return firstDate.before(secondDate);
    }
}
