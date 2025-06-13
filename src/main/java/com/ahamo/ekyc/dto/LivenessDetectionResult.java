package com.ahamo.ekyc.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LivenessDetectionResult {
    
    private String detectionId;
    private Boolean isLive;
    private BigDecimal confidenceScore;
    private String challengeType;
    private Boolean challengeCompleted;
    private List<String> detectedFeatures;
    private String nextChallenge;
    private String analysisTimestamp;
}
