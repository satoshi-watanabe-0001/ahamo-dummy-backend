package com.ahamo.mnp.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ReservationValidationResponse {
    
    private boolean valid;
    private String reservationNumber;
    private String detectedCarrier;
    private LocalDate expirationDate;
    private boolean expired;
    private boolean duplicate;
    private List<String> validationErrors;
    private String message;
}
