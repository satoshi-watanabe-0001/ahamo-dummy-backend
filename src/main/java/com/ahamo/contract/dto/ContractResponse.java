package com.ahamo.contract.dto;

import com.ahamo.contract.model.Contract;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractResponse {
    private String id;
    private String status;
    private String message;
    
    public static ContractResponse fromContract(Contract contract) {
        return ContractResponse.builder()
            .id(contract.getContractUuid())
            .status(contract.getStatus().toString())
            .build();
    }
}
