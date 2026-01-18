package org.example.dto.employee.registration.annotation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.example.exception.DateFormatException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomDateDeserializer extends JsonDeserializer<Date> {
    @Override
    public Date deserialize(JsonParser p, DeserializationContext context) throws IOException {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(p.getText());
        } catch (ParseException e) {
            throw new DateFormatException("Date parsing failed: " + p.getText());
        }
    }
}
