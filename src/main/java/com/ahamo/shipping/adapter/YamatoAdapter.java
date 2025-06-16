package com.ahamo.shipping.adapter;

import com.ahamo.shipping.dto.ShippingRequest;
import com.ahamo.shipping.dto.ShippingResponse;
import com.ahamo.shipping.dto.TrackingResponse;
import com.ahamo.shipping.dto.LocationUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Slf4j
public class YamatoAdapter implements LogisticsProviderAdapter {

    @Value("${logistics.yamato.api.url:https://api.yamato.co.jp/v1}")
    private String apiUrl;

    @Value("${logistics.yamato.api.key:}")
    private String apiKey;

    @Override
    public ShippingResponse createShipment(ShippingRequest request) {
        log.info("Creating Yamato shipment for order: {}", request.getOrderNumber());
        
        String trackingNumber = "YMT" + System.currentTimeMillis();
        
        return ShippingResponse.builder()
                .trackingNumber(trackingNumber)
                .status("SHIPPED")
                .estimatedDeliveryDate(LocalDateTime.now().plusDays(2).toLocalDate())
                .providerCode(getProviderCode())
                .message("配送依頼が正常に作成されました")
                .build();
    }

    @Override
    public TrackingResponse trackShipment(String trackingNumber) {
        log.info("Tracking Yamato shipment: {}", trackingNumber);
        
        return TrackingResponse.builder()
                .trackingNumber(trackingNumber)
                .status("IN_TRANSIT")
                .location("東京都配送センター")
                .lastUpdated(LocalDateTime.now())
                .description("配送中です")
                .build();
    }

    @Override
    public boolean cancelShipment(String trackingNumber) {
        log.info("Cancelling Yamato shipment: {}", trackingNumber);
        
        return true;
    }

    @Override
    public LocationUpdate getLocationUpdate(String trackingNumber) {
        log.info("Getting location update for Yamato shipment: {}", trackingNumber);
        
        return LocationUpdate.builder()
                .latitude(35.6762 + Math.random() * 0.01)
                .longitude(139.6503 + Math.random() * 0.01)
                .currentLocation("東京都渋谷区")
                .estimatedArrivalTime(LocalDateTime.now().plusHours(2))
                .status("配送中")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    public String getProviderCode() {
        return "YAMATO";
    }
}
