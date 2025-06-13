package com.ahamo.payment.controller;

import com.ahamo.payment.dto.PaymentMethodResponseDto;
import com.ahamo.payment.service.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentMethodController {
    
    private final PaymentMethodService paymentMethodService;
    
    @GetMapping("/methods")
    public ResponseEntity<List<PaymentMethodResponseDto>> getPaymentMethods() {
        try {
            List<PaymentMethodResponseDto> methods = paymentMethodService.getAvailablePaymentMethods();
            return ResponseEntity.ok(methods);
        } catch (Exception e) {
            log.error("Failed to get payment methods", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/methods/{methodId}/availability")
    public ResponseEntity<Boolean> checkMethodAvailability(@PathVariable String methodId) {
        try {
            boolean isAvailable = paymentMethodService.isPaymentMethodAvailable(methodId);
            return ResponseEntity.ok(isAvailable);
        } catch (Exception e) {
            log.error("Failed to check payment method availability for: " + methodId, e);
            return ResponseEntity.ok(false);
        }
    }
}
