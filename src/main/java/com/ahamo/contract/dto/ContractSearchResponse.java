package com.ahamo.contract.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractSearchResponse {

    private List<ContractSummary> contracts;
    private Pagination pagination;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ContractSummary {
        private String id;
        private String contractNumber;
        private String customerName;
        private String status;
        private LocalDateTime createdAt;
        private LocalDateTime signedAt;
        private BigDecimal totalAmount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Pagination {
        private Integer currentPage;
        private Integer totalPages;
        private Integer totalCount;
        private Integer perPage;
    }
}
