package com.ahamo.payment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankSearchRequestDto {
    private String query;
    private String bankCode;
    private String searchType;
}
