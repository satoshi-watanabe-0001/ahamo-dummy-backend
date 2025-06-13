package com.ahamo.ekyc.controller;

import com.ahamo.ekyc.dto.FacialDetectionRequest;
import com.ahamo.ekyc.dto.FacialDetectionResponse;
import com.ahamo.ekyc.dto.FaceComparisonRequest;
import com.ahamo.ekyc.dto.FaceComparisonResponse;
import com.ahamo.ekyc.dto.QualityCheckRequest;
import com.ahamo.ekyc.dto.QualityCheckResponse;
import com.ahamo.ekyc.service.FaceRecognitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/v1/ekyc/session")
@RequiredArgsConstructor
@Validated
@Slf4j
public class FaceRecognitionController {

    private final FaceRecognitionService faceRecognitionService;

    @PostMapping("/{sessionId}/facial-detection")
    public ResponseEntity<FacialDetectionResponse> detectFacialFeatures(
            @PathVariable @NotBlank String sessionId,
            @Valid @RequestBody FacialDetectionRequest request) {
        
        log.info("顔認識・生体検知リクエスト受信: sessionId={}, detectionType={}", 
                sessionId, request.getDetectionType());
        
        try {
            if (!faceRecognitionService.validateSessionSecurity(sessionId)) {
                log.warn("セッション検証失敗: sessionId={}", sessionId);
                return ResponseEntity.badRequest().build();
            }
            
            FacialDetectionResponse response = faceRecognitionService.detectFacialFeatures(sessionId, request);
            
            log.info("顔認識・生体検知完了: sessionId={}, isLive={}, confidenceScore={}", 
                    sessionId, response.getIsLive(), response.getConfidenceScore());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("顔認識・生体検知エラー: sessionId={}", sessionId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{sessionId}/face-comparison")
    public ResponseEntity<FaceComparisonResponse> compareFaces(
            @PathVariable @NotBlank String sessionId,
            @Valid @RequestBody FaceComparisonRequest request) {
        
        log.info("顔照合リクエスト受信: sessionId={}, similarityThreshold={}", 
                sessionId, request.getSimilarityThreshold());
        
        try {
            if (!faceRecognitionService.validateSessionSecurity(sessionId)) {
                log.warn("セッション検証失敗: sessionId={}", sessionId);
                return ResponseEntity.badRequest().build();
            }
            
            FaceComparisonResponse response = faceRecognitionService.compareFaces(sessionId, request);
            
            log.info("顔照合完了: sessionId={}, isMatch={}, similarityScore={}, verificationStatus={}", 
                    sessionId, response.getIsMatch(), response.getSimilarityScore(), response.getVerificationStatus());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("顔照合エラー: sessionId={}", sessionId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{sessionId}/quality-check")
    public ResponseEntity<QualityCheckResponse> checkImageQuality(
            @PathVariable @NotBlank String sessionId,
            @Valid @RequestBody QualityCheckRequest request) {
        
        log.info("画像品質チェックリクエスト受信: sessionId={}, checkType={}", 
                sessionId, request.getCheckType());
        
        try {
            if (!faceRecognitionService.validateSessionSecurity(sessionId)) {
                log.warn("セッション検証失敗: sessionId={}", sessionId);
                return ResponseEntity.badRequest().build();
            }
            
            QualityCheckResponse response = faceRecognitionService.checkImageQuality(sessionId, request);
            
            log.info("画像品質チェック完了: sessionId={}, overallScore={}, isAcceptable={}", 
                    sessionId, response.getOverallScore(), response.getIsAcceptable());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("画像品質チェックエラー: sessionId={}", sessionId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{sessionId}/cleanup")
    public ResponseEntity<Void> cleanupFacialData(
            @PathVariable @NotBlank String sessionId) {
        
        log.info("顔認識データクリーンアップリクエスト受信: sessionId={}", sessionId);
        
        try {
            faceRecognitionService.cleanupFacialData(sessionId);
            log.info("顔認識データクリーンアップ完了: sessionId={}", sessionId);
            return ResponseEntity.noContent().build();
            
        } catch (Exception e) {
            log.error("顔認識データクリーンアップエラー: sessionId={}", sessionId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
