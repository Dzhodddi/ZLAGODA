package org.example.exception.custom_exception;

import org.example.exception.handler.BaseServiceException;
import org.springframework.http.HttpStatus;

public class InvalidRoleException extends BaseServiceException {
    public InvalidRoleException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
