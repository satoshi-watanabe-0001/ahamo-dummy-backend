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
public class ExternalFaceComparisonRequest {
    
    private String requestId;
    private String sourceImage;
    private String targetImage;
    private BigDecimal similarityThreshold;
    private String sessionId;
}
