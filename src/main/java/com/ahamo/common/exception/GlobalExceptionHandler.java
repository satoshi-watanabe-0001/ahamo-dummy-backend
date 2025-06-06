package com.ahamo.common.exception;

import com.ahamo.security.exception.AuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(AuthenticationException ex) {
        log.error("Authentication error: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error_code", ErrorCode.UNAUTHORIZED.name());
        errorResponse.put("message", ErrorCode.UNAUTHORIZED.getDefaultMessage());
        errorResponse.put("severity", ErrorCode.UNAUTHORIZED.getSeverity().name());
        errorResponse.put("resolution", ErrorCode.UNAUTHORIZED.getResolution());
        errorResponse.put("request_id", UUID.randomUUID().toString());
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage());
        
        List<Map<String, String>> details = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            Map<String, String> detail = new HashMap<>();
            detail.put("field", error.getField());
            detail.put("code", "INVALID_VALUE");
            detail.put("message", error.getDefaultMessage());
            details.add(detail);
        }
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error_code", ErrorCode.VALIDATION_ERROR.name());
        errorResponse.put("message", ErrorCode.VALIDATION_ERROR.getDefaultMessage());
        errorResponse.put("severity", ErrorCode.VALIDATION_ERROR.getSeverity().name());
        errorResponse.put("resolution", ErrorCode.VALIDATION_ERROR.getResolution());
        errorResponse.put("details", details);
        errorResponse.put("request_id", UUID.randomUUID().toString());
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error_code", ErrorCode.INTERNAL_ERROR.name());
        errorResponse.put("message", ErrorCode.INTERNAL_ERROR.getDefaultMessage());
        errorResponse.put("severity", ErrorCode.INTERNAL_ERROR.getSeverity().name());
        errorResponse.put("resolution", ErrorCode.INTERNAL_ERROR.getResolution());
        errorResponse.put("request_id", UUID.randomUUID().toString());
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
