package com.ahamo.payment.validation;

import com.ahamo.payment.gateway.dto.CardDetails;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
@Slf4j
public class PaymentValidator {
    
    @Data
    @Builder
    public static class ValidationResult {
        private boolean isValid;
        private List<String> errors;
        private String message;
    }
    
    @Data
    @Builder
    public static class BankAccountDetails {
        private String bankCode;
        private String branchCode;
        private String accountNumber;
        private String accountName;
        private String accountType;
    }
    
    public ValidationResult validateCreditCard(CardDetails cardDetails) {
        List<String> errors = new ArrayList<>();
        
        if (!isValidCardNumber(cardDetails.getCardNumber())) {
            errors.add("カード番号が無効です");
        }
        
        if (!isValidExpiryDate(cardDetails.getExpiryMonth(), cardDetails.getExpiryYear())) {
            errors.add("有効期限が無効です");
        }
        
        if (!isValidCvv(cardDetails.getCvv(), cardDetails.getCardNumber())) {
            errors.add("セキュリティコードが無効です");
        }
        
        if (!isValidCardHolderName(cardDetails.getCardHolderName())) {
            errors.add("カード名義人名が無効です");
        }
        
        boolean isValid = errors.isEmpty();
        String message = isValid ? "カード情報の検証が完了しました" : "カード情報に問題があります";
        
        return ValidationResult.builder()
            .isValid(isValid)
            .errors(errors)
            .message(message)
            .build();
    }
    
    public ValidationResult validateBankAccount(BankAccountDetails bankDetails) {
        List<String> errors = new ArrayList<>();
        
        if (!isValidBankCode(bankDetails.getBankCode())) {
            errors.add("銀行コードが無効です");
        }
        
        if (!isValidBranchCode(bankDetails.getBranchCode())) {
            errors.add("支店コードが無効です");
        }
        
        if (!isValidAccountNumber(bankDetails.getAccountNumber())) {
            errors.add("口座番号が無効です");
        }
        
        if (!isValidAccountName(bankDetails.getAccountName())) {
            errors.add("口座名義が無効です");
        }
        
        boolean isValid = errors.isEmpty();
        String message = isValid ? "銀行口座情報の検証が完了しました" : "銀行口座情報に問題があります";
        
        return ValidationResult.builder()
            .isValid(isValid)
            .errors(errors)
            .message(message)
            .build();
    }
    
    private boolean isValidCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            return false;
        }
        
        String cleanNumber = cardNumber.replaceAll("\\s+", "");
        
        if (!Pattern.matches("^[0-9]{13,19}$", cleanNumber)) {
            return false;
        }
        
        return luhnCheck(cleanNumber);
    }
    
    private boolean luhnCheck(String cardNumber) {
        int sum = 0;
        boolean alternate = false;
        
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));
            
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }
            
            sum += digit;
            alternate = !alternate;
        }
        
        return (sum % 10) == 0;
    }
    
    private boolean isValidExpiryDate(String month, String year) {
        try {
            int expMonth = Integer.parseInt(month);
            int expYear = Integer.parseInt(year);
            
            if (expMonth < 1 || expMonth > 12) {
                return false;
            }
            
            if (expYear < 100) {
                expYear += 2000;
            }
            
            YearMonth expiry = YearMonth.of(expYear, expMonth);
            YearMonth current = YearMonth.now();
            
            return !expiry.isBefore(current);
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private boolean isValidCvv(String cvv, String cardNumber) {
        if (cvv == null || cvv.trim().isEmpty()) {
            return false;
        }
        
        String cleanCardNumber = cardNumber.replaceAll("\\s+", "");
        boolean isAmex = cleanCardNumber.startsWith("34") || cleanCardNumber.startsWith("37");
        
        if (isAmex) {
            return Pattern.matches("^[0-9]{4}$", cvv);
        } else {
            return Pattern.matches("^[0-9]{3}$", cvv);
        }
    }
    
    private boolean isValidCardHolderName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        
        return Pattern.matches("^[a-zA-Z\\s]{2,50}$", name.trim());
    }
    
    private boolean isValidBankCode(String bankCode) {
        return bankCode != null && Pattern.matches("^[0-9]{4}$", bankCode);
    }
    
    private boolean isValidBranchCode(String branchCode) {
        return branchCode != null && Pattern.matches("^[0-9]{3}$", branchCode);
    }
    
    private boolean isValidAccountNumber(String accountNumber) {
        return accountNumber != null && Pattern.matches("^[0-9]{7,8}$", accountNumber);
    }
    
    private boolean isValidAccountName(String accountName) {
        if (accountName == null || accountName.trim().isEmpty()) {
            return false;
        }
        
        return accountName.trim().length() >= 1 && accountName.trim().length() <= 30;
    }
}
