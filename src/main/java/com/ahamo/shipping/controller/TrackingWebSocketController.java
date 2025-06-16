package com.ahamo.shipping.controller;

import com.ahamo.shipping.dto.TrackingResponse;
import com.ahamo.shipping.service.ShippingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TrackingWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ShippingService shippingService;

    @MessageMapping("/tracking/{trackingNumber}")
    public void subscribeToTracking(@DestinationVariable String trackingNumber) {
        log.info("WebSocket subscription for tracking number: {}", trackingNumber);
        
        try {
            TrackingResponse trackingData = shippingService.trackShipment(trackingNumber);
            messagingTemplate.convertAndSend("/topic/tracking/" + trackingNumber, trackingData);
        } catch (Exception e) {
            log.error("Error sending tracking data for {}: {}", trackingNumber, e.getMessage());
        }
    }

    public void sendTrackingUpdate(String trackingNumber, TrackingResponse trackingData) {
        log.info("Sending tracking update for: {}", trackingNumber);
        messagingTemplate.convertAndSend("/topic/tracking/" + trackingNumber, trackingData);
    }
}
