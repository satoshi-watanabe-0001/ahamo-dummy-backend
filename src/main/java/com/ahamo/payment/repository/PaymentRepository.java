package com.ahamo.payment.repository;

import com.ahamo.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByPaymentUuid(String paymentUuid);
    
    List<Payment> findByContractId(String contractId);
    
    List<Payment> findByStatus(Payment.PaymentStatus status);
    
    List<Payment> findByContractIdAndStatus(String contractId, Payment.PaymentStatus status);
    
    Optional<Payment> findByTransactionId(String transactionId);
}
