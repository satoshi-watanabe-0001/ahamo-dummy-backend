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
public class QualityCheckResponse {
    
    private String checkId;
    private BigDecimal overallScore;
    private Boolean isAcceptable;
    private QualityMetrics qualityMetrics;
    private List<String> improvementSuggestions;
    private Boolean retryRecommended;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QualityMetrics {
        private BigDecimal sharpnessScore;
        private BigDecimal brightnessScore;
        private BigDecimal contrastScore;
        private Boolean glareDetected;
        private Boolean blurDetected;
        private Boolean resolutionAdequate;
    }
}
