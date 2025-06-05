package com.ahamo.plan.controller;

import com.ahamo.plan.dto.AdminPlanRequest;
import com.ahamo.plan.dto.AdminPlanResponse;
import com.ahamo.plan.dto.PlanVersionHistory;
import com.ahamo.plan.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/plans")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminPlanController {
    
    private final PlanService planService;
    
    @PostMapping
    public ResponseEntity<AdminPlanResponse> createPlan(@Valid @RequestBody AdminPlanRequest request) {
        AdminPlanResponse response = planService.createPlan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{planId}")
    public ResponseEntity<AdminPlanResponse> updatePlan(
            @PathVariable String planId,
            @Valid @RequestBody AdminPlanRequest request) {
        AdminPlanResponse response = planService.updatePlan(planId, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{planId}")
    public ResponseEntity<Map<String, String>> deactivatePlan(@PathVariable String planId) {
        planService.deactivatePlan(planId);
        return ResponseEntity.ok(Map.of("message", "Plan deactivated successfully"));
    }
    
    @GetMapping("/{planId}/versions")
    public ResponseEntity<List<PlanVersionHistory>> getPlanVersions(@PathVariable String planId) {
        List<PlanVersionHistory> versions = planService.getPlanVersionHistory(planId);
        return ResponseEntity.ok(versions);
    }
}
