package com.ahamo.mnp.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class MnpEligibilityRequest {
    
    @NotBlank(message = "電話番号は必須です")
    @Pattern(regexp = "^\\d{10,11}$", message = "有効な電話番号を入力してください")
    private String phoneNumber;
    
    @NotBlank(message = "現在のキャリアは必須です")
    private String currentCarrier;
}
