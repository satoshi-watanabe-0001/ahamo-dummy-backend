package com.ahamo.mnp.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
public class TransferInRequest {
    
    @NotBlank(message = "電話番号は必須です")
    @Pattern(regexp = "^\\d{10,11}$", message = "有効な電話番号を入力してください")
    private String phoneNumber;
    
    @NotBlank(message = "MNP予約番号は必須です")
    @Pattern(regexp = "^\\d{10}$", message = "MNP予約番号は10桁の数字で入力してください")
    private String reservationNumber;
    
    @NotBlank(message = "転出元キャリアは必須です")
    private String fromCarrier;
    
    @NotBlank(message = "契約IDは必須です")
    private String contractId;
    
    private LocalDate desiredTransferDate;
}
