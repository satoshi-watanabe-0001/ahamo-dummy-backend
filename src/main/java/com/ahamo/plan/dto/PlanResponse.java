package com.ahamo.plan.dto;

import com.ahamo.plan.model.Plan;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanResponse {
    private String id;
    private String name;
    private String description;
    private BigDecimal monthlyFee;
    private String dataCapacity;
    private String voiceCalls;
    private String sms;
    private List<String> features;
    private Boolean isActive;
    private Boolean isPopular;
    
    public static PlanResponse fromPlan(Plan plan) {
        PlanResponse response = new PlanResponse();
        response.setId(plan.getId());
        response.setName(plan.getName());
        response.setDescription(plan.getDescription());
        response.setMonthlyFee(plan.getMonthlyFee());
        response.setDataCapacity(plan.getDataCapacity());
        response.setVoiceCalls(plan.getVoiceCalls());
        response.setSms(plan.getSms());
        response.setIsActive(plan.getIsCurrentVersion());
        return response;
    }
}
