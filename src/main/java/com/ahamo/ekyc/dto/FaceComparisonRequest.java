package com.ahamo.ekyc.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FaceComparisonRequest {
    
    @NotBlank(message = "身分証明書の画像データは必須です")
    private String documentImage;
    
    @NotBlank(message = "自撮り画像データは必須です")
    private String selfieImage;
    
    @DecimalMin(value = "0.0", message = "類似度閾値は0以上である必要があります")
    @DecimalMax(value = "100.0", message = "類似度閾値は100以下である必要があります")
    @Builder.Default
    private BigDecimal similarityThreshold = BigDecimal.valueOf(80.0);
}
