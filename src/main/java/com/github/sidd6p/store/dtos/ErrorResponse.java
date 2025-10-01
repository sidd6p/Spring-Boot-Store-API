package com.github.sidd6p.store.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standardized error response DTO for consistent error handling across the application.
 * <p>
 * This class provides a uniform structure for all error responses, making it easier
 * for API consumers to parse and handle errors consistently.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    /**
     * Timestamp when the error occurred
     */
    private LocalDateTime timestamp;

    /**
     * HTTP status code
     */
    private int status;

    /**
     * HTTP status reason phrase (e.g., "Bad Request", "Not Found")
     */
    private String error;

    /**
     * Main error message describing what went wrong
     */
    private String message;

    /**
     * Optional detailed error information (e.g., validation errors by field)
     */
    private Map<String, String> details;

    /**
     * Request path where the error occurred
     */
    private String path;
}

