package com.ahamo.payment.service;

import com.ahamo.payment.dto.PaymentMethodDto;
import com.ahamo.payment.dto.PaymentRequestDto;
import com.ahamo.payment.dto.PaymentResponseDto;
import com.ahamo.payment.model.Payment;

import java.util.List;

public interface PaymentService {
    
    PaymentResponseDto processPayment(PaymentRequestDto request);
    
    List<PaymentMethodDto> getAvailablePaymentMethods();
    
    Payment.PaymentStatus getPaymentStatus(String paymentId);
    
    Payment getPaymentById(String paymentId);
    
    List<Payment> getPaymentsByContract(String contractId);
}
