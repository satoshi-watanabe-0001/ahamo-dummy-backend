package com.ahamo.shipping.adapter;

import com.ahamo.shipping.dto.ShippingRequest;
import com.ahamo.shipping.dto.ShippingResponse;
import com.ahamo.shipping.dto.TrackingResponse;
import com.ahamo.shipping.dto.LocationUpdate;

public interface LogisticsProviderAdapter {
    
    /**
     * 配送依頼を作成
     */
    ShippingResponse createShipment(ShippingRequest request);
    
    /**
     * 配送状況を追跡
     */
    TrackingResponse trackShipment(String trackingNumber);
    
    /**
     * 配送をキャンセル
     */
    boolean cancelShipment(String trackingNumber);

    /**
     * リアルタイム位置情報を取得
     */
    LocationUpdate getLocationUpdate(String trackingNumber);
    
    /**
     * プロバイダーコードを取得
     */
    String getProviderCode();
}
