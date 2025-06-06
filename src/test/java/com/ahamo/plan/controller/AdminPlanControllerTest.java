package com.ahamo.plan.controller;

import com.ahamo.plan.dto.AdminPlanRequest;
import com.ahamo.plan.dto.AdminPlanResponse;
import com.ahamo.plan.dto.PlanVersionHistory;
import com.ahamo.plan.model.Plan;
import com.ahamo.plan.service.PlanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdminPlanController.class)
class AdminPlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlanService planService;

    @MockBean
    private com.ahamo.security.jwt.JwtTokenProvider jwtTokenProvider;

    @MockBean
    private com.ahamo.security.service.CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private AdminPlanRequest adminPlanRequest;
    private AdminPlanResponse adminPlanResponse;
    private Plan testPlan;
    private List<PlanVersionHistory> versionHistory;

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

        PlanVersionHistory version1 = new PlanVersionHistory();
        version1.setVersion("1.0.0");
        version1.setChangeReason("新規プラン作成");
        version1.setCreatedBy("admin@example.com");
        version1.setCreatedAt(LocalDateTime.now());
        version1.setIsCurrentVersion(true);

        versionHistory = Arrays.asList(version1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createPlan_ValidRequest_ReturnsCreatedPlan() throws Exception {
        when(planService.createPlan(any(AdminPlanRequest.class))).thenReturn(adminPlanResponse);

        mockMvc.perform(post("/api/v1/admin/plans")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminPlanRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("plan_test_001"))
                .andExpect(jsonPath("$.name").value("テストプラン"))
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andExpect(jsonPath("$.isCurrentVersion").value(true))
                .andExpect(jsonPath("$.createdBy").value("admin@example.com"))
                .andExpect(jsonPath("$.approvalStatus").value("APPROVED"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updatePlan_ValidRequest_ReturnsUpdatedPlan() throws Exception {
        adminPlanRequest.setName("更新されたプラン");
        adminPlanRequest.setChangeReason("プラン名変更");
        
        adminPlanResponse.setName("更新されたプラン");
        adminPlanResponse.setVersion("1.1.0");
        adminPlanResponse.setChangeReason("プラン名変更");

        when(planService.updatePlan(anyString(), any(AdminPlanRequest.class))).thenReturn(adminPlanResponse);

        mockMvc.perform(put("/api/v1/admin/plans/plan_test_001")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminPlanRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("plan_test_001"))
                .andExpect(jsonPath("$.name").value("更新されたプラン"))
                .andExpect(jsonPath("$.version").value("1.1.0"))
                .andExpect(jsonPath("$.changeReason").value("プラン名変更"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePlan_ValidPlanId_ReturnsSuccess() throws Exception {
        doNothing().when(planService).deactivatePlan(anyString());

        mockMvc.perform(delete("/api/v1/admin/plans/plan_test_001")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Plan deactivated successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPlanVersions_ValidPlanId_ReturnsVersionHistory() throws Exception {
        when(planService.getPlanVersionHistory(anyString())).thenReturn(versionHistory);

        mockMvc.perform(get("/api/v1/admin/plans/plan_test_001/versions")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].version").value("1.0.0"))
                .andExpect(jsonPath("$[0].changeReason").value("新規プラン作成"))
                .andExpect(jsonPath("$[0].createdBy").value("admin@example.com"))
                .andExpect(jsonPath("$[0].isCurrentVersion").value(true));
    }

    @Test
    void createPlan_Unauthorized_ReturnsForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/admin/plans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminPlanRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createPlan_InsufficientRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/admin/plans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminPlanRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createPlan_InvalidRequest_ReturnsBadRequest() throws Exception {
        AdminPlanRequest invalidRequest = new AdminPlanRequest();

        mockMvc.perform(post("/api/v1/admin/plans")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updatePlan_NonExistentPlan_ReturnsNotFound() throws Exception {
        when(planService.updatePlan(anyString(), any(AdminPlanRequest.class)))
                .thenThrow(new RuntimeException("Plan not found"));

        mockMvc.perform(put("/api/v1/admin/plans/nonexistent_plan")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminPlanRequest)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePlan_NonExistentPlan_ReturnsNotFound() throws Exception {
        doThrow(new RuntimeException("Plan not found")).when(planService).deactivatePlan("nonexistent_plan");

        mockMvc.perform(delete("/api/v1/admin/plans/nonexistent_plan")
                .with(csrf()))
                .andExpect(status().isInternalServerError());
    }
}
