package com.ahamo.mnp.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class TransferOutRequest {
    
    @NotBlank(message = "電話番号は必須です")
    @Pattern(regexp = "^\\d{10,11}$", message = "有効な電話番号を入力してください")
    private String phoneNumber;
    
    @NotBlank(message = "転出先キャリアは必須です")
    private String toCarrier;
    
    @NotBlank(message = "契約IDは必須です")
    private String contractId;
    
    private String reason;
}
