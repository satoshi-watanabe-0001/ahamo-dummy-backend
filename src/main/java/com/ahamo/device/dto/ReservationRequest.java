package com.ahamo.device.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {
    
    @NotBlank(message = "機種IDは必須です")
    private String deviceId;
    
    @NotBlank(message = "カラーは必須です")
    private String color;
    
    @NotBlank(message = "ストレージは必須です")
    private String storage;
    
    @NotNull(message = "数量は必須です")
    @Positive(message = "数量は1以上である必要があります")
    private Integer quantity = 1;
    
    private Long customerId;
}
