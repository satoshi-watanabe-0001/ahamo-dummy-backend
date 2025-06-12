package com.ahamo.payment.repository;

import com.ahamo.payment.model.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    
    List<PaymentHistory> findByCustomerId(String customerId);
    
    List<PaymentHistory> findByContractId(String contractId);
    
    List<PaymentHistory> findByPaymentId(String paymentId);
    
    List<PaymentHistory> findByCustomerIdOrderByProcessedAtDesc(String customerId);
}
