package com.ahamo.plan.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AdminPlanRequestTest {

    private Validator validator;
    private AdminPlanRequest adminPlanRequest;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        adminPlanRequest = new AdminPlanRequest();
        adminPlanRequest.setName("テストプラン");
        adminPlanRequest.setDescription("テスト用のプラン");
        adminPlanRequest.setMonthlyFee(new BigDecimal("2980.00"));
        adminPlanRequest.setDataCapacity("20GB");
        adminPlanRequest.setVoiceCalls("無制限");
        adminPlanRequest.setChangeReason("新規プラン作成");
    }

    @Test
    void testValidRequest() {
        Set<ConstraintViolation<AdminPlanRequest>> violations = validator.validate(adminPlanRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testBlankName_ShouldFailValidation() {
        adminPlanRequest.setName("");
        
        Set<ConstraintViolation<AdminPlanRequest>> violations = validator.validate(adminPlanRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Plan name is required")));
    }

    @Test
    void testNullName_ShouldFailValidation() {
        adminPlanRequest.setName(null);
        
        Set<ConstraintViolation<AdminPlanRequest>> violations = validator.validate(adminPlanRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Plan name is required")));
    }

    @Test
    void testNullMonthlyFee_ShouldFailValidation() {
        adminPlanRequest.setMonthlyFee(null);
        
        Set<ConstraintViolation<AdminPlanRequest>> violations = validator.validate(adminPlanRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Monthly fee is required")));
    }

    @Test
    void testNegativeMonthlyFee_ShouldFailValidation() {
        adminPlanRequest.setMonthlyFee(new BigDecimal("-100.00"));
        
        Set<ConstraintViolation<AdminPlanRequest>> violations = validator.validate(adminPlanRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Monthly fee must be positive")));
    }

    @Test
    void testBlankDataCapacity_ShouldFailValidation() {
        adminPlanRequest.setDataCapacity("");
        
        Set<ConstraintViolation<AdminPlanRequest>> violations = validator.validate(adminPlanRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Data capacity is required")));
    }

    @Test
    void testBlankVoiceCalls_ShouldFailValidation() {
        adminPlanRequest.setVoiceCalls("");
        
        Set<ConstraintViolation<AdminPlanRequest>> violations = validator.validate(adminPlanRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Voice calls is required")));
    }

    @Test
    void testBlankChangeReason_ShouldFailValidation() {
        adminPlanRequest.setChangeReason("");
        
        Set<ConstraintViolation<AdminPlanRequest>> violations = validator.validate(adminPlanRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Change reason is required")));
    }

    @Test
    void testEffectivePeriodFields() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusYears(1);
        
        adminPlanRequest.setEffectiveStartDate(startDate);
        adminPlanRequest.setEffectiveEndDate(endDate);
        
        assertEquals(startDate, adminPlanRequest.getEffectiveStartDate());
        assertEquals(endDate, adminPlanRequest.getEffectiveEndDate());
        
        Set<ConstraintViolation<AdminPlanRequest>> violations = validator.validate(adminPlanRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testCampaignPeriodFields() {
        LocalDateTime campaignStart = LocalDateTime.now();
        LocalDateTime campaignEnd = LocalDateTime.now().plusMonths(3);
        
        adminPlanRequest.setCampaignStartDate(campaignStart);
        adminPlanRequest.setCampaignEndDate(campaignEnd);
        
        assertEquals(campaignStart, adminPlanRequest.getCampaignStartDate());
        assertEquals(campaignEnd, adminPlanRequest.getCampaignEndDate());
        
        Set<ConstraintViolation<AdminPlanRequest>> violations = validator.validate(adminPlanRequest);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testLombokGeneratedMethods() {
        AdminPlanRequest request1 = new AdminPlanRequest();
        request1.setName("プラン1");
        request1.setMonthlyFee(new BigDecimal("1000.00"));
        
        AdminPlanRequest request2 = new AdminPlanRequest();
        request2.setName("プラン1");
        request2.setMonthlyFee(new BigDecimal("1000.00"));
        
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotNull(request1.toString());
        assertTrue(request1.toString().contains("プラン1"));
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        AdminPlanRequest request = new AdminPlanRequest(
            "フルプラン",
            "全引数コンストラクタテスト",
            new BigDecimal("3980.00"),
            "30GB",
            "無制限",
            "SMS無制限",
            java.util.Arrays.asList("5G対応", "テザリング無料"),
            now,
            now.plusYears(1),
            now,
            now.plusMonths(6),
            "新バージョン作成"
        );
        
        assertEquals("フルプラン", request.getName());
        assertEquals("全引数コンストラクタテスト", request.getDescription());
        assertEquals(new BigDecimal("3980.00"), request.getMonthlyFee());
        assertEquals("30GB", request.getDataCapacity());
        assertEquals("無制限", request.getVoiceCalls());
        assertEquals("新バージョン作成", request.getChangeReason());
        
        Set<ConstraintViolation<AdminPlanRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
}
