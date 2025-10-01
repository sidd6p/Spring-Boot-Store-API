package com.github.sidd6p.store.controllers;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

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
     * extracts all field-specific validation errors, and returns them in a structured map.
     * <p>
     * Example response:
     * {
     * "email": "must be a valid email",
     * "name": "must not be blank"
     * }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        var errors = new HashMap<String, String>();

        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
            log.error("Validation error in field '{}': {}", fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Handles constraint violation exceptions that occur during entity validation.
     * <p>
     * These exceptions typically occur when Bean Validation constraints on entities
     * are violated during persistence operations, such as when saving an entity to
     * the database with invalid field values.
     * <p>
     * Example response:
     * {
     * "email": "Value must be in lower case"
     * }
     * <p>
     * Note: Even though handleGenericException catches Exception (parent of ConstraintViolationException),
     * Spring's exception resolution mechanism prioritizes built-in handlers for validation exceptions
     * before reaching our generic handler. ConstraintViolationException is handled by Spring's
     * default exception resolvers first, which is why we need this specific handler.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException exception) {
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

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
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
    public ResponseEntity<Map<String, String>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception) {
        var errors = new HashMap<String, String>();

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

        errors.put(parameterName, errorMessage);
        log.warn("Method argument type mismatch: {}", errorMessage);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
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
    public ResponseEntity<Map<String, String>> handleNoResourceFound(NoResourceFoundException exception) {
        var errors = new HashMap<String, String>();

        String resourcePath = exception.getResourcePath();
        String errorMessage = String.format("Endpoint not found: %s", resourcePath);
        errors.put("error", errorMessage);
        log.warn("Resource not found: {}", resourcePath);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errors);
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
    public ResponseEntity<Map<String, String>> handleMethodNotSupported(HttpRequestMethodNotSupportedException exception) {
        var errors = new HashMap<String, String>();

        String method = exception.getMethod();
        String[] supportedMethods = exception.getSupportedMethods();

        String errorMessage = String.format("HTTP method '%s' is not supported for this endpoint", method);
        errors.put("error", errorMessage);

        // Log supported methods for debugging but don't expose them in the response for security
        log.warn("Unsupported HTTP method '{}' attempted. Supported methods: {}", method,
                supportedMethods != null ? String.join(", ", supportedMethods) : "none");

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errors);
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
    public ResponseEntity<Map<String, String>> handleAuthenticationException(AuthenticationException exception) {
        var errors = new HashMap<String, String>();

        String errorMessage = "Invalid email or password";
        errors.put("error", errorMessage);
        log.warn("Authentication failed: {}", exception.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errors);
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
    public ResponseEntity<Map<String, String>> handleGenericException(Exception exception) {
        var errors = new HashMap<String, String>();

        String errorMessage = "An unexpected error occurred";
        errors.put("error", errorMessage);
        log.error("Unexpected error occurred: {}", exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
    }
}
