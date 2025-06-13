package com.ahamo.ekyc.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FaceComparisonResponse {
    
    private String comparisonId;
    private Boolean isMatch;
    private BigDecimal similarityScore;
    private ConfidenceLevel confidenceLevel;
    private AnalysisDetails analysisDetails;
    private VerificationStatus verificationStatus;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AnalysisDetails {
        private Boolean faceLandmarksMatch;
        private Boolean facialGeometryMatch;
        private Boolean ageConsistency;
    }
    
    public enum ConfidenceLevel {
        HIGH, MEDIUM, LOW
    }
    
    public enum VerificationStatus {
        VERIFIED, REJECTED, MANUAL_REVIEW
    }
}
