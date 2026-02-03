package org.example.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.example.exception.custom_exception.AuthorizationException;
import org.example.exception.custom_exception.DateFormatException;
import org.example.exception.custom_exception.DeletionException;
import org.example.exception.custom_exception.EntityNotFoundException;
import org.example.exception.custom_exception.InvalidCategoryException;
import org.example.exception.custom_exception.InvalidParameterException;
import org.example.exception.custom_exception.InvalidProductException;
import org.example.exception.custom_exception.InvalidRoleException;
import org.example.exception.custom_exception.RegistrationException;
import org.example.exception.handler.CustomGlobalExceptionHandler;
import org.example.exception.handler.CustomGlobalExceptionHandler.ErrorResponse;
import org.example.exception.handler.CustomGlobalExceptionHandler.ValidationErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

@ExtendWith(MockitoExtension.class)
@DisplayName("Custom Global Exception Handler Tests")
class CustomGlobalExceptionHandlerTest {

    @InjectMocks
    private CustomGlobalExceptionHandler exceptionHandler;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @Test
    @DisplayName("handleCustomExceptions should return NOT_FOUND for EntityNotFoundException")
    void handleCustomExceptions_EntityNotFound_shouldReturnNotFound() {
        EntityNotFoundException exception = new EntityNotFoundException("Entity not found");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleCustomExceptions(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Not Found", response.getBody().getError());
        assertEquals("Entity not found", response.getBody().getMessage());
    }

    @Test
    @DisplayName("handleCustomExceptions should return CONFLICT for RegistrationException")
    void handleCustomExceptions_Registration_shouldReturnConflict() {
        RegistrationException exception = new RegistrationException("User exists");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleCustomExceptions(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("Conflict", response.getBody().getError());
        assertEquals("User exists", response.getBody().getMessage());
    }

    @Test
    @DisplayName("handleCustomExceptions should return CONFLICT for DeletionException")
    void handleCustomExceptions_Deletion_shouldReturnConflict() {
        DeletionException exception = new DeletionException("Cannot delete");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleCustomExceptions(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("Conflict", response.getBody().getError());
        assertEquals("Cannot delete", response.getBody().getMessage());
    }

    @Test
    @DisplayName("handleCustomExceptions should return UNPROCESSABLE_ENTITY for InvalidCategoryException")
    void handleCustomExceptions_InvalidCategory_shouldReturnUnprocessableEntity() {
        InvalidCategoryException exception = new InvalidCategoryException("Invalid category");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleCustomExceptions(exception);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(422, response.getBody().getStatus());
        assertEquals("Unprocessable Entity", response.getBody().getError());
        assertEquals("Invalid category", response.getBody().getMessage());
    }

    @Test
    @DisplayName("handleCustomExceptions should return UNPROCESSABLE_ENTITY for InvalidRoleException")
    void handleCustomExceptions_InvalidRole_shouldReturnUnprocessableEntity() {
        InvalidRoleException exception = new InvalidRoleException("Invalid role");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleCustomExceptions(exception);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(422, response.getBody().getStatus());
        assertEquals("Unprocessable Entity", response.getBody().getError());
        assertEquals("Invalid role", response.getBody().getMessage());
    }

    @Test
    @DisplayName("handleCustomExceptions should return UNPROCESSABLE_ENTITY for InvalidProductException")
    void handleCustomExceptions_InvalidProduct_shouldReturnUnprocessableEntity() {
        InvalidProductException exception = new InvalidProductException("Invalid product");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleCustomExceptions(exception);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(422, response.getBody().getStatus());
        assertEquals("Unprocessable Entity", response.getBody().getError());
        assertEquals("Invalid product", response.getBody().getMessage());
    }

    @Test
    @DisplayName("handleCustomExceptions should return BAD_REQUEST for InvalidParameterException")
    void handleCustomExceptions_InvalidParameter_shouldReturnBadRequest() {
        InvalidParameterException exception = new InvalidParameterException("Invalid param");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleCustomExceptions(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Invalid param", response.getBody().getMessage());
    }

    @Test
    @DisplayName("handleCustomExceptions should return BAD_REQUEST for DateFormatException")
    void handleCustomExceptions_DateFormat_shouldReturnBadRequest() {
        DateFormatException exception = new DateFormatException("Bad date");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleCustomExceptions(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Bad date", response.getBody().getMessage());
    }

    @Test
    @DisplayName("handleCustomExceptions should return FORBIDDEN for AuthorizationException")
    void handleCustomExceptions_Authorization_shouldReturnForbidden() {
        AuthorizationException exception = new AuthorizationException("Not allowed");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleCustomExceptions(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(403, response.getBody().getStatus());
        assertEquals("Forbidden", response.getBody().getError());
        assertEquals("Not allowed", response.getBody().getMessage());
    }

    @Test
    @DisplayName("handleValidationExceptions should return field errors")
    void handleValidationExceptions_shouldReturnErrors() {
        FieldError fieldError1 = new FieldError("employee", "name", "cannot be empty");
        FieldError fieldError2 = new FieldError("employee", "age", "must be positive");

        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        ResponseEntity<ValidationErrorResponse> response = exceptionHandler
                .handleValidationExceptions(methodArgumentNotValidException);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(422, response.getBody().getStatus());
        assertEquals("Unprocessable Entity", response.getBody().getError());
        assertEquals("Validation failed", response.getBody().getMessage());
        assertEquals(2, response.getBody().getErrors().size());
        assertTrue(response.getBody().getErrors().contains("name cannot be empty"));
        assertTrue(response.getBody().getErrors().contains("age must be positive"));
    }

    @Test
    @DisplayName("handleDataIntegrityViolation should return UNPROCESSABLE_ENTITY")
    void handleDataIntegrityViolation_shouldReturnUnprocessableEntity() {
        DataIntegrityViolationException exception =
                new DataIntegrityViolationException("Constraint violation");

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleDataIntegrityViolation(exception);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(422, response.getBody().getStatus());
        assertEquals("Unprocessable Entity", response.getBody().getError());
        assertEquals("Data integrity violation", response.getBody().getMessage());
    }

    @Test
    @DisplayName("handleInvalidJson should return BAD_REQUEST for generic JSON error")
    void handleInvalidJson_generic_shouldReturnBadRequest() {
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
        when(exception.getCause()).thenReturn(null);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidJson(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Invalid JSON format", response.getBody().getMessage());
    }

    @Test
    @DisplayName("handleInvalidJson should return specific message for nested DateFormatException")
    void handleInvalidJson_withDateFormatException_shouldReturnSpecificMessage() {
        DateFormatException dateException = new DateFormatException("Invalid Date");
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
        when(exception.getCause()).thenReturn(dateException);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidJson(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Invalid date format. Expected format: yyyy-MM-dd",
                response.getBody().getMessage());
    }

    @Test
    @DisplayName("handleAuthenticationException should return UNAUTHORIZED")
    void handleAuthenticationException_shouldReturnUnauthorized() {
        AuthenticationException exception = new BadCredentialsException("Bad credentials");

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleAuthenticationException(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().getStatus());
        assertEquals("Unauthorized", response.getBody().getError());
        assertEquals("Bad credentials", response.getBody().getMessage());
    }

    @Test
    @DisplayName("handleAccessDenied should return FORBIDDEN")
    void handleAccessDenied_shouldReturnForbidden() {
        AccessDeniedException exception = new AccessDeniedException("Access is denied");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAccessDenied(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(403, response.getBody().getStatus());
        assertEquals("Forbidden", response.getBody().getError());
        assertEquals("Access Denied", response.getBody().getMessage());
    }

    @Test
    @DisplayName("handleAllExceptions should return INTERNAL_SERVER_ERROR")
    void handleAllExceptions_shouldReturnInternalServerError() {
        Exception exception = new NullPointerException("Something went wrong");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAllExceptions(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("Unexpected error occurred", response.getBody().getMessage());
    }
}
