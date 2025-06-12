package com.ahamo.payment.dto;

import com.ahamo.payment.model.BillingSchedule;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingScheduleDto {
    private String scheduleId;
    private String customerId;
    private String contractId;
    private BigDecimal amount;
    private BillingSchedule.BillingFrequency frequency;
    private LocalDateTime nextBillingDate;
    private BillingSchedule.BillingStatus status;
    private String paymentMethodId;
}
