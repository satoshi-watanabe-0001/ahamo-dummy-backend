package com.ahamo.mnp.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
public class MnpRequest {
    
    @NotBlank(message = "契約IDは必須です")
    private String contractId;
    
    @NotBlank(message = "電話番号は必須です")
    @Pattern(regexp = "^\\d{10,11}$", message = "有効な電話番号を入力してください")
    private String phoneNumber;
    
    @NotBlank(message = "現在のキャリアは必須です")
    private String currentCarrier;
    
    @Valid
    @NotNull(message = "アカウント情報は必須です")
    private AccountInfo accountInfo;
    
    private LocalDate desiredPortingDate;
    
    @Data
    public static class AccountInfo {
        @NotBlank(message = "アカウント名は必須です")
        private String accountName;
        
        @NotBlank(message = "アカウント番号は必須です")
        private String accountNumber;
        
        @NotBlank(message = "パスワードは必須です")
        private String password;
    }
}
