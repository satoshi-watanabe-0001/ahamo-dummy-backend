package com.ahamo.contract.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.validation.constraints.NotBlank;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractGenerationRequest {

    @NotBlank
    private String templateId;

    private Map<String, Object> variables;

    private String format;
}
