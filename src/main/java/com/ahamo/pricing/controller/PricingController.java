package com.ahamo.pricing.controller;

import com.ahamo.pricing.dto.PricingCalculationRequest;
import com.ahamo.pricing.dto.PricingCalculationResult;
import com.ahamo.pricing.dto.EstimateRequest;
import com.ahamo.pricing.dto.EstimateResult;
import com.ahamo.pricing.service.PricingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/pricing")
@RequiredArgsConstructor
@Slf4j
public class PricingController {
    
    private final PricingService pricingService;
    
    @PostMapping("/calculate")
    public ResponseEntity<PricingCalculationResult> calculatePricing(@Valid @RequestBody PricingCalculationRequest request) {
        log.info("Received pricing calculation request for plan: {}, device: {}", request.getPlanId(), request.getDeviceId());
        
        try {
            PricingCalculationResult result = pricingService.calculatePricing(request);
            log.info("Pricing calculation completed successfully. Monthly total: {}", result.getMonthlyTotal());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error calculating pricing: {}", e.getMessage(), e);
            throw new RuntimeException("料金計算中にエラーが発生しました: " + e.getMessage());
        }
    }
    
    @PostMapping("/estimate")
    public ResponseEntity<EstimateResult> generateEstimate(@Valid @RequestBody EstimateRequest request) {
        log.info("Received estimate request for plan: {}, device: {}, period: {} months", 
                request.getPlanId(), request.getDeviceId(), request.getEstimatePeriodMonths());
        
        try {
            EstimateResult result = pricingService.generateEstimate(request);
            log.info("Estimate generated successfully. Estimate ID: {}", result.getEstimateId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error generating estimate: {}", e.getMessage(), e);
            throw new RuntimeException("見積生成中にエラーが発生しました: " + e.getMessage());
        }
    }
}
