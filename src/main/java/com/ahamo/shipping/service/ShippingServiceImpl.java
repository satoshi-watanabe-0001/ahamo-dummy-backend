package com.ahamo.shipping.service;

import com.ahamo.shipping.adapter.LogisticsProviderAdapter;
import com.ahamo.shipping.dto.ShippingRequest;
import com.ahamo.shipping.dto.ShippingResponse;
import com.ahamo.shipping.dto.TrackingResponse;
import com.ahamo.shipping.model.LogisticsProvider;
import com.ahamo.shipping.model.ShippingOrder;
import com.ahamo.shipping.model.TrackingEvent;
import com.ahamo.shipping.repository.LogisticsProviderRepository;
import com.ahamo.shipping.repository.ShippingOrderRepository;
import com.ahamo.shipping.repository.TrackingEventRepository;
import com.ahamo.contract.model.Contract;
import com.ahamo.contract.repository.ContractRepository;
import com.ahamo.device.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ShippingServiceImpl implements ShippingService {
    
    private final ShippingOrderRepository shippingOrderRepository;
    private final LogisticsProviderRepository logisticsProviderRepository;
    private final TrackingEventRepository trackingEventRepository;
    private final ContractRepository contractRepository;
    private final ReservationService reservationService;
    private final LogisticsProviderFactory providerFactory;
    
    @Override
    public ShippingResponse createShippingOrder(ShippingRequest request) {
        log.info("Creating shipping order for contract: {}", request.getContractId());
        
        String providerCode = request.getProviderCode() != null ? request.getProviderCode() : "YAMATO";
        LogisticsProvider provider = logisticsProviderRepository.findByProviderCode(providerCode)
                .orElseThrow(() -> new RuntimeException("物流プロバイダーが見つかりません: " + providerCode));
        
        ShippingOrder shippingOrder = ShippingOrder.builder()
                .orderNumber(request.getOrderNumber())
                .contractId(request.getContractId())
                .providerId(provider.getId())
                .shippingAddressId(request.getShippingAddressId())
                .deviceId(request.getDeviceId())
                .deliveryTimeWindow(request.getDeliveryTimeWindow())
                .deliveryOptions(request.getDeliveryOptions())
                .status(ShippingOrder.ShippingStatus.PENDING)
                .build();
        
        ShippingOrder savedOrder = shippingOrderRepository.save(shippingOrder);
        
        LogisticsProviderAdapter adapter = providerFactory.getAdapter(providerCode);
        ShippingResponse providerResponse = adapter.createShipment(request);
        
        savedOrder.setTrackingNumber(providerResponse.getTrackingNumber());
        savedOrder.setEstimatedDeliveryDate(providerResponse.getEstimatedDeliveryDate());
        savedOrder.setStatus(ShippingOrder.ShippingStatus.SHIPPED);
        shippingOrderRepository.save(savedOrder);
        
        TrackingEvent event = TrackingEvent.builder()
                .shippingOrderId(savedOrder.getId())
                .status("SHIPPED")
                .timestamp(LocalDateTime.now())
                .description("配送依頼が作成されました")
                .build();
        trackingEventRepository.save(event);
        
        log.info("Shipping order created successfully: {}", savedOrder.getOrderNumber());
        
        return ShippingResponse.builder()
                .orderId(savedOrder.getId())
                .orderNumber(savedOrder.getOrderNumber())
                .trackingNumber(savedOrder.getTrackingNumber())
                .status(savedOrder.getStatus().name())
                .estimatedDeliveryDate(savedOrder.getEstimatedDeliveryDate())
                .providerCode(provider.getProviderCode())
                .providerName(provider.getProviderName())
                .message("配送注文が正常に作成されました")
                .createdAt(savedOrder.getCreatedAt())
                .build();
    }
    
    @Override
    public ShippingResponse arrangeShippingForContract(Long contractId) {
        log.info("Arranging shipping for contract: {}", contractId);
        
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("契約が見つかりません: " + contractId));
        
        List<ShippingOrder> existingOrders = shippingOrderRepository.findByContractId(contractId);
        if (!existingOrders.isEmpty()) {
            log.warn("Shipping already arranged for contract: {}", contractId);
            ShippingOrder existingOrder = existingOrders.get(0);
            LogisticsProvider provider = logisticsProviderRepository.findById(existingOrder.getProviderId())
                    .orElse(null);
            
            return ShippingResponse.builder()
                    .orderId(existingOrder.getId())
                    .orderNumber(existingOrder.getOrderNumber())
                    .trackingNumber(existingOrder.getTrackingNumber())
                    .status(existingOrder.getStatus().name())
                    .estimatedDeliveryDate(existingOrder.getEstimatedDeliveryDate())
                    .providerCode(provider != null ? provider.getProviderCode() : "UNKNOWN")
                    .providerName(provider != null ? provider.getProviderName() : "不明")
                    .message("配送は既に手配済みです")
                    .createdAt(existingOrder.getCreatedAt())
                    .build();
        }
        
        Long shippingAddressId = 1L; // 仮の値
        
        String orderNumber = "SO" + System.currentTimeMillis();
        ShippingRequest request = ShippingRequest.builder()
                .orderNumber(orderNumber)
                .contractId(contractId)
                .shippingAddressId(shippingAddressId)
                .deviceId(contract.getDeviceId())
                .deviceColor(contract.getDeviceColor())
                .deviceStorage(contract.getDeviceStorage())
                .providerCode("YAMATO") // デフォルト
                .build();
        
        return createShippingOrder(request);
    }
    
    @Override
    @Transactional(readOnly = true)
    public TrackingResponse trackShipment(String trackingNumber) {
        log.info("Tracking shipment: {}", trackingNumber);
        
        ShippingOrder order = shippingOrderRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new RuntimeException("配送注文が見つかりません: " + trackingNumber));
        
        LogisticsProvider provider = logisticsProviderRepository.findById(order.getProviderId())
                .orElseThrow(() -> new RuntimeException("物流プロバイダーが見つかりません"));
        
        LogisticsProviderAdapter adapter = providerFactory.getAdapter(provider.getProviderCode());
        TrackingResponse providerResponse = adapter.trackShipment(trackingNumber);
        
        List<TrackingEvent> events = trackingEventRepository.findByShippingOrderIdOrderByTimestampDesc(order.getId());
        List<TrackingResponse.TrackingEvent> trackingEvents = events.stream()
                .map(event -> TrackingResponse.TrackingEvent.builder()
                        .status(event.getStatus())
                        .location(event.getLocation())
                        .timestamp(event.getTimestamp())
                        .description(event.getDescription())
                        .build())
                .collect(Collectors.toList());
        
        providerResponse.setEvents(trackingEvents);
        return providerResponse;
    }
    
    @Override
    @Transactional(readOnly = true)
    public TrackingResponse trackShipmentByOrderNumber(String orderNumber) {
        log.info("Tracking shipment by order number: {}", orderNumber);
        
        ShippingOrder order = shippingOrderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("配送注文が見つかりません: " + orderNumber));
        
        if (order.getTrackingNumber() == null) {
            throw new RuntimeException("追跡番号が設定されていません");
        }
        
        return trackShipment(order.getTrackingNumber());
    }
    
    @Override
    public boolean cancelShipment(String orderNumber) {
        log.info("Cancelling shipment: {}", orderNumber);
        
        ShippingOrder order = shippingOrderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("配送注文が見つかりません: " + orderNumber));
        
        if (order.getStatus() == ShippingOrder.ShippingStatus.DELIVERED) {
            throw new RuntimeException("配達済みの注文はキャンセルできません");
        }
        
        LogisticsProvider provider = logisticsProviderRepository.findById(order.getProviderId())
                .orElseThrow(() -> new RuntimeException("物流プロバイダーが見つかりません"));
        
        LogisticsProviderAdapter adapter = providerFactory.getAdapter(provider.getProviderCode());
        boolean cancelled = adapter.cancelShipment(order.getTrackingNumber());
        
        if (cancelled) {
            order.setStatus(ShippingOrder.ShippingStatus.CANCELLED);
            shippingOrderRepository.save(order);
            
            TrackingEvent event = TrackingEvent.builder()
                    .shippingOrderId(order.getId())
                    .status("CANCELLED")
                    .timestamp(LocalDateTime.now())
                    .description("配送がキャンセルされました")
                    .build();
            trackingEventRepository.save(event);
            
            log.info("Shipment cancelled successfully: {}", orderNumber);
        }
        
        return cancelled;
    }

    @Override
    public boolean changeDeliveryTime(String orderNumber, Map<String, Object> request) {
        Optional<ShippingOrder> orderOpt = shippingOrderRepository.findByOrderNumber(orderNumber);
        if (orderOpt.isEmpty()) {
            return false;
        }
        
        ShippingOrder order = orderOpt.get();
        
        if (request.containsKey("deliveryDate")) {
            String deliveryDate = (String) request.get("deliveryDate");
            order.setEstimatedDeliveryDate(java.time.LocalDate.parse(deliveryDate));
        }
        
        if (request.containsKey("timeWindow")) {
            String timeWindow = (String) request.get("timeWindow");
            order.setDeliveryTimeWindow(timeWindow);
        }
        
        shippingOrderRepository.save(order);
        return true;
    }

    @Override
    public boolean requestRedelivery(String orderNumber, Map<String, Object> request) {
        Optional<ShippingOrder> orderOpt = shippingOrderRepository.findByOrderNumber(orderNumber);
        if (orderOpt.isEmpty()) {
            return false;
        }
        
        ShippingOrder order = orderOpt.get();
        order.setStatus(ShippingOrder.ShippingStatus.PENDING);
        
        if (request.containsKey("deliveryDate")) {
            String deliveryDate = (String) request.get("deliveryDate");
            order.setEstimatedDeliveryDate(java.time.LocalDate.parse(deliveryDate));
        }
        
        if (request.containsKey("timeWindow")) {
            String timeWindow = (String) request.get("timeWindow");
            order.setDeliveryTimeWindow(timeWindow);
        }
        
        shippingOrderRepository.save(order);
        
        TrackingEvent event = TrackingEvent.builder()
            .shippingOrderId(order.getId())
            .status("REDELIVERY_REQUESTED")
            .location("再配達依頼")
            .timestamp(LocalDateTime.now())
            .description("再配達が依頼されました")
            .build();
        
        trackingEventRepository.save(event);
        
        return true;
    }

    @Override
    public boolean confirmDelivery(String orderNumber, Map<String, Object> request) {
        Optional<ShippingOrder> orderOpt = shippingOrderRepository.findByOrderNumber(orderNumber);
        if (orderOpt.isEmpty()) {
            return false;
        }
        
        ShippingOrder order = orderOpt.get();
        order.setStatus(ShippingOrder.ShippingStatus.DELIVERED);
        order.setDeliveryConfirmedAt(LocalDateTime.now());
        
        if (request.containsKey("rating")) {
            Integer rating = (Integer) request.get("rating");
            order.setDeliveryRating(rating);
        }
        
        if (request.containsKey("feedback")) {
            String feedback = (String) request.get("feedback");
            order.setDeliveryFeedback(feedback);
        }
        
        shippingOrderRepository.save(order);
        
        TrackingEvent event = TrackingEvent.builder()
            .shippingOrderId(order.getId())
            .status("DELIVERED")
            .location("配達完了")
            .timestamp(LocalDateTime.now())
            .description("配達が確認されました")
            .build();
        
        trackingEventRepository.save(event);
        
        return true;
    }
}
