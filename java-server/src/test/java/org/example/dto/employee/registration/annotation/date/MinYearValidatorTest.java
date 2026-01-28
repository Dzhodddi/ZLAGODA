package org.example.dto.employee.registration.annotation.date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MinYearValidatorTest {

    private MinYearValidator validator;

    @BeforeEach
    void setUp() {
        validator = new MinYearValidator();

        // Анонімна реалізація анотації MinYear для тесту
        MinYear minYearAnnotation = new MinYear() {
            @Override
            public int value() {
                return 2000; // мінімальний рік
            }

            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return MinYear.class;
            }

            @Override
            public String message() {
                return "Year must be greater than or equal to {value}";
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<? extends jakarta.validation.Payload>[] payload() {
                return new Class[0];
            }
        };

        validator.initialize(minYearAnnotation);
    }

    @Test
    void shouldReturnTrue_whenYearIsAfterMin() {
        Calendar cal = Calendar.getInstance();
        cal.set(2022, Calendar.JANUARY, 1);
        Date date = cal.getTime();

        assertTrue(validator.isValid(date, null), "Year 2022 should be valid");
    }

    @Test
    void shouldReturnTrue_whenYearIsEqualToMin() {
        Calendar cal = Calendar.getInstance();
        cal.set(2000, Calendar.JANUARY, 1);
        Date date = cal.getTime();

        assertTrue(validator.isValid(date, null), "Year 2000 should be valid");
    }

    @Test
    void shouldReturnFalse_whenYearIsBeforeMin() {
        Calendar cal = Calendar.getInstance();
        cal.set(1999, Calendar.JANUARY, 1);
        Date date = cal.getTime();

        assertFalse(validator.isValid(date, null), "Year 1999 should be invalid");
    }

    @Test
    void shouldReturnTrue_whenDateIsNull() {
        assertTrue(validator.isValid(null, null), "Null date should be valid");
    }
}
