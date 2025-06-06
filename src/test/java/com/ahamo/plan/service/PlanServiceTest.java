package com.ahamo.plan.service;

import com.ahamo.plan.dto.AdminPlanRequest;
import com.ahamo.plan.dto.AdminPlanResponse;
import com.ahamo.plan.dto.PlanVersionHistory;
import com.ahamo.plan.model.Plan;
import com.ahamo.plan.repository.PlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class PlanServiceTest {

    @Mock
    private PlanRepository planRepository;

    @InjectMocks
    private PlanServiceImpl planService;

    private AdminPlanRequest adminPlanRequest;
    private Plan testPlan;
    private AdminPlanResponse adminPlanResponse;

    @BeforeEach
    void setUp() {
        adminPlanRequest = new AdminPlanRequest();
        adminPlanRequest.setName("テストプラン");
        adminPlanRequest.setDescription("テスト用のプラン");
        adminPlanRequest.setMonthlyFee(new BigDecimal("2980.00"));
        adminPlanRequest.setDataCapacity("20GB");
        adminPlanRequest.setVoiceCalls("無制限");
        adminPlanRequest.setEffectiveStartDate(LocalDateTime.now());
        adminPlanRequest.setEffectiveEndDate(LocalDateTime.now().plusYears(1));
        adminPlanRequest.setCampaignStartDate(LocalDateTime.now());
        adminPlanRequest.setCampaignEndDate(LocalDateTime.now().plusMonths(3));
        adminPlanRequest.setChangeReason("新規プラン作成");

        testPlan = new Plan();
        testPlan.setId("plan_test_001");
        testPlan.setName("テストプラン");
        testPlan.setDescription("テスト用のプラン");
        testPlan.setMonthlyFee(new BigDecimal("2980.00"));
        testPlan.setDataCapacity("20GB");
        testPlan.setVoiceCalls("無制限");
        testPlan.setVersion("1.0.0");
        testPlan.setIsCurrentVersion(true);
        testPlan.setCreatedAt(LocalDateTime.now());
        testPlan.setUpdatedAt(LocalDateTime.now());
        testPlan.setCreatedBy("admin@example.com");
        testPlan.setUpdatedBy("admin@example.com");
        testPlan.setChangeReason("新規プラン作成");
        testPlan.setApprovalStatus("APPROVED");

        adminPlanResponse = new AdminPlanResponse();
        adminPlanResponse.setId("plan_test_001");
        adminPlanResponse.setName("テストプラン");
        adminPlanResponse.setDescription("テスト用のプラン");
        adminPlanResponse.setMonthlyFee(new BigDecimal("2980.00"));
        adminPlanResponse.setDataCapacity("20GB");
        adminPlanResponse.setVoiceCalls("無制限");
        adminPlanResponse.setVersion("1.0.0");
        adminPlanResponse.setIsCurrentVersion(true);
        adminPlanResponse.setCreatedBy("admin@example.com");
        adminPlanResponse.setUpdatedBy("admin@example.com");
        adminPlanResponse.setChangeReason("新規プラン作成");
        adminPlanResponse.setApprovalStatus("APPROVED");
        adminPlanResponse.setCreatedAt(LocalDateTime.now());
        adminPlanResponse.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void createPlan_ValidRequest_ReturnsCreatedPlan() {
        when(planRepository.save(any(Plan.class))).thenReturn(testPlan);

        AdminPlanResponse result = planService.createPlan(adminPlanRequest);

        assertNotNull(result);
        assertEquals("plan_test_001", result.getId());
        assertEquals("テストプラン", result.getName());
        assertEquals("1.0.0", result.getVersion());
        assertTrue(result.getIsCurrentVersion());
        verify(planRepository).save(any(Plan.class));
    }

    @Test
    void updatePlan_ExistingPlan_ReturnsUpdatedPlan() {
        when(planRepository.findById("plan_test_001")).thenReturn(Optional.of(testPlan));
        
        Plan updatedPlan = new Plan();
        updatedPlan.setId("plan_test_001");
        updatedPlan.setName("更新されたプラン");
        updatedPlan.setVersion("1.1.0");
        updatedPlan.setIsCurrentVersion(true);
        
        when(planRepository.save(any(Plan.class))).thenReturn(updatedPlan);

        adminPlanRequest.setName("更新されたプラン");
        adminPlanRequest.setChangeReason("プラン名変更");

        AdminPlanResponse result = planService.updatePlan("plan_test_001", adminPlanRequest);

        assertNotNull(result);
        assertEquals("plan_test_001", result.getId());
        assertEquals("更新されたプラン", result.getName());
        assertEquals("1.1.0", result.getVersion());
        verify(planRepository).findById("plan_test_001");
        verify(planRepository, times(2)).save(any(Plan.class));
    }

    @Test
    void updatePlan_NonExistentPlan_ThrowsException() {
        when(planRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            planService.updatePlan("nonexistent", adminPlanRequest);
        });

        verify(planRepository).findById("nonexistent");
        verify(planRepository, never()).save(any(Plan.class));
    }

    @Test
    void deactivatePlan_ExistingPlan_DeactivatesSuccessfully() {
        when(planRepository.findById("plan_test_001")).thenReturn(Optional.of(testPlan));

        planService.deactivatePlan("plan_test_001");

        verify(planRepository).findById("plan_test_001");
        verify(planRepository).save(testPlan);
        assertFalse(testPlan.getIsCurrentVersion());
    }

    @Test
    void deactivatePlan_NonExistentPlan_ThrowsException() {
        when(planRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            planService.deactivatePlan("nonexistent");
        });

        verify(planRepository).findById("nonexistent");
        verify(planRepository, never()).save(any(Plan.class));
    }

    @Test
    void getPlanVersionHistory_ExistingPlan_ReturnsVersionHistory() {
        Plan version1 = new Plan();
        version1.setVersion("1.0.0");
        version1.setChangeReason("新規プラン作成");
        version1.setCreatedBy("admin@example.com");
        version1.setCreatedAt(LocalDateTime.now().minusDays(10));
        version1.setIsCurrentVersion(false);

        Plan version2 = new Plan();
        version2.setVersion("1.1.0");
        version2.setChangeReason("プラン更新");
        version2.setCreatedBy("admin@example.com");
        version2.setCreatedAt(LocalDateTime.now());
        version2.setIsCurrentVersion(true);

        List<Plan> planVersions = Arrays.asList(version2, version1);
        when(planRepository.findByParentPlanIdOrIdOrderByCreatedAtDesc(anyString(), anyString()))
                .thenReturn(planVersions);

        List<PlanVersionHistory> result = planService.getPlanVersionHistory("plan_test_001");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("1.1.0", result.get(0).getVersion());
        assertEquals("1.0.0", result.get(1).getVersion());
        assertTrue(result.get(0).getIsCurrentVersion());
        assertFalse(result.get(1).getIsCurrentVersion());
        verify(planRepository).findByParentPlanIdOrIdOrderByCreatedAtDesc("plan_test_001", "plan_test_001");
    }

    @Test
    void getPlanVersionHistory_NonExistentPlan_ReturnsEmptyList() {
        when(planRepository.findByParentPlanIdOrIdOrderByCreatedAtDesc(anyString(), anyString()))
                .thenReturn(Arrays.asList());

        List<PlanVersionHistory> result = planService.getPlanVersionHistory("nonexistent");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(planRepository).findByParentPlanIdOrIdOrderByCreatedAtDesc("nonexistent", "nonexistent");
    }
}
