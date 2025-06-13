package com.ahamo.payment.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalPaymentExceptionHandler {

    @ExceptionHandler(PaymentTokenException.class)
    public ResponseEntity<Map<String, Object>> handlePaymentTokenException(PaymentTokenException e) {
        log.error("Payment token error: {}", e.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("errorMessage", e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(PaymentValidationException.class)
    public ResponseEntity<Map<String, Object>> handlePaymentValidationException(PaymentValidationException e) {
        log.error("Payment validation error: {}", e.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("valid", false);
        response.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        response.put("success", false);
        response.put("errors", errors);
        response.put("errorMessage", "入力データに問題があります");
        
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {
        log.error("Unexpected error in payment processing", e);
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("errorMessage", "システムエラーが発生しました");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
