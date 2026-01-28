package org.example.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
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
    @DisplayName("handleValidationExceptions should return field and global errors")
    void handleValidationExceptions_shouldReturnAllErrors() {
        FieldError fieldError1 = new FieldError("employee", "empl_name", "must not be blank");
        FieldError fieldError2 = new FieldError("employee", "empl_surname", "must not be blank");
        ObjectError globalError = new ObjectError("employee", "Invalid employee data");

        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));
        when(bindingResult.getGlobalErrors()).thenReturn(List.of(globalError));

        ResponseEntity<List<String>> response = exceptionHandler
                .handleValidationExceptions(methodArgumentNotValidException);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());
        assertTrue(response.getBody().contains("empl_name must not be blank"));
        assertTrue(response.getBody().contains("empl_surname must not be blank"));
        assertTrue(response.getBody().contains("Invalid employee data"));
    }

    @Test
    @DisplayName("handleValidationExceptions should handle only field errors")
    void handleValidationExceptions_onlyFieldErrors_shouldReturnFieldErrors() {
        FieldError fieldError = new FieldError("product", "product_name", "must not be null");

        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(bindingResult.getGlobalErrors()).thenReturn(List.of());

        ResponseEntity<List<String>> response = exceptionHandler
                .handleValidationExceptions(methodArgumentNotValidException);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertTrue(response.getBody().contains("product_name must not be null"));
    }

    @Test
    @DisplayName("handleValidationExceptions should handle empty errors")
    void handleValidationExceptions_noErrors_shouldReturnEmptyList() {
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());
        when(bindingResult.getGlobalErrors()).thenReturn(List.of());

        ResponseEntity<List<String>> response = exceptionHandler
                .handleValidationExceptions(methodArgumentNotValidException);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    @DisplayName("handleIllegalArgumentException should return BAD_REQUEST")
    void handleIllegalArgumentException_shouldReturnBadRequest() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        ResponseEntity<String> response = exceptionHandler.handleIllegalArgumentException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid argument", response.getBody());
    }

    @Test
    @DisplayName("handleDataIntegrityViolationException should return UNPROCESSABLE_ENTITY")
    void handleDataIntegrityViolationException_shouldReturnUnprocessableEntity() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Constraint violation");

        ResponseEntity<String> response = exceptionHandler
                .handleDataIntegrityViolationException(exception);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("Data integrity violation", response.getBody());
    }

    @Test
    @DisplayName("handleInvalidJson should return BAD_REQUEST for invalid JSON")
    void handleInvalidJson_invalidJson_shouldReturnBadRequest() {
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
        when(exception.getCause()).thenReturn(null);

        ResponseEntity<String> response = exceptionHandler.handleInvalidJson(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid JSON format", response.getBody());
    }

    @Test
    @DisplayName("handleInvalidJson should return specific message for DateFormatException")
    void handleInvalidJson_dateFormatException_shouldReturnDateFormatMessage() {
        DateFormatException dateFormatException = new DateFormatException("Invalid date");
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
        when(exception.getCause()).thenReturn(dateFormatException);

        ResponseEntity<String> response = exceptionHandler.handleInvalidJson(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid date format. Expected format: yyyy-MM-dd", response.getBody());
    }

    @Test
    @DisplayName("handleAuthenticationException should return UNAUTHORIZED")
    void handleAuthenticationException_shouldReturnUnauthorized() {
        AuthenticationException exception = new AuthenticationException("Authentication failed");

        ResponseEntity<String> response = exceptionHandler.handleAuthenticationException(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Authentication failed", response.getBody());
    }

    @Test
    @DisplayName("handleBadCredentialsException should return UNAUTHORIZED")
    void handleBadCredentialsException_shouldReturnUnauthorized() {
        BadCredentialsException exception = new BadCredentialsException("Bad credentials");

        ResponseEntity<String> response = exceptionHandler.handleBadCredentialsException(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Bad credentials", response.getBody());
    }

    @Test
    @DisplayName("handleEntityNotFoundException should return NOT_FOUND")
    void handleEntityNotFoundException_shouldReturnNotFound() {
        EntityNotFoundException exception = new EntityNotFoundException("Entity not found");

        ResponseEntity<String> response = exceptionHandler.handleEntityNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Entity not found", response.getBody());
    }

    @Test
    @DisplayName("handleRegistrationException should return CONFLICT")
    void handleRegistrationException_shouldReturnConflict() {
        RegistrationException exception = new RegistrationException("Employee already exists");

        ResponseEntity<String> response = exceptionHandler.handleRegistrationException(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Employee already exists", response.getBody());
    }

    @Test
    @DisplayName("handleDeletionException should return CONFLICT")
    void handleDeletionException_shouldReturnConflict() {
        DeletionException exception = new DeletionException("Cannot delete yourself");

        ResponseEntity<String> response = exceptionHandler.handleDeletionException(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Cannot delete yourself", response.getBody());
    }

    @Test
    @DisplayName("handleAllExceptions should return INTERNAL_SERVER_ERROR")
    void handleAllExceptions_shouldReturnInternalServerError() {
        Exception exception = new Exception("Unexpected error");

        ResponseEntity<String> response = exceptionHandler.handleAllExceptions(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unexpected error occurred", response.getBody());
    }

    @Test
    @DisplayName("handleDateFormatException should return BAD_REQUEST")
    void handleDateFormatException_shouldReturnBadRequest() {
        DateFormatException exception = new DateFormatException("Invalid date format");

        ResponseEntity<String> response = exceptionHandler.handleDateFormatException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid date format", response.getBody());
    }

    @Test
    @DisplayName("handleInvalidRole should return UNPROCESSABLE_ENTITY")
    void handleInvalidRole_shouldReturnUnprocessableEntity() {
        InvalidRoleException exception = new InvalidRoleException("Invalid role");

        ResponseEntity<String> response = exceptionHandler.handleInvalidRole(exception);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("Invalid role", response.getBody());
    }

    @Test
    @DisplayName("handleInvalidCategory should return UNPROCESSABLE_ENTITY")
    void handleInvalidCategory_shouldReturnUnprocessableEntity() {
        InvalidCategoryException exception = new InvalidCategoryException("Invalid category");

        ResponseEntity<String> response = exceptionHandler.handleInvalidCategory(exception);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("Invalid category", response.getBody());
    }

    @Test
    @DisplayName("handleInvalidProduct should return UNPROCESSABLE_ENTITY")
    void handleInvalidProduct_shouldReturnUnprocessableEntity() {
        InvalidProductException exception = new InvalidProductException("Invalid product");

        ResponseEntity<String> response = exceptionHandler.handleInvalidProduct(exception);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("Invalid product", response.getBody());
    }

    @Test
    @DisplayName("handleAuthorizationException should return FORBIDDEN")
    void handleAuthorizationException_shouldReturnForbidden() {
        AuthorizationException exception = new AuthorizationException("Access denied");

        ResponseEntity<String> response = exceptionHandler.handleAuthorizationException(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Access denied", response.getBody());
    }

    @Test
    @DisplayName("handleInvalidParameter should return BAD_REQUEST")
    void handleInvalidParameter_shouldReturnBadRequest() {
        InvalidParameterException exception = new InvalidParameterException("Invalid parameter");

        ResponseEntity<String> response = exceptionHandler.handleInvalidParameter(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid parameter", response.getBody());
    }

    @Test
    @DisplayName("handleAccessDeniedException should return FORBIDDEN")
    void handleAccessDeniedException_shouldReturnForbidden() {
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        ResponseEntity<String> response = exceptionHandler.handleAccessDeniedException(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Access denied", response.getBody());
    }

    @Test
    @DisplayName("handleInvalidJson should handle nested DateFormatException")
    void handleInvalidJson_nestedDateFormatException_shouldReturnDateFormatMessage() {
        DateFormatException dateFormatException = new DateFormatException("Invalid date");
        RuntimeException wrapperException = new RuntimeException("Wrapper", dateFormatException);
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
        when(exception.getCause()).thenReturn(wrapperException);

        ResponseEntity<String> response = exceptionHandler.handleInvalidJson(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid date format. Expected format: yyyy-MM-dd", response.getBody());
    }

    @Test
    @DisplayName("handleValidationExceptions should format field errors correctly")
    void handleValidationExceptions_shouldFormatFieldErrorsCorrectly() {
        FieldError fieldError = new FieldError("employee", "salary", "must be positive");

        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(bindingResult.getGlobalErrors()).thenReturn(List.of());

        ResponseEntity<List<String>> response = exceptionHandler
                .handleValidationExceptions(methodArgumentNotValidException);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("salary must be positive", response.getBody().get(0));
    }

    @Test
    @DisplayName("handleAllExceptions should handle RuntimeException")
    void handleAllExceptions_runtimeException_shouldReturnInternalServerError() {
        RuntimeException exception = new RuntimeException("Runtime error");

        ResponseEntity<String> response = exceptionHandler.handleAllExceptions(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unexpected error occurred", response.getBody());
    }

    @Test
    @DisplayName("handleAllExceptions should handle NullPointerException")
    void handleAllExceptions_nullPointerException_shouldReturnInternalServerError() {
        NullPointerException exception = new NullPointerException("Null pointer");

        ResponseEntity<String> response = exceptionHandler.handleAllExceptions(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unexpected error occurred", response.getBody());
    }
}
