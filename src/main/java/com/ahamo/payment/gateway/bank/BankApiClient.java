package com.ahamo.payment.gateway.bank;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class BankApiClient {
    
    private final RestTemplate restTemplate;
    
    @Value("${bank.api.url:}")
    private String bankApiUrl;
    
    @Value("${bank.api.enabled:false}")
    private boolean bankApiEnabled;
    
    @Data
    @Builder
    public static class BankInfo {
        private String code;
        private String name;
        private String kana;
        private boolean isAvailable;
    }
    
    @Data
    @Builder
    public static class BranchInfo {
        private String code;
        private String name;
        private String kana;
        private String bankCode;
        private boolean isAvailable;
    }
    
    @Data
    @Builder
    public static class BankSearchResponse {
        private List<BankInfo> banks;
        private int totalCount;
    }
    
    @Data
    @Builder
    public static class BranchSearchResponse {
        private List<BranchInfo> branches;
        private int totalCount;
    }
    
    public List<BankInfo> searchBanksByCode(String bankCode) {
        if (!bankApiEnabled) {
            return getMockBanks().stream()
                .filter(bank -> bank.getCode().contains(bankCode))
                .collect(Collectors.toList());
        }
        
        try {
            String url = bankApiUrl + "/banks/search?code=" + bankCode;
            BankSearchResponse response = restTemplate.getForObject(url, BankSearchResponse.class);
            return response != null ? response.getBanks() : Collections.emptyList();
        } catch (Exception e) {
            log.error("Bank search failed for code: " + bankCode, e);
            return Collections.emptyList();
        }
    }
    
    public List<BankInfo> searchBanksByName(String bankName) {
        if (!bankApiEnabled) {
            return getMockBanks().stream()
                .filter(bank -> bank.getName().contains(bankName) || bank.getKana().contains(bankName))
                .collect(Collectors.toList());
        }
        
        try {
            String url = bankApiUrl + "/banks/search?name=" + bankName;
            BankSearchResponse response = restTemplate.getForObject(url, BankSearchResponse.class);
            return response != null ? response.getBanks() : Collections.emptyList();
        } catch (Exception e) {
            log.error("Bank search failed for name: " + bankName, e);
            return Collections.emptyList();
        }
    }
    
    public List<BranchInfo> searchBranches(String bankCode, String branchName) {
        if (!bankApiEnabled) {
            return getMockBranches(bankCode).stream()
                .filter(branch -> branch.getName().contains(branchName) || branch.getKana().contains(branchName))
                .collect(Collectors.toList());
        }
        
        try {
            String url = bankApiUrl + "/banks/" + bankCode + "/branches/search?name=" + branchName;
            BranchSearchResponse response = restTemplate.getForObject(url, BranchSearchResponse.class);
            return response != null ? response.getBranches() : Collections.emptyList();
        } catch (Exception e) {
            log.error("Branch search failed for bank: " + bankCode + ", name: " + branchName, e);
            return Collections.emptyList();
        }
    }
    
    public boolean validateBankAccount(String bankCode, String branchCode, String accountNumber) {
        if (!bankApiEnabled) {
            return isValidMockAccount(bankCode, branchCode, accountNumber);
        }
        
        try {
            String url = bankApiUrl + "/validate-account";
            ValidationRequest request = ValidationRequest.builder()
                .bankCode(bankCode)
                .branchCode(branchCode)
                .accountNumber(accountNumber)
                .build();
            
            ValidationResponse response = restTemplate.postForObject(url, request, ValidationResponse.class);
            return response != null && response.isValid();
        } catch (Exception e) {
            log.error("Bank account validation failed", e);
            return false;
        }
    }
    
    @Data
    @Builder
    private static class ValidationRequest {
        private String bankCode;
        private String branchCode;
        private String accountNumber;
    }
    
    @Data
    @Builder
    private static class ValidationResponse {
        private boolean isValid;
        private String message;
    }
    
    private List<BankInfo> getMockBanks() {
        List<BankInfo> banks = new ArrayList<>();
        banks.add(BankInfo.builder().code("0001").name("みずほ銀行").kana("ミズホギンコウ").isAvailable(true).build());
        banks.add(BankInfo.builder().code("0005").name("三菱UFJ銀行").kana("ミツビシユーエフジェイギンコウ").isAvailable(true).build());
        banks.add(BankInfo.builder().code("0009").name("三井住友銀行").kana("ミツイスミトモギンコウ").isAvailable(true).build());
        banks.add(BankInfo.builder().code("0010").name("りそな銀行").kana("リソナギンコウ").isAvailable(true).build());
        banks.add(BankInfo.builder().code("0017").name("埼玉りそな銀行").kana("サイタマリソナギンコウ").isAvailable(true).build());
        banks.add(BankInfo.builder().code("0033").name("ジャパンネット銀行").kana("ジャパンネットギンコウ").isAvailable(true).build());
        banks.add(BankInfo.builder().code("0034").name("セブン銀行").kana("セブンギンコウ").isAvailable(true).build());
        banks.add(BankInfo.builder().code("0035").name("ソニー銀行").kana("ソニーギンコウ").isAvailable(true).build());
        return banks;
    }
    
    private List<BranchInfo> getMockBranches(String bankCode) {
        List<BranchInfo> branches = new ArrayList<>();
        
        switch (bankCode) {
            case "0001":
                branches.add(BranchInfo.builder().code("001").name("本店").kana("ホンテン").bankCode(bankCode).isAvailable(true).build());
                branches.add(BranchInfo.builder().code("002").name("東京営業部").kana("トウキョウエイギョウブ").bankCode(bankCode).isAvailable(true).build());
                branches.add(BranchInfo.builder().code("003").name("丸の内支店").kana("マルノウチシテン").bankCode(bankCode).isAvailable(true).build());
                break;
            case "0005":
                branches.add(BranchInfo.builder().code("001").name("本店").kana("ホンテン").bankCode(bankCode).isAvailable(true).build());
                branches.add(BranchInfo.builder().code("002").name("東京営業部").kana("トウキョウエイギョウブ").bankCode(bankCode).isAvailable(true).build());
                branches.add(BranchInfo.builder().code("003").name("丸の内支店").kana("マルノウチシテン").bankCode(bankCode).isAvailable(true).build());
                break;
            case "0009":
                branches.add(BranchInfo.builder().code("001").name("本店").kana("ホンテン").bankCode(bankCode).isAvailable(true).build());
                branches.add(BranchInfo.builder().code("002").name("東京営業部").kana("トウキョウエイギョウブ").bankCode(bankCode).isAvailable(true).build());
                branches.add(BranchInfo.builder().code("003").name("丸の内支店").kana("マルノウチシテン").bankCode(bankCode).isAvailable(true).build());
                break;
        }
        
        return branches;
    }
    
    private boolean isValidMockAccount(String bankCode, String branchCode, String accountNumber) {
        return getMockBanks().stream().anyMatch(bank -> bank.getCode().equals(bankCode)) &&
               getMockBranches(bankCode).stream().anyMatch(branch -> branch.getCode().equals(branchCode)) &&
               accountNumber.matches("^[0-9]{7,8}$");
    }
}
