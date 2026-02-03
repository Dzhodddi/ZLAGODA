package org.example.dto.employee.registration.annotation.role;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.model.employee.Role;

public class ValidRoleValidator implements ConstraintValidator<ValidRole, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }

        try {
            Role.RoleName.valueOf(value.toUpperCase().trim());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
