package com.ahamo.contract.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractTemplateUpdateRequest {

    private String name;
    private String description;
    private String content;
    private List<ContractTemplateCreateRequest.TemplateVariable> variables;
    private String changeReason;
}
