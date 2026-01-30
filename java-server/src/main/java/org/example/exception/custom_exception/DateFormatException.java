package org.example.exception.custom_exception;

import org.example.exception.handler.BaseServiceException;
import org.springframework.http.HttpStatus;

public class DateFormatException extends BaseServiceException {
    public DateFormatException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
