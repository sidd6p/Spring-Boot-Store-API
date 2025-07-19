package com.github.sidd6p.store.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
/**
 * Global exception handler for the entire application.
 *
 * This class automatically catches exceptions thrown by any controller and handles them
 * using the methods annotated with @ExceptionHandler. It helps avoid repeating try-catch
 * blocks in each controller and ensures consistent error responses.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation errors triggered by @Valid annotated request bodies.
     *
     * When validation fails (e.g., missing required field or invalid format),
     * Spring throws a MethodArgumentNotValidException. This method catches that exception,
     * extracts all field-specific validation errors, and returns them in a structured map.
     *
     * Example response:
     * {
     *   "email": "must be a valid email",
     *   "name": "must not be blank"
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
     *
     * These exceptions typically occur when Bean Validation constraints on entities
     * are violated during persistence operations, such as when saving an entity to
     * the database with invalid field values.
     *
     * Example response:
     * {
     *   "email": "Value must be in lower case"
     * }
     *
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
     * Handles any unhandled exceptions that are not specifically caught elsewhere.
     *
     * This acts as a fallback to ensure that even unexpected exceptions return
     * a proper HTTP 500 response instead of crashing the application.
     *
     * Note:
     * If a more specific handler (like the one above for MethodArgumentNotValidException) exists,
     * that handler will take precedence and this method will not be called.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception exception) {
        var errors = new HashMap<String, String>();

        String errorMessage = exception.getMessage() != null ? exception.getMessage() : "An unexpected error occurred";
        errors.put("error", errorMessage);
        log.error("Unexpected error occurred: {}", errorMessage, exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
    }
}
