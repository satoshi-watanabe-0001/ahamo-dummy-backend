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
public class ExternalFaceComparisonResponse {
    
    private String responseId;
    private String requestId;
    private Boolean isMatch;
    private BigDecimal similarityScore;
    private String confidenceLevel;
    private Long processingTimeMs;
    private String serviceName;
}
