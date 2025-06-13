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
public class FacialDetectionResponse {
    
    private String detectionId;
    private Boolean isLive;
    private BigDecimal confidenceScore;
    private Boolean faceDetected;
    private BigDecimal qualityScore;
    private BiometricFeatures biometricFeatures;
    private List<String> guidance;
    private NextChallenge nextChallenge;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BiometricFeatures {
        private Boolean eyeBlinkDetected;
        private Boolean microMovementDetected;
        private Boolean headRotationDetected;
    }
    
    public enum NextChallenge {
        BLINK, HEAD_TURN, SMILE, COMPLETE
    }
}
