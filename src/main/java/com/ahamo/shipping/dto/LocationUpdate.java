package com.ahamo.shipping.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationUpdate {
    private Double latitude;
    private Double longitude;
    private String currentLocation;
    private LocalDateTime estimatedArrivalTime;
    private String status;
    private LocalDateTime timestamp;
}
