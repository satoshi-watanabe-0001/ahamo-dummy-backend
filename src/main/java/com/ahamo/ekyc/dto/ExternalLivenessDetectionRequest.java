package com.ahamo.ekyc.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExternalLivenessDetectionRequest {
    
    private String requestId;
    private String imageData;
    private String challengeType;
    private String sessionId;
}
