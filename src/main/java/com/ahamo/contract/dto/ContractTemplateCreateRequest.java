package com.ahamo.contract.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractTemplateCreateRequest {

    @NotBlank
    private String name;

    private String description;

    @NotBlank
    private String format;

    @NotBlank
    private String content;

    private List<TemplateVariable> variables;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TemplateVariable {
        private String name;
        private String type;
        private Boolean required;
        private String description;
    }
}
