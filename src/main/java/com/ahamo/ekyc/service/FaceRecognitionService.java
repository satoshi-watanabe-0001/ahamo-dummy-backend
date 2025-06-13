package com.ahamo.ekyc.service;

import com.ahamo.ekyc.dto.FacialDetectionRequest;
import com.ahamo.ekyc.dto.FacialDetectionResponse;
import com.ahamo.ekyc.dto.FaceComparisonRequest;
import com.ahamo.ekyc.dto.FaceComparisonResponse;
import com.ahamo.ekyc.dto.QualityCheckRequest;
import com.ahamo.ekyc.dto.QualityCheckResponse;

public interface FaceRecognitionService {
    
    FacialDetectionResponse detectFacialFeatures(String sessionId, FacialDetectionRequest request);
    
    FaceComparisonResponse compareFaces(String sessionId, FaceComparisonRequest request);
    
    QualityCheckResponse checkImageQuality(String sessionId, QualityCheckRequest request);
    
    void cleanupFacialData(String sessionId);
    
    boolean validateSessionSecurity(String sessionId);
}
