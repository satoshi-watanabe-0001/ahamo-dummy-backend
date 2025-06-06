package com.ahamo.plan.service;

import com.ahamo.plan.dto.AdminPlanRequest;
import com.ahamo.plan.dto.AdminPlanResponse;
import com.ahamo.plan.dto.PlanVersionHistory;
import com.ahamo.plan.dto.PlanResponse;
import com.ahamo.plan.dto.PlansResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PlanService {
    
    AdminPlanResponse createPlan(AdminPlanRequest request);
    
    AdminPlanResponse updatePlan(String planId, AdminPlanRequest request);
    
    void deactivatePlan(String planId);
    
    List<PlanVersionHistory> getPlanVersionHistory(String planId);
    
    PlansResponse getActivePlans();
    
    PlanResponse getPlanById(String planId);
}
