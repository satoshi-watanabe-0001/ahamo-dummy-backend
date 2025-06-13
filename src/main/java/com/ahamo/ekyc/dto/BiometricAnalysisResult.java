package com.ahamo.ekyc.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BiometricAnalysisResult {
    
    private String analysisId;
    private Boolean isLive;
    private BigDecimal livenessScore;
    private Boolean spoofingDetected;
    private Map<String, Object> biometricFeatures;
    private String analysisTimestamp;
    private String challengeType;
    private BigDecimal confidenceLevel;
}
