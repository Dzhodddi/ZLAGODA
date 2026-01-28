package org.example.dto.employee.registration.annotation.field_match;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FieldMatchValidatorTest {

    private FieldMatchValidator validator;

    @BeforeEach
    void setUp() {
        validator = new FieldMatchValidator();
        validator.initialize(new FieldMatch() {
            @Override
            public String first() { return "password"; }

            @Override
            public String second() { return "confirmPassword"; }

            @Override
            public String message() { return ""; }

            @Override
            public Class<?>[] groups() { return new Class[0]; }

            @Override
            public Class[] payload() { return new Class[0]; }

            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() { return FieldMatch.class; }
        });
    }

    static class TestUser {
        private String password;
        private String confirmPassword;

        TestUser(String password, String confirmPassword) {
            this.password = password;
            this.confirmPassword = confirmPassword;
        }

        public String getPassword() {
            return password;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }
    }

    @Test
    void shouldReturnTrue_whenFieldsMatch() {
        assertTrue(validator.isValid(new TestUser("123", "123"), null));
    }

    @Test
    void shouldReturnFalse_whenFieldsDoNotMatch() {
        assertFalse(validator.isValid(new TestUser("123", "456"), null));
    }

    @Test
    void shouldReturnTrue_whenBothFieldsNull() {
        assertTrue(validator.isValid(new TestUser(null, null), null));
    }

    @Test
    void shouldReturnFalse_whenFirstNullSecondNotNull() {
        assertFalse(validator.isValid(new TestUser(null, "123"), null));
    }

    @Test
    void shouldReturnTrue_whenObjectNull() {
        assertTrue(validator.isValid(null, null));
    }
}
