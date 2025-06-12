package com.ahamo.mnp.dto;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class TransferInResponse {
    
    private String transferId;
    private String phoneNumber;
    private String status;
    private LocalDate estimatedCompletionDate;
    private LocalDateTime createdAt;
    private String message;
}
