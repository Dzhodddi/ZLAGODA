package org.example.exception.custom_exception;

import org.example.exception.handler.BaseServiceException;
import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends BaseServiceException {
    public EntityNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
