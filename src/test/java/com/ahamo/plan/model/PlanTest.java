package com.ahamo.plan.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PlanTest {

    private Plan plan;

    @BeforeEach
    void setUp() {
        plan = new Plan();
    }

    @Test
    void testPlanCreation() {
        plan.setId("plan_test_001");
        plan.setName("テストプラン");
        plan.setDescription("テスト用のプラン");
        plan.setMonthlyFee(new BigDecimal("2980.00"));
        plan.setDataCapacity("20GB");
        plan.setVoiceCalls("無制限");
        plan.setVersion("1.0.0");
        plan.setIsCurrentVersion(true);

        assertEquals("plan_test_001", plan.getId());
        assertEquals("テストプラン", plan.getName());
        assertEquals("テスト用のプラン", plan.getDescription());
        assertEquals(new BigDecimal("2980.00"), plan.getMonthlyFee());
        assertEquals("20GB", plan.getDataCapacity());
        assertEquals("無制限", plan.getVoiceCalls());
        assertEquals("1.0.0", plan.getVersion());
        assertTrue(plan.getIsCurrentVersion());
    }

    @Test
    void testVersionManagementFields() {
        plan.setVersion("1.2.0");
        plan.setParentPlanId("plan_basic_001");
        plan.setIsCurrentVersion(false);

        assertEquals("1.2.0", plan.getVersion());
        assertEquals("plan_basic_001", plan.getParentPlanId());
        assertFalse(plan.getIsCurrentVersion());
    }

    @Test
    void testEffectivePeriodFields() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusYears(1);

        plan.setEffectiveStartDate(startDate);
        plan.setEffectiveEndDate(endDate);

        assertEquals(startDate, plan.getEffectiveStartDate());
        assertEquals(endDate, plan.getEffectiveEndDate());
    }

    @Test
    void testCampaignPeriodFields() {
        LocalDateTime campaignStart = LocalDateTime.now();
        LocalDateTime campaignEnd = LocalDateTime.now().plusMonths(3);

        plan.setCampaignStartDate(campaignStart);
        plan.setCampaignEndDate(campaignEnd);

        assertEquals(campaignStart, plan.getCampaignStartDate());
        assertEquals(campaignEnd, plan.getCampaignEndDate());
    }

    @Test
    void testMetadataFields() {
        LocalDateTime now = LocalDateTime.now();
        
        plan.setCreatedAt(now);
        plan.setUpdatedAt(now);
        plan.setCreatedBy("admin@example.com");
        plan.setUpdatedBy("admin@example.com");
        plan.setChangeReason("新規プラン作成");
        plan.setApprovalStatus("APPROVED");

        assertEquals(now, plan.getCreatedAt());
        assertEquals(now, plan.getUpdatedAt());
        assertEquals("admin@example.com", plan.getCreatedBy());
        assertEquals("admin@example.com", plan.getUpdatedBy());
        assertEquals("新規プラン作成", plan.getChangeReason());
        assertEquals("APPROVED", plan.getApprovalStatus());
    }

    @Test
    void testLombokGeneratedMethods() {
        Plan plan1 = new Plan();
        plan1.setId("plan_001");
        plan1.setName("プラン1");

        Plan plan2 = new Plan();
        plan2.setId("plan_001");
        plan2.setName("プラン1");

        assertEquals(plan1, plan2);
        assertEquals(plan1.hashCode(), plan2.hashCode());
        assertNotNull(plan1.toString());
        assertTrue(plan1.toString().contains("plan_001"));
        assertTrue(plan1.toString().contains("プラン1"));
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Plan planWithAllArgs = new Plan(
            "plan_002",
            "フルプラン",
            "全引数コンストラクタテスト",
            new BigDecimal("3980.00"),
            "30GB",
            "無制限",
            "2.0.0",
            "plan_001",
            true,
            now,
            now.plusYears(1),
            now,
            now.plusMonths(6),
            now,
            now,
            "admin@example.com",
            "admin@example.com",
            "新バージョン作成",
            "APPROVED"
        );

        assertEquals("plan_002", planWithAllArgs.getId());
        assertEquals("フルプラン", planWithAllArgs.getName());
        assertEquals("全引数コンストラクタテスト", planWithAllArgs.getDescription());
        assertEquals(new BigDecimal("3980.00"), planWithAllArgs.getMonthlyFee());
        assertEquals("30GB", planWithAllArgs.getDataCapacity());
        assertEquals("無制限", planWithAllArgs.getVoiceCalls());
        assertEquals("2.0.0", planWithAllArgs.getVersion());
        assertEquals("plan_001", planWithAllArgs.getParentPlanId());
        assertTrue(planWithAllArgs.getIsCurrentVersion());
    }
}
