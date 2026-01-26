package org.example.dto.employee.registration.annotation.date;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Calendar;
import java.util.Date;

public class MinYearValidator implements ConstraintValidator<MinYear, Date> {

    private int minYear;

    @Override
    public void initialize(MinYear constraintAnnotation) {
        this.minYear = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Date date, ConstraintValidatorContext context) {
        if (date == null) return true;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.YEAR) >= minYear;
    }
}
