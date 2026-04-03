package org.example.exception.custom_exception;

import org.example.exception.handler.BaseServiceException;
import org.springframework.http.HttpStatus;

public class EntityHasRelationsException extends BaseServiceException {
    public EntityHasRelationsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
