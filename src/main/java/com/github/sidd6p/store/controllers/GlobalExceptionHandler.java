package com.github.sidd6p.store.controllers;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// The @ControllerAdvice annotation allows this class to provide centralized exception handling across all controllers.
// It acts as a global interceptor for controller exceptions.
@ControllerAdvice
public class GlobalExceptionHandler {


    // The @ExceptionHandler annotation defines a method that handles specific types of exceptions.
    // When a MethodArgumentNotValidException is thrown by any controller, this method will be invoked.
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

    // This handler catches any generic Exception that isn't caught by a more specific handler.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception exception) {
        var errors = new HashMap<String, String>();

        String errorMessage = exception.getMessage() != null ? exception.getMessage() : "An unexpected error occurred";
        errors.put("error", errorMessage);
        log.error("Unexpected error occurred: {}", errorMessage, exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
    }
}
