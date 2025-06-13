package com.ahamo.payment.validation;

import com.ahamo.payment.gateway.dto.CardDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentValidatorTest {

    private PaymentValidator paymentValidator;

    @BeforeEach
    void setUp() {
        paymentValidator = new PaymentValidator();
    }

    @Test
    void testValidateCreditCard_ValidCard() {
        CardDetails cardDetails = CardDetails.builder()
            .cardNumber("4111111111111111")
            .expiryMonth("12")
            .expiryYear("2025")
            .cvv("123")
            .cardHolderName("TARO YAMADA")
            .build();

        PaymentValidator.ValidationResult result = paymentValidator.validateCreditCard(cardDetails);

        assertTrue(result.isValid());
        assertEquals("カード情報の検証が完了しました", result.getMessage());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void testValidateCreditCard_InvalidCardNumber() {
        CardDetails cardDetails = CardDetails.builder()
            .cardNumber("1234567890123456")
            .expiryMonth("12")
            .expiryYear("2025")
            .cvv("123")
            .cardHolderName("TARO YAMADA")
            .build();

        PaymentValidator.ValidationResult result = paymentValidator.validateCreditCard(cardDetails);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("カード番号が無効です"));
    }

    @Test
    void testValidateBankAccount_ValidAccount() {
        PaymentValidator.BankAccountDetails bankDetails = PaymentValidator.BankAccountDetails.builder()
            .bankCode("0001")
            .branchCode("001")
            .accountNumber("1234567")
            .accountName("ヤマダ タロウ")
            .build();

        PaymentValidator.ValidationResult result = paymentValidator.validateBankAccount(bankDetails);

        assertTrue(result.isValid());
        assertEquals("銀行口座情報の検証が完了しました", result.getMessage());
    }

    @Test
    void testValidateBankAccount_InvalidBankCode() {
        PaymentValidator.BankAccountDetails bankDetails = PaymentValidator.BankAccountDetails.builder()
            .bankCode("invalid")
            .branchCode("001")
            .accountNumber("1234567")
            .accountName("ヤマダ タロウ")
            .build();

        PaymentValidator.ValidationResult result = paymentValidator.validateBankAccount(bankDetails);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("銀行コードが無効です"));
    }
}
