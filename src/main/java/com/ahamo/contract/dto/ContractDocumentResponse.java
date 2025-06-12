package com.ahamo.contract.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractDocumentResponse {

    private String documentId;
    private String contractId;
    private String documentUrl;
    private String format;
    private Integer fileSize;
    private String signatureStatus;
    private LocalDateTime createdAt;
    private LocalDateTime signedAt;
    private LocalDateTime expiresAt;
}
