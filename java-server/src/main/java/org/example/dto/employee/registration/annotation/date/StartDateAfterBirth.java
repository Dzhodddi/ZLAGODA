package org.example.dto.employee.registration.annotation.date;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.example.dto.employee.registration.annotation.field_match.FieldMatchValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = StartDateAfterBirthValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface StartDateAfterBirth {
    String first();
    String second();
    String message() default "Fields do not match";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
