package com.ahamo.contract.dto;

import com.ahamo.contract.model.Contract;
import com.ahamo.plan.model.Plan;
import com.ahamo.option.model.Option;
import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ContractDetailsResponse {
    private String id;
    private String status;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private LocalDateTime createdAt;
    private Plan plan;
    private List<Option> optionDetails;
    private Double totalMonthlyFee;
    private ContractPeriod contractPeriod;

    @Data
    @Builder
    public static class ContractPeriod {
        private LocalDateTime startDate;
        private LocalDateTime endDate;
    }

    public static ContractDetailsResponse fromContract(Contract contract) {
        return ContractDetailsResponse.builder()
            .id(contract.getContractUuid())
            .status(contract.getStatus().toString())
            .customerName(contract.getCustomerFirstName() + " " + contract.getCustomerLastName())
            .customerEmail(contract.getCustomerEmail())
            .customerPhone(contract.getCustomerPhone())
            .createdAt(contract.getCreatedAt())
            .contractPeriod(ContractPeriod.builder()
                .startDate(contract.getCreatedAt())
                .endDate(null)
                .build())
            .build();
    }
}
