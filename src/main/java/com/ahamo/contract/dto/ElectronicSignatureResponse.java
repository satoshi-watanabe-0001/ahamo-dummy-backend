package com.ahamo.contract.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElectronicSignatureResponse {

    private String signatureId;
    private String signatureUrl;
    private String status;
    private LocalDateTime signedAt;
    private Map<String, Object> certificateInfo;
    private LocalDateTime expiresAt;
}
