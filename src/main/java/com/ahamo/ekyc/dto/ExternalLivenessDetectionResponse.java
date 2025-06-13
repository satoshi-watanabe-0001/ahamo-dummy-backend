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
public class ExternalLivenessDetectionResponse {
    
    private String responseId;
    private String requestId;
    private Boolean isLive;
    private BigDecimal livenessScore;
    private String confidenceLevel;
    private String challengeType;
    private Long processingTimeMs;
    private String serviceName;
}
