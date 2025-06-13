package com.ahamo.shipping.repository;

import com.ahamo.shipping.model.TrackingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackingEventRepository extends JpaRepository<TrackingEvent, Long> {
    
    List<TrackingEvent> findByShippingOrderIdOrderByTimestampDesc(Long shippingOrderId);
    
    @Query("SELECT t FROM TrackingEvent t WHERE t.shippingOrderId = :shippingOrderId ORDER BY t.timestamp DESC")
    List<TrackingEvent> findTrackingHistoryByOrderId(@Param("shippingOrderId") Long shippingOrderId);
}
