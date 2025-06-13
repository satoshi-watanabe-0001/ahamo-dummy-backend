package com.ahamo.ekyc.integration;

import com.ahamo.ekyc.dto.ExternalFaceComparisonRequest;
import com.ahamo.ekyc.dto.ExternalFaceComparisonResponse;
import com.ahamo.ekyc.dto.ExternalLivenessDetectionRequest;
import com.ahamo.ekyc.dto.ExternalLivenessDetectionResponse;

public interface ExternalAiServiceClient {
    
    String getServiceName();
    
    ExternalFaceComparisonResponse compareFaces(ExternalFaceComparisonRequest request);
    
    ExternalLivenessDetectionResponse detectLiveness(ExternalLivenessDetectionRequest request);
    
    boolean isServiceAvailable();
    
    void authenticateService();
    
    double getServiceConfidenceThreshold();
}
