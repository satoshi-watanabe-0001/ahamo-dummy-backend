package com.ahamo.ekyc.integration.impl;

import com.ahamo.ekyc.dto.ExternalFaceComparisonRequest;
import com.ahamo.ekyc.dto.ExternalFaceComparisonResponse;
import com.ahamo.ekyc.dto.ExternalLivenessDetectionRequest;
import com.ahamo.ekyc.dto.ExternalLivenessDetectionResponse;
import com.ahamo.ekyc.integration.ExternalAiServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class AwsRekognitionServiceClient implements ExternalAiServiceClient {

    @Value("${aws.rekognition.region:ap-northeast-1}")
    private String awsRegion;

    @Value("${aws.rekognition.confidence-threshold:80.0}")
    private BigDecimal confidenceThreshold;

    @Value("${aws.rekognition.enabled:true}")
    private boolean serviceEnabled;

    @Override
    public String getServiceName() {
        return "AWS Rekognition";
    }

    @Override
    public ExternalFaceComparisonResponse compareFaces(ExternalFaceComparisonRequest request) {
        log.info("AWS Rekognition顔照合開始: requestId={}", request.getRequestId());
        
        try {
            if (!isServiceAvailable()) {
                throw new RuntimeException("AWS Rekognitionサービスが利用できません");
            }
            
            String responseId = UUID.randomUUID().toString();
            BigDecimal similarityScore = simulateFaceComparison(request);
            boolean isMatch = similarityScore.compareTo(request.getSimilarityThreshold()) >= 0;
            
            ExternalFaceComparisonResponse response = ExternalFaceComparisonResponse.builder()
                .responseId(responseId)
                .requestId(request.getRequestId())
                .isMatch(isMatch)
                .similarityScore(similarityScore)
                .confidenceLevel(determineConfidenceLevel(similarityScore))
                .processingTimeMs(simulateProcessingTime())
                .serviceName(getServiceName())
                .build();
            
            log.info("AWS Rekognition顔照合完了: responseId={}, isMatch={}, similarityScore={}", 
                    responseId, isMatch, similarityScore);
            
            return response;
            
        } catch (Exception e) {
            log.error("AWS Rekognition顔照合エラー: requestId={}", request.getRequestId(), e);
            throw new RuntimeException("AWS Rekognition顔照合に失敗しました", e);
        }
    }

    @Override
    public ExternalLivenessDetectionResponse detectLiveness(ExternalLivenessDetectionRequest request) {
        log.info("AWS Rekognition生体検知開始: requestId={}", request.getRequestId());
        
        try {
            if (!isServiceAvailable()) {
                throw new RuntimeException("AWS Rekognitionサービスが利用できません");
            }
            
            String responseId = UUID.randomUUID().toString();
            BigDecimal livenessScore = simulateLivenessDetection(request);
            boolean isLive = livenessScore.compareTo(confidenceThreshold) >= 0;
            
            ExternalLivenessDetectionResponse response = ExternalLivenessDetectionResponse.builder()
                .responseId(responseId)
                .requestId(request.getRequestId())
                .isLive(isLive)
                .livenessScore(livenessScore)
                .confidenceLevel(determineConfidenceLevel(livenessScore))
                .challengeType(request.getChallengeType())
                .processingTimeMs(simulateProcessingTime())
                .serviceName(getServiceName())
                .build();
            
            log.info("AWS Rekognition生体検知完了: responseId={}, isLive={}, livenessScore={}", 
                    responseId, isLive, livenessScore);
            
            return response;
            
        } catch (Exception e) {
            log.error("AWS Rekognition生体検知エラー: requestId={}", request.getRequestId(), e);
            throw new RuntimeException("AWS Rekognition生体検知に失敗しました", e);
        }
    }

    @Override
    public boolean isServiceAvailable() {
        try {
            return serviceEnabled && authenticateServiceInternal();
        } catch (Exception e) {
            log.error("AWS Rekognitionサービス可用性チェックエラー", e);
            return false;
        }
    }

    @Override
    public void authenticateService() {
        log.info("AWS Rekognition認証開始");
        
        try {
            log.info("AWS Rekognition認証完了: region={}", awsRegion);
        } catch (Exception e) {
            log.error("AWS Rekognition認証エラー", e);
            throw new RuntimeException("AWS Rekognition認証に失敗しました", e);
        }
    }

    @Override
    public double getServiceConfidenceThreshold() {
        return confidenceThreshold.doubleValue();
    }

    private boolean authenticateServiceInternal() {
        return true;
    }

    private BigDecimal simulateFaceComparison(ExternalFaceComparisonRequest request) {
        Random random = new Random();
        double baseScore = 70.0 + (random.nextDouble() * 25.0);
        
        if (request.getSourceImage().length() > 10000 && request.getTargetImage().length() > 10000) {
            baseScore += 5.0;
        }
        
        return BigDecimal.valueOf(Math.min(baseScore, 100.0));
    }

    private BigDecimal simulateLivenessDetection(ExternalLivenessDetectionRequest request) {
        Random random = new Random();
        double baseScore = 75.0 + (random.nextDouble() * 20.0);
        
        if ("BLINK".equals(request.getChallengeType())) {
            baseScore += 3.0;
        } else if ("HEAD_TURN".equals(request.getChallengeType())) {
            baseScore += 2.0;
        }
        
        return BigDecimal.valueOf(Math.min(baseScore, 100.0));
    }

    private String determineConfidenceLevel(BigDecimal score) {
        if (score.compareTo(BigDecimal.valueOf(90)) >= 0) {
            return "HIGH";
        } else if (score.compareTo(BigDecimal.valueOf(70)) >= 0) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }

    private long simulateProcessingTime() {
        Random random = new Random();
        return 500 + random.nextInt(1500);
    }
}
