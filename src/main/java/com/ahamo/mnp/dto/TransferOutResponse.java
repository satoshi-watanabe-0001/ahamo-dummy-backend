package com.ahamo.mnp.dto;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class TransferOutResponse {
    
    private String transferId;
    private String phoneNumber;
    private String reservationNumber;
    private String status;
    private LocalDate expirationDate;
    private LocalDateTime createdAt;
    private String message;
}
