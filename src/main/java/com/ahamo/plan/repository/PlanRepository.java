package com.ahamo.plan.repository;

import com.ahamo.plan.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, String> {
    
    List<Plan> findByParentPlanIdOrIdOrderByCreatedAtDesc(String parentPlanId, String id);
    
    List<Plan> findByIsCurrentVersionTrue();
}
