package org.example.dto.employee.registration.annotation.date;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = MinYearValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MinYear {
    String message() default "Year must be less than {value}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int value();
}

