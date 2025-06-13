package com.ahamo.payment.service;

import com.ahamo.payment.dto.PaymentMethodResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class PaymentMethodService {
    
    public List<PaymentMethodResponseDto> getAvailablePaymentMethods() {
        return Arrays.asList(
            PaymentMethodResponseDto.builder()
                .id("credit")
                .name("クレジットカード")
                .type("credit")
                .description("Visa, Mastercard, JCB, American Express対応")
                .isAvailable(true)
                .additionalFees(BigDecimal.ZERO)
                .icon("💳")
                .build(),
            PaymentMethodResponseDto.builder()
                .id("bank")
                .name("銀行口座振替")
                .type("bank")
                .description("毎月自動引き落とし（手数料無料）")
                .isAvailable(true)
                .additionalFees(BigDecimal.ZERO)
                .icon("🏦")
                .build(),
            PaymentMethodResponseDto.builder()
                .id("convenience")
                .name("コンビニ払い")
                .type("convenience")
                .description("毎月コンビニでお支払い（手数料110円）")
                .isAvailable(true)
                .additionalFees(new BigDecimal("110"))
                .icon("🏪")
                .build()
        );
    }
    
    public boolean isPaymentMethodAvailable(String paymentMethodId) {
        return getAvailablePaymentMethods().stream()
            .anyMatch(method -> method.getId().equals(paymentMethodId) && method.isAvailable());
    }
    
    public BigDecimal getAdditionalFees(String paymentMethodId) {
        return getAvailablePaymentMethods().stream()
            .filter(method -> method.getId().equals(paymentMethodId))
            .findFirst()
            .map(PaymentMethodResponseDto::getAdditionalFees)
            .orElse(BigDecimal.ZERO);
    }
}
