package org.example.dto.employee.registration.annotation.date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.example.exception.custom_exception.DateFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomDateDeserializerTest {

    private CustomDateDeserializer deserializer;
    private JsonParser jsonParser;
    private DeserializationContext context;

    @BeforeEach
    void setUp() {
        deserializer = new CustomDateDeserializer();
        jsonParser = mock(JsonParser.class);
        context = mock(DeserializationContext.class);
    }

    @Test
    void shouldDeserializeCorrectDate() throws Exception {
        when(jsonParser.getText()).thenReturn("2023-01-28");

        Date result = deserializer.deserialize(jsonParser, context);
        Calendar cal = Calendar.getInstance();
        cal.setTime(result);

        assertEquals(2023, cal.get(Calendar.YEAR));
        assertEquals(Calendar.JANUARY, cal.get(Calendar.MONTH));
        assertEquals(28, cal.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    void shouldThrowException_onInvalidDate() throws Exception {
        when(jsonParser.getText()).thenReturn("invalid-date");

        assertThrows(DateFormatException.class, () -> deserializer.deserialize(jsonParser, context));
    }
}
