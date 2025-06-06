package com.ahamo.plan.controller;

import com.ahamo.plan.dto.PlanResponse;
import com.ahamo.plan.dto.PlansResponse;
import com.ahamo.plan.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/plans")
@RequiredArgsConstructor
public class PlanController {
    
    private final PlanService planService;
    
    @GetMapping
    public ResponseEntity<PlansResponse> getPlans(
            @RequestParam(defaultValue = "true") boolean activeOnly) {
        PlansResponse response = planService.getActivePlans();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{planId}")
    public ResponseEntity<PlanResponse> getPlan(@PathVariable String planId) {
        PlanResponse response = planService.getPlanById(planId);
        return ResponseEntity.ok(response);
    }
}
