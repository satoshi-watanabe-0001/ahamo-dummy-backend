package com.ahamo.payment.dto;

import com.ahamo.payment.model.Payment;
import com.ahamo.payment.model.PaymentHistory;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentHistoryDto {
    private String paymentId;
    private String customerId;
    private String contractId;
    private BigDecimal amount;
    private PaymentHistory.PaymentType paymentType;
    private Payment.PaymentStatus status;
    private String paymentMethod;
    private String transactionId;
    private String description;
    private LocalDateTime processedAt;
}
