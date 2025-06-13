package com.ahamo.payment.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
public class PaymentAuditService {

    public void logTokenizationEvent(String eventType, String maskedCardNumber, boolean success) {
        log.info("PAYMENT_AUDIT: event={}, maskedCard={}, success={}, timestamp={}", 
                eventType, maskedCardNumber, success, LocalDateTime.now());
    }

    public void logValidationEvent(String eventType, String validationType, boolean success) {
        log.info("VALIDATION_AUDIT: event={}, type={}, success={}, timestamp={}", 
                eventType, validationType, success, LocalDateTime.now());
    }

    public void logSecurityEvent(String eventType, String details) {
        log.warn("SECURITY_AUDIT: event={}, details={}, timestamp={}", 
                eventType, details, LocalDateTime.now());
    }

    public void logPaymentProcessing(String paymentId, String paymentMethod, String status) {
        log.info("PAYMENT_PROCESSING_AUDIT: paymentId={}, method={}, status={}, timestamp={}", 
                paymentId, paymentMethod, status, LocalDateTime.now());
    }
}
