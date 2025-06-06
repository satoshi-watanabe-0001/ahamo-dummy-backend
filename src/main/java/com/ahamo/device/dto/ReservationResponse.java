package com.ahamo.device.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {
    
    private Long reservationId;
    private String deviceId;
    private String color;
    private String storage;
    private Integer quantity;
    private String status;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private Long customerId;
}
