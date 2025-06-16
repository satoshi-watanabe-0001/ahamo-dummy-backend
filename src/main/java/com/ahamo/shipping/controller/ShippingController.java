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
import java.util.HashMap;
import java.util.Map;

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

    @PutMapping("/delivery-time/{orderNumber}")
    public ResponseEntity<Map<String, Object>> changeDeliveryTime(
            @PathVariable String orderNumber,
            @RequestBody Map<String, Object> request) {
        
        try {
            boolean updated = shippingService.changeDeliveryTime(orderNumber, request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", updated);
            response.put("message", updated ? "配達日時が変更されました" : "配達日時の変更に失敗しました");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "配達日時の変更に失敗しました: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/redelivery/{orderNumber}")
    public ResponseEntity<Map<String, Object>> requestRedelivery(
            @PathVariable String orderNumber,
            @RequestBody Map<String, Object> request) {
        
        try {
            boolean requested = shippingService.requestRedelivery(orderNumber, request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", requested);
            response.put("message", requested ? "再配達を依頼しました" : "再配達の依頼に失敗しました");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "再配達の依頼に失敗しました: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/confirm-delivery/{orderNumber}")
    public ResponseEntity<Map<String, Object>> confirmDelivery(
            @PathVariable String orderNumber,
            @RequestBody Map<String, Object> request) {
        
        try {
            boolean confirmed = shippingService.confirmDelivery(orderNumber, request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", confirmed);
            response.put("message", confirmed ? "配達が確認されました" : "配達確認に失敗しました");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "配達確認に失敗しました: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
}
