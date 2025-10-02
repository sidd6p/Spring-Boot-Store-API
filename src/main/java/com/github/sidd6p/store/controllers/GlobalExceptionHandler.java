package com.github.sidd6p.store.controllers;

import com.github.sidd6p.store.dtos.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.NoSuchElementException;

/**
 * Global exception handler for the entire application.
 * <p>
 * This class automatically catches exceptions thrown by any controller and handles them
 * using the methods annotated with @ExceptionHandler. It helps avoid repeating try-catch
 * blocks in each controller and ensures consistent error responses.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation errors triggered by @Valid annotated request bodies.
     * <p>
     * When validation fails (e.g., missing required field or invalid format),
     * Spring throws a MethodArgumentNotValidException. This method catches that exception,
     * extracts all field-specific validation errors, and returns them in a structured format.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                      HttpServletRequest request) {
        var errors = new HashMap<String, String>();

        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
            log.error("Validation error in field '{}': {}", fieldName, errorMessage);
        });

        var errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation failed")
                .details(errors)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles constraint violation exceptions that occur during entity validation.
     * <p>
     * These exceptions typically occur when Bean Validation constraints on entities
     * are violated during persistence operations, such as when saving an entity to
     * the database with invalid field values.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException exception,
                                                                   HttpServletRequest request) {
        var errors = new HashMap<String, String>();

        exception.getConstraintViolations().forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();
            // Extract just the field name from the property path (removes the method name if present)
            String fieldName = propertyPath.contains(".") ?
                    propertyPath.substring(propertyPath.lastIndexOf('.') + 1) : propertyPath;
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
            log.error("Constraint violation in field '{}': {}", fieldName, errorMessage);
        });

        var errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Constraint violation")
                .details(errors)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles method argument type mismatch exceptions, commonly occurring when
     * path variables cannot be converted to their expected types.
     * <p>
     * This is particularly useful for UUID path parameters where invalid formats
     * (like strings that are too long or contain invalid characters) cause conversion failures.
     * <p>
     * Example scenarios:
     * - GET /carts/invalid-uuid-format -> 400 Bad Request instead of 500 Internal Server Error
     * - GET /carts/123-456-789-too-long -> 400 Bad Request with clear message
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception,
                                                                          HttpServletRequest request) {
        String parameterName = exception.getName();
        Class<?> requiredType = exception.getRequiredType();
        Object rejectedValue = exception.getValue();

        String errorMessage;
        if (requiredType != null && requiredType.equals(java.util.UUID.class)) {
            errorMessage = String.format("Invalid UUID format for parameter '%s': '%s'", parameterName, rejectedValue);
        } else {
            errorMessage = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s",
                    rejectedValue, parameterName, requiredType != null ? requiredType.getSimpleName() : "unknown");
        }

        log.warn("Method argument type mismatch: {}", errorMessage);

        var errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(errorMessage)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles HttpMessageNotReadableException when the request body cannot be read or deserialized.
     * <p>
     * This commonly occurs when:
     * - Invalid JSON syntax is provided in the request body
     * - JSON contains values that cannot be converted to the expected types (e.g., invalid UUID format)
     * - Required fields are missing or malformed
     * <p>
     * Example scenarios:
     * - POST /checkout with cartId: "invalid-uuid" -> 400 Bad Request instead of 500 Internal Server Error
     * - POST /checkout with malformed JSON -> 400 Bad Request with clear message
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException exception,
                                                                      HttpServletRequest request) {
        String errorMessage = "Invalid request body";

        // Extract more specific error message if available
        Throwable cause = exception.getCause();
        if (cause != null) {
            String causeMessage = cause.getMessage();
            if (causeMessage != null) {
                // Check if it's a UUID deserialization error
                if (causeMessage.contains("UUID") && causeMessage.contains("standard 36-char representation")) {
                    errorMessage = "Invalid UUID format in request body";
                } else if (causeMessage.contains("Cannot deserialize")) {
                    // Extract field name from error message if possible
                    errorMessage = "Invalid data format in request body";
                }
            }
        }

        log.warn("HTTP message not readable: {}", exception.getMessage());

        var errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(errorMessage)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles NoResourceFoundException when an endpoint/resource is not found.
     * <p>
     * This occurs when:
     * - A request is made to an endpoint that doesn't exist (e.g., PUT /carts/{id}/items/{id})
     * - Spring treats it as a static resource request and fails to find it
     * <p>
     * Returns 404 Not Found instead of 500 Internal Server Error.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException exception,
                                                               HttpServletRequest request) {
        String resourcePath = exception.getResourcePath();
        String errorMessage = String.format("Endpoint not found: %s", resourcePath);
        log.warn("Resource not found: {}", resourcePath);

        var errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(errorMessage)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handles HttpRequestMethodNotSupportedException when an unsupported HTTP method is used.
     * <p>
     * This occurs when:
     * - A request is made using an HTTP method that is not supported by the endpoint
     * - For example, sending DELETE to an endpoint that only supports GET/POST
     * <p>
     * Returns 405 Method Not Allowed without exposing supported methods for security reasons.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException exception,
                                                                  HttpServletRequest request) {
        String method = exception.getMethod();
        String[] supportedMethods = exception.getSupportedMethods();

        String errorMessage = String.format("HTTP method '%s' is not supported for this endpoint", method);

        // Log supported methods for debugging but don't expose them in the response for security
        log.warn("Unsupported HTTP method '{}' attempted. Supported methods: {}", method,
                supportedMethods != null ? String.join(", ", supportedMethods) : "none");

        var errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .error(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase())
                .message(errorMessage)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
    }

    /**
     * Handles authentication failures such as bad credentials.
     * <p>
     * This occurs when:
     * - User provides incorrect email/password combination during login
     * - Authentication token is invalid or expired
     * <p>
     * Returns 401 Unauthorized with a user-friendly error message.
     */
    @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException exception,
                                                                       HttpServletRequest request) {
        String errorMessage = "Invalid email or password";
        log.warn("Authentication failed: {}", exception.getMessage());

        var errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(errorMessage)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * Handles IllegalArgumentException thrown when invalid arguments are provided.
     * <p>
     * This occurs when:
     * - Invalid data is provided to service methods
     * - Business logic validation fails
     * <p>
     * Returns 400 Bad Request with a descriptive error message.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException exception,
                                                                        HttpServletRequest request) {
        log.warn("Illegal argument: {}", exception.getMessage());

        var errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(exception.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles IllegalStateException thrown when an operation is attempted in an invalid state.
     * <p>
     * This occurs when:
     * - Trying to add a product that already exists in the cart
     * - Operations that violate business rules or state constraints
     * <p>
     * Returns 409 Conflict with a descriptive error message.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException exception,
                                                                     HttpServletRequest request) {
        log.warn("Illegal state: {}", exception.getMessage());

        var errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(exception.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Handles NoSuchElementException thrown when a requested element doesn't exist.
     * <p>
     * This occurs when:
     * - Using orElseThrow() on an empty Optional
     * - Attempting to access a resource that doesn't exist
     * <p>
     * Returns 404 Not Found with a descriptive error message.
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException exception,
                                                                      HttpServletRequest request) {
        log.warn("Element not found: {}", exception.getMessage());

        var errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(exception.getMessage() != null ? exception.getMessage() : "Resource not found")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handles AccessDeniedException thrown when a user tries to access a resource they don't have permission for.
     * <p>
     * This occurs when:
     * - A user without proper role tries to access admin endpoints
     * - Authorization checks fail
     * <p>
     * Returns 403 Forbidden with a descriptive error message.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException exception,
                                                                     HttpServletRequest request) {
        log.warn("Access denied: {}", exception.getMessage());

        var errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message("Access denied. You don't have permission to access this resource")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    /**
     * Handles RuntimeException thrown when a runtime error occurs.
     * <p>
     * This is a catch-all for RuntimeExceptions that provides more specific error messages
     * for payment-related failures and other runtime issues.
     * <p>
     * Returns 500 Internal Server Error with a descriptive error message.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException exception,
                                                                HttpServletRequest request) {
        String errorMessage = exception.getMessage();

        // Check if it's a payment-related error
        if (errorMessage != null && errorMessage.contains("payment session")) {
            log.error("Payment processing error: {}", errorMessage, exception);
            errorMessage = "Payment processing failed. Please try again later.";
        } else {
            log.error("Runtime error occurred: {}", errorMessage, exception);
            errorMessage = errorMessage != null ? errorMessage : "An unexpected error occurred";
        }

        var errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(errorMessage)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Handles any unhandled exceptions that are not specifically caught elsewhere.
     * <p>
     * This acts as a fallback to ensure that even unexpected exceptions return
     * a proper HTTP 500 response instead of crashing the application.
     * <p>
     * Note:
     * If a more specific handler (like the one above for MethodArgumentNotValidException) exists,
     * that handler will take precedence and this method will not be called.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception exception,
                                                                HttpServletRequest request) {
        String errorMessage = "An unexpected error occurred";
        log.error("Unexpected error occurred: {}", exception.getMessage(), exception);

        var errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(errorMessage)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
