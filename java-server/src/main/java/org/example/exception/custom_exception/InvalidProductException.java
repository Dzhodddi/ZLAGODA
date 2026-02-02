package org.example.exception.custom_exception;

import org.example.exception.handler.BaseServiceException;
import org.springframework.http.HttpStatus;

public class InvalidProductException extends BaseServiceException {
    public InvalidProductException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
