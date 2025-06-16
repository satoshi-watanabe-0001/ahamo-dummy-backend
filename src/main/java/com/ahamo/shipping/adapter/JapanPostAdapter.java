package com.ahamo.shipping.adapter;

import com.ahamo.shipping.dto.ShippingRequest;
import com.ahamo.shipping.dto.ShippingResponse;
import com.ahamo.shipping.dto.TrackingResponse;
import com.ahamo.shipping.dto.LocationUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class JapanPostAdapter implements LogisticsProviderAdapter {

    @Value("${logistics.japanpost.api.url:https://api.post.japanpost.jp/v1}")
    private String apiUrl;

    @Value("${logistics.japanpost.api.key:}")
    private String apiKey;

    @Override
    public ShippingResponse createShipment(ShippingRequest request) {
        log.info("Creating Japan Post shipment for order: {}", request.getOrderNumber());
        
        String trackingNumber = "JP" + System.currentTimeMillis();
        
        return ShippingResponse.builder()
                .trackingNumber(trackingNumber)
                .status("SHIPPED")
                .estimatedDeliveryDate(LocalDateTime.now().plusDays(3).toLocalDate())
                .providerCode(getProviderCode())
                .message("配送依頼が正常に作成されました")
                .build();
    }

    @Override
    public TrackingResponse trackShipment(String trackingNumber) {
        log.info("Tracking Japan Post shipment: {}", trackingNumber);
        
        return TrackingResponse.builder()
                .trackingNumber(trackingNumber)
                .status("IN_TRANSIT")
                .location("東京中央郵便局")
                .lastUpdated(LocalDateTime.now())
                .description("配送中です")
                .build();
    }

    @Override
    public boolean cancelShipment(String trackingNumber) {
        log.info("Cancelling Japan Post shipment: {}", trackingNumber);
        
        return true;
    }

    @Override
    public LocationUpdate getLocationUpdate(String trackingNumber) {
        log.info("Getting location update for Japan Post shipment: {}", trackingNumber);
        
        return LocationUpdate.builder()
                .latitude(35.6785 + Math.random() * 0.01)
                .longitude(139.6823 + Math.random() * 0.01)
                .currentLocation("東京都千代田区")
                .estimatedArrivalTime(LocalDateTime.now().plusHours(3))
                .status("配送中")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    public String getProviderCode() {
        return "JAPAN_POST";
    }
}
