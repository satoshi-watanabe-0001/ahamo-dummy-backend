package com.ahamo.ekyc.service.impl;

import com.ahamo.ekyc.dto.BiometricAnalysisResult;
import com.ahamo.ekyc.dto.LivenessDetectionResult;
import com.ahamo.ekyc.service.BiometricVerificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class BiometricVerificationServiceImpl implements BiometricVerificationService {

    @Value("${ekyc.biometric.liveness-threshold:75.0}")
    private BigDecimal livenessThreshold;

    @Value("${ekyc.biometric.spoofing-threshold:80.0}")
    private BigDecimal spoofingThreshold;

    @Override
    public LivenessDetectionResult detectLiveness(String imageData, String challengeType) {
        log.info("生体検知開始: challengeType={}", challengeType);
        
        try {
            String detectionId = UUID.randomUUID().toString();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            
            BigDecimal confidenceScore = simulateLivenessDetection(imageData, challengeType);
            boolean isLive = confidenceScore.compareTo(livenessThreshold) >= 0;
            boolean challengeCompleted = isLive && isValidChallenge(challengeType);
            
            List<String> detectedFeatures = analyzeDetectedFeatures(imageData, challengeType);
            String nextChallenge = determineNextChallenge(challengeType, challengeCompleted);
            
            return LivenessDetectionResult.builder()
                .detectionId(detectionId)
                .isLive(isLive)
                .confidenceScore(confidenceScore)
                .challengeType(challengeType)
                .challengeCompleted(challengeCompleted)
                .detectedFeatures(detectedFeatures)
                .nextChallenge(nextChallenge)
                .analysisTimestamp(timestamp)
                .build();
                
        } catch (Exception e) {
            log.error("生体検知エラー: challengeType={}", challengeType, e);
            throw new RuntimeException("生体検知に失敗しました", e);
        }
    }

    @Override
    public BiometricAnalysisResult analyzeBiometricFeatures(String imageData) {
        log.info("生体特徴分析開始");
        
        try {
            String analysisId = UUID.randomUUID().toString();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            
            Map<String, Object> biometricFeatures = extractBiometricFeatures(imageData);
            BigDecimal livenessScore = calculateLivenessScore(biometricFeatures);
            boolean isLive = livenessScore.compareTo(livenessThreshold) >= 0;
            boolean spoofingDetected = detectSpoofingAttempt(biometricFeatures);
            
            return BiometricAnalysisResult.builder()
                .analysisId(analysisId)
                .isLive(isLive)
                .livenessScore(livenessScore)
                .spoofingDetected(spoofingDetected)
                .biometricFeatures(biometricFeatures)
                .analysisTimestamp(timestamp)
                .challengeType("GENERAL")
                .confidenceLevel(livenessScore)
                .build();
                
        } catch (Exception e) {
            log.error("生体特徴分析エラー", e);
            throw new RuntimeException("生体特徴分析に失敗しました", e);
        }
    }

    @Override
    public boolean validateEyeBlinkPattern(String[] imageSequence) {
        log.info("瞬きパターン検証開始: imageCount={}", imageSequence.length);
        
        try {
            if (imageSequence.length < 3) {
                log.warn("瞬きパターン検証: 画像数が不足 imageCount={}", imageSequence.length);
                return false;
            }
            
            int blinkCount = 0;
            boolean previousEyesClosed = false;
            
            for (String imageData : imageSequence) {
                boolean eyesClosed = detectEyesClosed(imageData);
                
                if (!previousEyesClosed && eyesClosed) {
                    blinkCount++;
                }
                
                previousEyesClosed = eyesClosed;
            }
            
            boolean isValidBlink = blinkCount >= 1 && blinkCount <= 3;
            log.info("瞬きパターン検証完了: blinkCount={}, isValid={}", blinkCount, isValidBlink);
            
            return isValidBlink;
            
        } catch (Exception e) {
            log.error("瞬きパターン検証エラー", e);
            return false;
        }
    }

    @Override
    public boolean detectMicroMovements(String[] imageSequence) {
        log.info("微細動作検知開始: imageCount={}", imageSequence.length);
        
        try {
            if (imageSequence.length < 2) {
                return false;
            }
            
            double totalMovement = 0.0;
            
            for (int i = 1; i < imageSequence.length; i++) {
                double movement = calculateImageDifference(imageSequence[i-1], imageSequence[i]);
                totalMovement += movement;
            }
            
            double averageMovement = totalMovement / (imageSequence.length - 1);
            boolean hasValidMovement = averageMovement > 0.1 && averageMovement < 5.0;
            
            log.info("微細動作検知完了: averageMovement={}, hasValidMovement={}", averageMovement, hasValidMovement);
            
            return hasValidMovement;
            
        } catch (Exception e) {
            log.error("微細動作検知エラー", e);
            return false;
        }
    }

    @Override
    public boolean validateHeadRotation(String imageData, String expectedDirection) {
        log.info("頭部回転検証開始: expectedDirection={}", expectedDirection);
        
        try {
            double rotationAngle = detectHeadRotationAngle(imageData);
            boolean isValidRotation = false;
            
            switch (expectedDirection.toUpperCase()) {
                case "LEFT":
                    isValidRotation = rotationAngle > 10 && rotationAngle < 45;
                    break;
                case "RIGHT":
                    isValidRotation = rotationAngle < -10 && rotationAngle > -45;
                    break;
                case "UP":
                    isValidRotation = Math.abs(rotationAngle) < 10;
                    break;
                default:
                    log.warn("未対応の回転方向: {}", expectedDirection);
                    break;
            }
            
            log.info("頭部回転検証完了: rotationAngle={}, expectedDirection={}, isValid={}", 
                    rotationAngle, expectedDirection, isValidRotation);
            
            return isValidRotation;
            
        } catch (Exception e) {
            log.error("頭部回転検証エラー: expectedDirection={}", expectedDirection, e);
            return false;
        }
    }

    @Override
    public double calculateLivenessScore(BiometricAnalysisResult analysis) {
        try {
            Map<String, Object> features = analysis.getBiometricFeatures();
            double score = 0.0;
            
            if (features.containsKey("eye_blink") && (Boolean) features.get("eye_blink")) {
                score += 30.0;
            }
            
            if (features.containsKey("micro_movement") && (Boolean) features.get("micro_movement")) {
                score += 25.0;
            }
            
            if (features.containsKey("head_rotation") && (Boolean) features.get("head_rotation")) {
                score += 20.0;
            }
            
            if (features.containsKey("facial_texture") && (Boolean) features.get("facial_texture")) {
                score += 15.0;
            }
            
            if (features.containsKey("depth_perception") && (Boolean) features.get("depth_perception")) {
                score += 10.0;
            }
            
            return Math.min(score, 100.0);
            
        } catch (Exception e) {
            log.error("生体スコア計算エラー", e);
            return 0.0;
        }
    }

    @Override
    public boolean isSpoofingAttempt(BiometricAnalysisResult analysis) {
        try {
            Map<String, Object> features = analysis.getBiometricFeatures();
            
            boolean hasStaticFeatures = features.containsKey("static_image") && (Boolean) features.get("static_image");
            boolean hasLowDepth = features.containsKey("low_depth") && (Boolean) features.get("low_depth");
            boolean hasUniformTexture = features.containsKey("uniform_texture") && (Boolean) features.get("uniform_texture");
            boolean hasScreenReflection = features.containsKey("screen_reflection") && (Boolean) features.get("screen_reflection");
            
            boolean isSpoofing = hasStaticFeatures || hasLowDepth || hasUniformTexture || hasScreenReflection;
            
            if (isSpoofing) {
                log.warn("なりすまし検知: static={}, lowDepth={}, uniformTexture={}, screenReflection={}", 
                        hasStaticFeatures, hasLowDepth, hasUniformTexture, hasScreenReflection);
            }
            
            return isSpoofing;
            
        } catch (Exception e) {
            log.error("なりすまし検知エラー", e);
            return true;
        }
    }

    private BigDecimal simulateLivenessDetection(String imageData, String challengeType) {
        Random random = new Random();
        double baseScore = 70.0 + (random.nextDouble() * 25.0);
        
        switch (challengeType.toUpperCase()) {
            case "BLINK":
                baseScore += random.nextDouble() * 10.0;
                break;
            case "HEAD_TURN":
                baseScore += random.nextDouble() * 8.0;
                break;
            case "SMILE":
                baseScore += random.nextDouble() * 6.0;
                break;
        }
        
        return BigDecimal.valueOf(Math.min(baseScore, 100.0));
    }

    private boolean isValidChallenge(String challengeType) {
        return Arrays.asList("BLINK", "HEAD_TURN", "SMILE").contains(challengeType.toUpperCase());
    }

    private List<String> analyzeDetectedFeatures(String imageData, String challengeType) {
        List<String> features = new ArrayList<>();
        
        switch (challengeType.toUpperCase()) {
            case "BLINK":
                features.add("EYE_BLINK");
                features.add("MICRO_MOVEMENT");
                break;
            case "HEAD_TURN":
                features.add("HEAD_ROTATION");
                features.add("FACIAL_GEOMETRY");
                break;
            case "SMILE":
                features.add("FACIAL_EXPRESSION");
                features.add("MOUTH_MOVEMENT");
                break;
        }
        
        return features;
    }

    private String determineNextChallenge(String currentChallenge, boolean completed) {
        if (!completed) {
            return currentChallenge;
        }
        
        switch (currentChallenge.toUpperCase()) {
            case "BLINK":
                return "HEAD_TURN";
            case "HEAD_TURN":
                return "SMILE";
            case "SMILE":
                return "COMPLETE";
            default:
                return "BLINK";
        }
    }

    private Map<String, Object> extractBiometricFeatures(String imageData) {
        Map<String, Object> features = new HashMap<>();
        Random random = new Random();
        
        features.put("eye_blink", random.nextBoolean());
        features.put("micro_movement", random.nextBoolean());
        features.put("head_rotation", random.nextBoolean());
        features.put("facial_texture", random.nextBoolean());
        features.put("depth_perception", random.nextBoolean());
        features.put("static_image", random.nextDouble() < 0.1);
        features.put("low_depth", random.nextDouble() < 0.15);
        features.put("uniform_texture", random.nextDouble() < 0.1);
        features.put("screen_reflection", random.nextDouble() < 0.05);
        
        return features;
    }

    private BigDecimal calculateLivenessScore(Map<String, Object> features) {
        double score = 0.0;
        
        if ((Boolean) features.getOrDefault("eye_blink", false)) score += 25.0;
        if ((Boolean) features.getOrDefault("micro_movement", false)) score += 20.0;
        if ((Boolean) features.getOrDefault("head_rotation", false)) score += 20.0;
        if ((Boolean) features.getOrDefault("facial_texture", false)) score += 15.0;
        if ((Boolean) features.getOrDefault("depth_perception", false)) score += 20.0;
        
        if ((Boolean) features.getOrDefault("static_image", false)) score -= 30.0;
        if ((Boolean) features.getOrDefault("low_depth", false)) score -= 25.0;
        if ((Boolean) features.getOrDefault("uniform_texture", false)) score -= 20.0;
        if ((Boolean) features.getOrDefault("screen_reflection", false)) score -= 15.0;
        
        return BigDecimal.valueOf(Math.max(0.0, Math.min(score, 100.0)));
    }

    private boolean detectSpoofingAttempt(Map<String, Object> features) {
        return (Boolean) features.getOrDefault("static_image", false) ||
               (Boolean) features.getOrDefault("low_depth", false) ||
               (Boolean) features.getOrDefault("uniform_texture", false) ||
               (Boolean) features.getOrDefault("screen_reflection", false);
    }

    private boolean detectEyesClosed(String imageData) {
        Random random = new Random();
        return random.nextDouble() < 0.3;
    }

    private double calculateImageDifference(String image1, String image2) {
        Random random = new Random();
        return random.nextDouble() * 2.0;
    }

    private double detectHeadRotationAngle(String imageData) {
        Random random = new Random();
        return (random.nextDouble() - 0.5) * 60.0;
    }
}
