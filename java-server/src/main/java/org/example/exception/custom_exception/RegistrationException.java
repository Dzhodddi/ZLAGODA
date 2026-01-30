package org.example.exception.custom_exception;

import org.example.exception.handler.BaseServiceException;
import org.springframework.http.HttpStatus;

public class RegistrationException extends BaseServiceException {
    public RegistrationException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
