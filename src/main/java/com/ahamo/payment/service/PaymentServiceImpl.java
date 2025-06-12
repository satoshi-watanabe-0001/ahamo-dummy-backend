package com.ahamo.payment.service;

import com.ahamo.payment.dto.PaymentMethodDto;
import com.ahamo.payment.dto.PaymentRequestDto;
import com.ahamo.payment.dto.PaymentResponseDto;
import com.ahamo.payment.gateway.PaymentGatewayAdapter;
import com.ahamo.payment.gateway.PaymentGatewayFactory;
import com.ahamo.payment.gateway.dto.GatewayRequest;
import com.ahamo.payment.gateway.dto.GatewayResponse;
import com.ahamo.payment.model.Payment;
import com.ahamo.payment.model.PaymentHistory;
import com.ahamo.payment.repository.PaymentRepository;
import com.ahamo.payment.repository.PaymentHistoryRepository;
import com.ahamo.pricing.service.PricingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final PaymentGatewayFactory gatewayFactory;
    private final PricingService pricingService;
    
    @Override
    @Transactional
    public PaymentResponseDto processPayment(PaymentRequestDto request) {
        log.info("Processing payment for contract: {}", request.getContractId());
        
        Payment payment = createPaymentRecord(request);
        
        try {
            PaymentGatewayAdapter gateway = gatewayFactory.getAdapter("default");
            
            GatewayRequest gatewayRequest = GatewayRequest.builder()
                    .paymentId(payment.getPaymentUuid())
                    .amount(payment.getAmount())
                    .currency("JPY")
                    .paymentMethodId(request.getPaymentMethodId())
                    .build();
            
            GatewayResponse response = gateway.processPayment(gatewayRequest);
            
            updatePaymentFromGatewayResponse(payment, response);
            paymentRepository.save(payment);
            
            createPaymentHistory(payment);
            
            return PaymentResponseDto.builder()
                    .paymentId(payment.getPaymentUuid())
                    .status(payment.getStatus().toString().toLowerCase())
                    .transactionId(payment.getTransactionId())
                    .redirectUrl(payment.getRedirectUrl())
                    .build();
                    
        } catch (Exception e) {
            log.error("Payment processing failed for contract: {}", request.getContractId(), e);
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setFailureReason(e.getMessage());
            paymentRepository.save(payment);
            
            throw new RuntimeException("Payment processing failed: " + e.getMessage());
        }
    }
    
    @Override
    public List<PaymentMethodDto> getAvailablePaymentMethods() {
        return Arrays.asList(
            PaymentMethodDto.builder()
                .id("credit_card")
                .name("クレジットカード")
                .type("credit_card")
                .isAvailable(true)
                .additionalFees(BigDecimal.ZERO)
                .build(),
            PaymentMethodDto.builder()
                .id("convenience_store")
                .name("コンビニ決済")
                .type("convenience_store")
                .isAvailable(true)
                .additionalFees(new BigDecimal("110"))
                .build(),
            PaymentMethodDto.builder()
                .id("bank_transfer")
                .name("銀行振込")
                .type("bank_transfer")
                .isAvailable(true)
                .additionalFees(new BigDecimal("220"))
                .build()
        );
    }
    
    @Override
    public Payment.PaymentStatus getPaymentStatus(String paymentId) {
        return paymentRepository.findByPaymentUuid(paymentId)
                .map(Payment::getStatus)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));
    }
    
    @Override
    public Payment getPaymentById(String paymentId) {
        return paymentRepository.findByPaymentUuid(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));
    }
    
    @Override
    public List<Payment> getPaymentsByContract(String contractId) {
        return paymentRepository.findByContractId(contractId);
    }
    
    private Payment createPaymentRecord(PaymentRequestDto request) {
        Payment payment = Payment.builder()
                .paymentUuid(UUID.randomUUID().toString())
                .contractId(request.getContractId())
                .paymentMethodId(request.getPaymentMethodId())
                .amount(calculatePaymentAmount(request.getContractId()))
                .status(Payment.PaymentStatus.PENDING)
                .build();
        
        return paymentRepository.save(payment);
    }
    
    private void updatePaymentFromGatewayResponse(Payment payment, GatewayResponse response) {
        if (response.isSuccess()) {
            payment.setStatus(Payment.PaymentStatus.COMPLETED);
            payment.setProcessedAt(LocalDateTime.now());
        } else {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setFailureReason(response.getMessage());
        }
        
        payment.setTransactionId(response.getTransactionId());
        payment.setGatewayResponse(response.getMessage());
        payment.setRedirectUrl(response.getRedirectUrl());
    }
    
    private void createPaymentHistory(Payment payment) {
        PaymentHistory history = PaymentHistory.builder()
                .paymentId(payment.getPaymentUuid())
                .customerId(payment.getContractId())
                .contractId(payment.getContractId())
                .amount(payment.getAmount())
                .paymentType(PaymentHistory.PaymentType.ONE_TIME)
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethodId())
                .transactionId(payment.getTransactionId())
                .description("Contract payment")
                .processedAt(payment.getProcessedAt())
                .build();
        
        paymentHistoryRepository.save(history);
    }
    
    private BigDecimal calculatePaymentAmount(String contractId) {
        return new BigDecimal("5000.00");
    }
}
