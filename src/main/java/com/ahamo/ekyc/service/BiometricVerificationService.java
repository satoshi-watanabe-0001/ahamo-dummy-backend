package com.ahamo.ekyc.service;

import com.ahamo.ekyc.dto.BiometricAnalysisResult;
import com.ahamo.ekyc.dto.LivenessDetectionResult;

public interface BiometricVerificationService {
    
    LivenessDetectionResult detectLiveness(String imageData, String challengeType);
    
    BiometricAnalysisResult analyzeBiometricFeatures(String imageData);
    
    boolean validateEyeBlinkPattern(String[] imageSequence);
    
    boolean detectMicroMovements(String[] imageSequence);
    
    boolean validateHeadRotation(String imageData, String expectedDirection);
    
    double calculateLivenessScore(BiometricAnalysisResult analysis);
    
    boolean isSpoofingAttempt(BiometricAnalysisResult analysis);
}
