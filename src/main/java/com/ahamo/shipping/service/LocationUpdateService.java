package com.ahamo.shipping.service;

import com.ahamo.shipping.controller.TrackingWebSocketController;
import com.ahamo.shipping.dto.LocationUpdate;
import com.ahamo.shipping.dto.TrackingResponse;
import com.ahamo.shipping.model.ShippingOrder;
import com.ahamo.shipping.model.TrackingEvent;
import com.ahamo.shipping.repository.ShippingOrderRepository;
import com.ahamo.shipping.repository.TrackingEventRepository;
import com.ahamo.shipping.service.ShippingNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationUpdateService {

    private final ShippingOrderRepository shippingOrderRepository;
    private final TrackingEventRepository trackingEventRepository;
    private final LogisticsProviderFactory providerFactory;
    private final TrackingWebSocketController webSocketController;
    private final ShippingService shippingService;
    private final ShippingNotificationService notificationService;

    @Scheduled(fixedRate = 300000)
    public void updateLocationData() {
        log.info("Starting scheduled location update");
        
        List<ShippingOrder> activeOrders = shippingOrderRepository.findByStatusIn(
            List.of(ShippingOrder.ShippingStatus.SHIPPED, 
                   ShippingOrder.ShippingStatus.IN_TRANSIT, 
                   ShippingOrder.ShippingStatus.OUT_FOR_DELIVERY)
        );

        for (ShippingOrder order : activeOrders) {
            try {
                updateOrderLocation(order);
            } catch (Exception e) {
                log.error("Failed to update location for order {}: {}", order.getOrderNumber(), e.getMessage());
            }
        }
        
        log.info("Completed location update for {} orders", activeOrders.size());
    }

    private void updateOrderLocation(ShippingOrder order) {
        if (order.getTrackingNumber() == null) {
            return;
        }

        LocationUpdate locationUpdate = providerFactory
            .getAdapter(order.getProvider().getProviderCode())
            .getLocationUpdate(order.getTrackingNumber());

        TrackingEvent trackingEvent = TrackingEvent.builder()
            .shippingOrderId(order.getId())
            .status(locationUpdate.getStatus())
            .location(locationUpdate.getCurrentLocation())
            .latitude(locationUpdate.getLatitude())
            .longitude(locationUpdate.getLongitude())
            .estimatedArrivalTime(locationUpdate.getEstimatedArrivalTime())
            .timestamp(locationUpdate.getTimestamp())
            .description("位置情報自動更新")
            .build();

        trackingEventRepository.save(trackingEvent);

        TrackingResponse trackingResponse = shippingService.trackShipment(order.getTrackingNumber());
        webSocketController.sendTrackingUpdate(order.getTrackingNumber(), trackingResponse);
        
        if (shouldSendNotification(order, locationUpdate)) {
            notificationService.sendShippingStatusNotification(order, locationUpdate.getStatus());
        }
        
        log.info("Updated location for tracking number: {}", order.getTrackingNumber());
    }

    private boolean shouldSendNotification(ShippingOrder order, LocationUpdate locationUpdate) {
        return "OUT_FOR_DELIVERY".equals(locationUpdate.getStatus()) || 
               "DELIVERED".equals(locationUpdate.getStatus()) ||
               locationUpdate.getEstimatedArrivalTime() != null;
    }
}
