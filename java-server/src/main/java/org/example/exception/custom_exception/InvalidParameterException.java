package org.example.exception.custom_exception;

import org.example.exception.handler.BaseServiceException;
import org.springframework.http.HttpStatus;

public class InvalidParameterException extends BaseServiceException {
    public InvalidParameterException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
