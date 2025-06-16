package com.ahamo.payment.controller;

import com.ahamo.payment.dto.PaymentMethodDto;
import com.ahamo.payment.dto.PaymentRequestDto;
import com.ahamo.payment.dto.PaymentResponseDto;
import com.ahamo.payment.model.Payment;
import com.ahamo.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @GetMapping("/methods")
    public ResponseEntity<Map<String, List<PaymentMethodDto>>> getPaymentMethods() {
        log.info("Getting available payment methods");
        
        List<PaymentMethodDto> methods = paymentService.getAvailablePaymentMethods();
        
        return ResponseEntity.ok(Map.of("payment_methods", methods));
    }
    
    @PostMapping("/process")
    public ResponseEntity<PaymentResponseDto> processPayment(@Valid @RequestBody PaymentRequestDto request) {
        log.info("Processing payment for contract: {}", request.getContractId());
        
        PaymentResponseDto response = paymentService.processPayment(request);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{paymentId}/status")
    public ResponseEntity<Map<String, String>> getPaymentStatus(@PathVariable String paymentId) {
        log.info("Getting payment status for: {}", paymentId);
        
        Payment.PaymentStatus status = paymentService.getPaymentStatus(paymentId);
        
        return ResponseEntity.ok(Map.of("status", status.toString().toLowerCase()));
    }
    
    @GetMapping("/contract/{contractId}")
    public ResponseEntity<List<Payment>> getPaymentsByContract(@PathVariable String contractId) {
        log.info("Getting payments for contract: {}", contractId);
        
        List<Payment> payments = paymentService.getPaymentsByContract(contractId);
        
        return ResponseEntity.ok(payments);
    }
}
