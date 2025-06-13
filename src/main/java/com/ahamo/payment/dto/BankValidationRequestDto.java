package com.ahamo.payment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankValidationRequestDto {
    @NotBlank(message = "銀行コードが必要です")
    @Pattern(regexp = "^[0-9]{4}$", message = "銀行コードは4桁の数字である必要があります")
    private String bankCode;
    
    @NotBlank(message = "支店コードが必要です")
    @Pattern(regexp = "^[0-9]{3}$", message = "支店コードは3桁の数字である必要があります")
    private String branchCode;
    
    @NotBlank(message = "口座番号が必要です")
    @Pattern(regexp = "^[0-9]{7,8}$", message = "口座番号は7-8桁の数字である必要があります")
    private String accountNumber;
    
    @NotBlank(message = "口座名義が必要です")
    private String accountName;
}
