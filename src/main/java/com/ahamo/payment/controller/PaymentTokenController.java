package com.ahamo.payment.controller;

import com.ahamo.payment.dto.TokenizationRequestDto;
import com.ahamo.payment.dto.BankSearchRequestDto;
import com.ahamo.payment.dto.BankValidationRequestDto;
import com.ahamo.payment.dto.TokenValidationRequestDto;
import com.ahamo.payment.gateway.dto.CardDetails;
import com.ahamo.payment.gateway.dto.TokenizationResult;
import com.ahamo.payment.security.TokenizationService;
import com.ahamo.payment.validation.PaymentValidator;
import com.ahamo.payment.gateway.bank.BankApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@Slf4j
public class PaymentTokenController {
    
    @Autowired
    @Qualifier("tokenizationServiceImpl")
    private TokenizationService tokenizationService;
    
    @Autowired
    private PaymentValidator paymentValidator;
    
    @Autowired
    private BankApiClient bankApiClient;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private static final String DECRYPTION_KEY = "ahamo_contract_form_key_2024";
    
    @PostMapping("/tokenize")
    public ResponseEntity<TokenizationResult> tokenizeCard(@Valid @RequestBody TokenizationRequestDto request) {
        try {
            String encryptedCardData = request.getEncryptedCardData();
            if (encryptedCardData == null) {
                return ResponseEntity.badRequest().body(
                    TokenizationResult.builder()
                        .success(false)
                        .errorMessage("暗号化されたカードデータが必要です")
                        .build()
                );
            }
            
            String decryptedData = decrypt(encryptedCardData);
            CardDetails cardDetails = objectMapper.readValue(decryptedData, CardDetails.class);
            
            PaymentValidator.ValidationResult validation = paymentValidator.validateCreditCard(cardDetails);
            if (!validation.isValid()) {
                return ResponseEntity.badRequest().body(
                    TokenizationResult.builder()
                        .success(false)
                        .errorMessage(String.join(", ", validation.getErrors()))
                        .build()
                );
            }
            
            TokenizationResult result = tokenizationService.tokenizeCard(cardDetails);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Card tokenization failed", e);
            return ResponseEntity.badRequest().body(
                TokenizationResult.builder()
                    .success(false)
                    .errorMessage("カードのトークン化に失敗しました")
                    .build()
            );
        }
    }
    
    @PostMapping("/validate-token")
    public ResponseEntity<Map<String, Boolean>> validateToken(@Valid @RequestBody TokenValidationRequestDto request) {
        try {
            boolean isValid = tokenizationService.validateToken(request.getToken());
            return ResponseEntity.ok(Map.of("valid", isValid));
        } catch (Exception e) {
            log.error("Token validation failed", e);
            return ResponseEntity.ok(Map.of("valid", false));
        }
    }
    
    @GetMapping("/banks/search")
    public ResponseEntity<List<BankApiClient.BankInfo>> searchBanks(@RequestParam String q) {
        try {
            List<BankApiClient.BankInfo> banks;
            if (q.matches("^[0-9]+$")) {
                banks = bankApiClient.searchBanksByCode(q);
            } else {
                banks = bankApiClient.searchBanksByName(q);
            }
            return ResponseEntity.ok(banks);
        } catch (Exception e) {
            log.error("Bank search failed", e);
            return ResponseEntity.ok(List.of());
        }
    }
    
    @GetMapping("/banks/{bankCode}/branches/search")
    public ResponseEntity<List<BankApiClient.BranchInfo>> searchBranches(
            @PathVariable String bankCode, 
            @RequestParam String q) {
        try {
            List<BankApiClient.BranchInfo> branches = bankApiClient.searchBranches(bankCode, q);
            return ResponseEntity.ok(branches);
        } catch (Exception e) {
            log.error("Branch search failed", e);
            return ResponseEntity.ok(List.of());
        }
    }
    
    @PostMapping("/validate-bank-account")
    public ResponseEntity<Map<String, Object>> validateBankAccount(@Valid @RequestBody BankValidationRequestDto request) {
        try {
            PaymentValidator.BankAccountDetails bankDetails = PaymentValidator.BankAccountDetails.builder()
                .bankCode(request.getBankCode())
                .branchCode(request.getBranchCode())
                .accountNumber(request.getAccountNumber())
                .accountName(request.getAccountName())
                .build();
            
            PaymentValidator.ValidationResult validation = paymentValidator.validateBankAccount(bankDetails);
            boolean accountExists = bankApiClient.validateBankAccount(
                request.getBankCode(), 
                request.getBranchCode(), 
                request.getAccountNumber()
            );
            
            return ResponseEntity.ok(Map.of(
                "valid", validation.isValid() && accountExists,
                "message", validation.getMessage(),
                "errors", validation.getErrors()
            ));
        } catch (Exception e) {
            log.error("Bank account validation failed", e);
            return ResponseEntity.ok(Map.of(
                "valid", false,
                "message", "銀行口座の検証に失敗しました"
            ));
        }
    }
    
    private String decrypt(String encryptedData) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(
            DECRYPTION_KEY.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedData = cipher.doFinal(decodedData);
        return new String(decryptedData, StandardCharsets.UTF_8);
    }
}
