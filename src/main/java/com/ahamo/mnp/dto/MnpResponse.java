package com.ahamo.mnp.dto;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class MnpResponse {
    
    private String mnpId;
    private String reservationNumber;
    private String status;
    private LocalDate estimatedCompletionDate;
    private List<String> nextSteps;
}
