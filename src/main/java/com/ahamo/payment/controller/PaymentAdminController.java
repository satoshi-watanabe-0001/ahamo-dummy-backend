package com.ahamo.payment.controller;

import com.ahamo.payment.dto.BillingScheduleDto;
import com.ahamo.payment.dto.PaymentHistoryDto;
import com.ahamo.payment.model.Payment;
import com.ahamo.payment.service.PaymentService;
import com.ahamo.payment.service.RecurringBillingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/payments")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class PaymentAdminController {
    
    private final PaymentService paymentService;
    private final RecurringBillingService recurringBillingService;
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(@PathVariable String status) {
        log.info("Admin getting payments by status: {}", status);
        
        Payment.PaymentStatus paymentStatus = Payment.PaymentStatus.valueOf(status.toUpperCase());
        
        return ResponseEntity.ok(List.of());
    }
    
    @PostMapping("/billing/schedule")
    public ResponseEntity<Map<String, String>> scheduleBilling(
            @Valid @RequestBody BillingScheduleDto request) {
        log.info("Admin scheduling billing for customer: {}", request.getCustomerId());
        
        recurringBillingService.scheduleBilling(request.getCustomerId(), request);
        
        return ResponseEntity.ok(Map.of("message", "Billing scheduled successfully"));
    }
    
    @PostMapping("/billing/{billingId}/process")
    public ResponseEntity<Map<String, Object>> processBilling(@PathVariable String billingId) {
        log.info("Admin processing billing: {}", billingId);
        
        RecurringBillingService.BillingResult result = recurringBillingService.processBilling(billingId);
        
        return ResponseEntity.ok(Map.of(
            "success", result.isSuccess(),
            "message", result.getMessage(),
            "transactionId", result.getTransactionId()
        ));
    }
    
    @PostMapping("/billing/{billingId}/retry")
    public ResponseEntity<Map<String, String>> retryFailedBilling(
            @PathVariable String billingId,
            @RequestParam String reason) {
        log.info("Admin retrying failed billing: {} with reason: {}", billingId, reason);
        
        RecurringBillingService.FailureReason failureReason = 
            RecurringBillingService.FailureReason.valueOf(reason.toUpperCase());
        
        recurringBillingService.handleFailedBilling(billingId, failureReason);
        
        return ResponseEntity.ok(Map.of("message", "Billing retry initiated"));
    }
    
    @PostMapping("/billing/process-all")
    public ResponseEntity<Map<String, String>> processAllScheduledBillings() {
        log.info("Admin processing all scheduled billings");
        
        recurringBillingService.processScheduledBillings();
        
        return ResponseEntity.ok(Map.of("message", "All scheduled billings processed"));
    }
}
