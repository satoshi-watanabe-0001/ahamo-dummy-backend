package com.ahamo.ekyc.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacialDetectionRequest {
    
    @NotBlank(message = "画像データは必須です")
    private String imageData;
    
    @NotNull(message = "検知タイプは必須です")
    private DetectionType detectionType;
    
    private ChallengeType challengeType;
    
    @DecimalMin(value = "0.0", message = "品質閾値は0以上である必要があります")
    @DecimalMax(value = "100.0", message = "品質閾値は100以下である必要があります")
    @Builder.Default
    private BigDecimal qualityThreshold = BigDecimal.valueOf(80.0);
    
    public enum DetectionType {
        LIVENESS, FACE_DETECTION, BIOMETRIC_ANALYSIS
    }
    
    public enum ChallengeType {
        BLINK, HEAD_TURN, SMILE
    }
}
