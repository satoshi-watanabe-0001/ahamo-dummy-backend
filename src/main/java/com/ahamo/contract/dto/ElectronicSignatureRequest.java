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
public class ElectronicSignatureRequest {

    @NotBlank
    private String signatureMethod;

    private Map<String, Object> signerInfo;
    private String returnUrl;
    private String webhookUrl;
}
