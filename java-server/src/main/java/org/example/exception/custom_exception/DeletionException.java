package org.example.exception.custom_exception;

import org.example.exception.handler.BaseServiceException;
import org.springframework.http.HttpStatus;

public class DeletionException extends BaseServiceException {
    public DeletionException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
