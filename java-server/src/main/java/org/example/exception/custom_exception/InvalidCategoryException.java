package org.example.exception.custom_exception;

import org.example.exception.handler.BaseServiceException;
import org.springframework.http.HttpStatus;

public class InvalidCategoryException extends BaseServiceException {
    public InvalidCategoryException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
