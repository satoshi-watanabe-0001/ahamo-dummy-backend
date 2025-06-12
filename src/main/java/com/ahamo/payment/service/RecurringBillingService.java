package com.ahamo.payment.service;

import com.ahamo.payment.dto.BillingScheduleDto;
import com.ahamo.payment.model.BillingSchedule;

public interface RecurringBillingService {
    
    void scheduleBilling(String customerId, BillingScheduleDto schedule);
    
    BillingResult processBilling(String billingId);
    
    void handleFailedBilling(String billingId, FailureReason reason);
    
    void processScheduledBillings();
    
    void retryFailedBillings();
    
    public static class BillingResult {
        private boolean success;
        private String message;
        private String transactionId;
        
        public BillingResult(boolean success, String message, String transactionId) {
            this.success = success;
            this.message = message;
            this.transactionId = transactionId;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getTransactionId() { return transactionId; }
    }
    
    public enum FailureReason {
        INSUFFICIENT_FUNDS, CARD_EXPIRED, PAYMENT_METHOD_INVALID, GATEWAY_ERROR, UNKNOWN
    }
}
