package com.ahamo.shipping.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackingResponse {

    private String trackingNumber;

    private String status;

    private String location;

    private LocalDateTime lastUpdated;

    private String description;

    private List<TrackingEvent> events;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TrackingEvent {
        private String status;
        private String location;
        private LocalDateTime timestamp;
        private String description;
    }
}
