package com.ahamo.plan.service;

import com.ahamo.plan.dto.AdminPlanRequest;
import com.ahamo.plan.dto.AdminPlanResponse;
import com.ahamo.plan.dto.PlanVersionHistory;
import com.ahamo.plan.model.Plan;
import com.ahamo.plan.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {
    
    private final PlanRepository planRepository;
    
    @Override
    public AdminPlanResponse createPlan(AdminPlanRequest request) {
        Plan plan = new Plan();
        plan.setId("plan_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8));
        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setMonthlyFee(request.getMonthlyFee());
        plan.setDataCapacity(request.getDataCapacity());
        plan.setVoiceCalls(request.getVoiceCalls());
        plan.setVersion("1.0.0");
        plan.setIsCurrentVersion(true);
        plan.setEffectiveStartDate(request.getEffectiveStartDate());
        plan.setEffectiveEndDate(request.getEffectiveEndDate());
        plan.setCampaignStartDate(request.getCampaignStartDate());
        plan.setCampaignEndDate(request.getCampaignEndDate());
        plan.setCreatedAt(LocalDateTime.now());
        plan.setUpdatedAt(LocalDateTime.now());
        plan.setCreatedBy("admin@example.com");
        plan.setUpdatedBy("admin@example.com");
        plan.setChangeReason(request.getChangeReason());
        plan.setApprovalStatus("APPROVED");
        
        Plan savedPlan = planRepository.save(plan);
        return convertToAdminResponse(savedPlan);
    }
    
    @Override
    public AdminPlanResponse updatePlan(String planId, AdminPlanRequest request) {
        Plan existingPlan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found: " + planId));
        
        existingPlan.setIsCurrentVersion(false);
        planRepository.save(existingPlan);
        
        Plan newVersion = new Plan();
        newVersion.setId(planId + "_v" + System.currentTimeMillis());
        newVersion.setName(request.getName());
        newVersion.setDescription(request.getDescription());
        newVersion.setMonthlyFee(request.getMonthlyFee());
        newVersion.setDataCapacity(request.getDataCapacity());
        newVersion.setVoiceCalls(request.getVoiceCalls());
        newVersion.setVersion(incrementVersion(existingPlan.getVersion()));
        newVersion.setParentPlanId(planId);
        newVersion.setIsCurrentVersion(true);
        newVersion.setEffectiveStartDate(request.getEffectiveStartDate());
        newVersion.setEffectiveEndDate(request.getEffectiveEndDate());
        newVersion.setCampaignStartDate(request.getCampaignStartDate());
        newVersion.setCampaignEndDate(request.getCampaignEndDate());
        newVersion.setCreatedAt(LocalDateTime.now());
        newVersion.setUpdatedAt(LocalDateTime.now());
        newVersion.setCreatedBy("admin@example.com");
        newVersion.setUpdatedBy("admin@example.com");
        newVersion.setChangeReason(request.getChangeReason());
        newVersion.setApprovalStatus("APPROVED");
        
        Plan savedPlan = planRepository.save(newVersion);
        return convertToAdminResponse(savedPlan);
    }
    
    @Override
    public void deactivatePlan(String planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found: " + planId));
        
        plan.setIsCurrentVersion(false);
        plan.setUpdatedAt(LocalDateTime.now());
        plan.setUpdatedBy("admin@example.com");
        planRepository.save(plan);
    }
    
    @Override
    public List<PlanVersionHistory> getPlanVersionHistory(String planId) {
        List<Plan> versions = planRepository.findByParentPlanIdOrIdOrderByCreatedAtDesc(planId, planId);
        
        return versions.stream()
                .map(this::convertToVersionHistory)
                .collect(Collectors.toList());
    }
    
    private AdminPlanResponse convertToAdminResponse(Plan plan) {
        AdminPlanResponse response = new AdminPlanResponse();
        response.setId(plan.getId());
        response.setName(plan.getName());
        response.setDescription(plan.getDescription());
        response.setMonthlyFee(plan.getMonthlyFee());
        response.setDataCapacity(plan.getDataCapacity());
        response.setVoiceCalls(plan.getVoiceCalls());
        response.setVersion(plan.getVersion());
        response.setParentPlanId(plan.getParentPlanId());
        response.setIsCurrentVersion(plan.getIsCurrentVersion());
        response.setEffectiveStartDate(plan.getEffectiveStartDate());
        response.setEffectiveEndDate(plan.getEffectiveEndDate());
        response.setCampaignStartDate(plan.getCampaignStartDate());
        response.setCampaignEndDate(plan.getCampaignEndDate());
        response.setCreatedBy(plan.getCreatedBy());
        response.setUpdatedBy(plan.getUpdatedBy());
        response.setChangeReason(plan.getChangeReason());
        response.setApprovalStatus(plan.getApprovalStatus());
        response.setCreatedAt(plan.getCreatedAt());
        response.setUpdatedAt(plan.getUpdatedAt());
        return response;
    }
    
    private PlanVersionHistory convertToVersionHistory(Plan plan) {
        PlanVersionHistory history = new PlanVersionHistory();
        history.setVersion(plan.getVersion());
        history.setChangeReason(plan.getChangeReason());
        history.setCreatedBy(plan.getCreatedBy());
        history.setCreatedAt(plan.getCreatedAt());
        history.setIsCurrentVersion(plan.getIsCurrentVersion());
        return history;
    }
    
    private String incrementVersion(String currentVersion) {
        String[] parts = currentVersion.split("\\.");
        if (parts.length >= 2) {
            int minor = Integer.parseInt(parts[1]) + 1;
            return parts[0] + "." + minor + ".0";
        }
        return "1.1.0";
    }
}
