package com.ahamo.plan.controller;

import com.ahamo.plan.dto.FeeCalculationRequest;
import com.ahamo.plan.dto.FeeCalculationResult;
import com.ahamo.plan.service.FeeCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FeeController {
    
    private final FeeCalculationService feeCalculationService;
    
    @PostMapping("/calculate-fee")
    public ResponseEntity<FeeCalculationResult> calculateFee(
            @Valid @RequestBody FeeCalculationRequest request) {
        FeeCalculationResult result = feeCalculationService.calculateFee(request);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/compare-fee-plans")
    public ResponseEntity<Map<String, Object>> compareFeePlans(
            @RequestBody Map<String, Object> request) {
        Map<String, Object> usage = (Map<String, Object>) request.get("usage");
        List<String> planIds = (List<String>) request.get("planIds");
        
        List<FeeCalculationResult> results = feeCalculationService.compareFeePlans(usage, planIds);
        
        return ResponseEntity.ok(Map.of("results", results));
    }
}
