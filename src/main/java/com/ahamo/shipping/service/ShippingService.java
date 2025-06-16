package com.ahamo.shipping.service;

import com.ahamo.shipping.dto.ShippingRequest;
import com.ahamo.shipping.dto.ShippingResponse;
import com.ahamo.shipping.dto.TrackingResponse;

import java.util.Map;

public interface ShippingService {
    
    /**
     * 配送注文を作成
     */
    ShippingResponse createShippingOrder(ShippingRequest request);
    
    /**
     * 契約完了時の自動配送手配
     */
    ShippingResponse arrangeShippingForContract(Long contractId);
    
    /**
     * 配送状況を追跡
     */
    TrackingResponse trackShipment(String trackingNumber);
    
    /**
     * 注文番号で配送状況を追跡
     */
    TrackingResponse trackShipmentByOrderNumber(String orderNumber);
    
    /**
     * 配送をキャンセル
     */
    boolean cancelShipment(String orderNumber);
    
    /**
     * 配達日時を変更
     */
    boolean changeDeliveryTime(String orderNumber, Map<String, Object> request);
    
    /**
     * 再配達を依頼
     */
    boolean requestRedelivery(String orderNumber, Map<String, Object> request);
    
    /**
     * 配達を確認
     */
    boolean confirmDelivery(String orderNumber, Map<String, Object> request);
}
