package com.ahamo.shipping.controller;

import com.ahamo.shipping.dto.ShippingRequest;
import com.ahamo.shipping.dto.ShippingResponse;
import com.ahamo.shipping.dto.TrackingResponse;
import com.ahamo.shipping.service.ShippingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/shipping")
@RequiredArgsConstructor
@Slf4j
public class ShippingController {
    
    private final ShippingService shippingService;
    
    @PostMapping("/order")
    public ResponseEntity<ShippingResponse> createShippingOrder(@Valid @RequestBody ShippingRequest request) {
        log.info("Creating shipping order for contract: {}", request.getContractId());
        
        try {
            ShippingResponse response = shippingService.createShippingOrder(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Failed to create shipping order: {}", e.getMessage());
            throw new RuntimeException("配送注文の作成に失敗しました: " + e.getMessage());
        }
    }
    
    @GetMapping("/track")
    public ResponseEntity<TrackingResponse> trackShipment(@RequestParam String trackingNumber) {
        log.info("Tracking shipment: {}", trackingNumber);
        
        try {
            TrackingResponse response = shippingService.trackShipment(trackingNumber);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to track shipment: {}", e.getMessage());
            throw new RuntimeException("配送追跡に失敗しました: " + e.getMessage());
        }
    }
    
    @GetMapping("/track/order/{orderNumber}")
    public ResponseEntity<TrackingResponse> trackShipmentByOrderNumber(@PathVariable String orderNumber) {
        log.info("Tracking shipment by order number: {}", orderNumber);
        
        try {
            TrackingResponse response = shippingService.trackShipmentByOrderNumber(orderNumber);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to track shipment by order number: {}", e.getMessage());
            throw new RuntimeException("配送追跡に失敗しました: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/order/{orderNumber}")
    public ResponseEntity<Void> cancelShipment(@PathVariable String orderNumber) {
        log.info("Cancelling shipment: {}", orderNumber);
        
        try {
            boolean cancelled = shippingService.cancelShipment(orderNumber);
            if (cancelled) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Failed to cancel shipment: {}", e.getMessage());
            throw new RuntimeException("配送キャンセルに失敗しました: " + e.getMessage());
        }
    }
}
