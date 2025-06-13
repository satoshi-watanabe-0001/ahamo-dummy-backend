package com.ahamo.shipping.adapter;

import com.ahamo.shipping.dto.ShippingRequest;
import com.ahamo.shipping.dto.ShippingResponse;
import com.ahamo.shipping.dto.TrackingResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class SagawaAdapter implements LogisticsProviderAdapter {

    @Value("${logistics.sagawa.api.url:https://api.sagawa-exp.co.jp/v1}")
    private String apiUrl;

    @Value("${logistics.sagawa.api.key:}")
    private String apiKey;

    @Override
    public ShippingResponse createShipment(ShippingRequest request) {
        log.info("Creating Sagawa shipment for order: {}", request.getOrderNumber());
        
        String trackingNumber = "SGW" + System.currentTimeMillis();
        
        return ShippingResponse.builder()
                .trackingNumber(trackingNumber)
                .status("SHIPPED")
                .estimatedDeliveryDate(LocalDateTime.now().plusDays(1).toLocalDate())
                .providerCode(getProviderCode())
                .message("配送依頼が正常に作成されました")
                .build();
    }

    @Override
    public TrackingResponse trackShipment(String trackingNumber) {
        log.info("Tracking Sagawa shipment: {}", trackingNumber);
        
        return TrackingResponse.builder()
                .trackingNumber(trackingNumber)
                .status("OUT_FOR_DELIVERY")
                .location("大阪府配送センター")
                .lastUpdated(LocalDateTime.now())
                .description("配達中です")
                .build();
    }

    @Override
    public boolean cancelShipment(String trackingNumber) {
        log.info("Cancelling Sagawa shipment: {}", trackingNumber);
        
        return true;
    }

    @Override
    public String getProviderCode() {
        return "SAGAWA";
    }
}
