package com.example.modernrest.exception;

import com.example.modernrest.dto.ProblemDetailsResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetailsResponse> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        return problem(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage(), request, null);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ProblemDetailsResponse> handleDuplicateResourceException(DuplicateResourceException ex, WebRequest request) {
        return problem(HttpStatus.CONFLICT, "Duplicate Resource", ex.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetailsResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            if (error instanceof FieldError fieldError) {
                errors.put(fieldError.getField(), error.getDefaultMessage());
            }
        });
        return problem(HttpStatus.BAD_REQUEST, "Validation Failed", "Request validation failed", request, errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetailsResponse> handleNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
        String detail = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
        return problem(HttpStatus.BAD_REQUEST, "Malformed Request", detail, request, null);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ProblemDetailsResponse> handleAuthorizationDenied(AuthorizationDeniedException ex, WebRequest request) {
        return problem(HttpStatus.FORBIDDEN, "Forbidden", "Access Denied", request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetailsResponse> handleGeneric(Exception ex, WebRequest request) {
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(), request, null);
    }

    private ResponseEntity<ProblemDetailsResponse> problem(HttpStatus status, String title, String detail, WebRequest request, Map<String, String> errors) {
        String instance = request.getDescription(false).replace("uri=", "");
        ProblemDetailsResponse body = new ProblemDetailsResponse(
                "about:blank",
                title,
                status.value(),
                detail,
                instance,
                OffsetDateTime.now(),
                errors
        );
        return new ResponseEntity<>(body, status);
    }
}
