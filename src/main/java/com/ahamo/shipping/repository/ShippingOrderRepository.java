package com.ahamo.shipping.repository;

import com.ahamo.shipping.model.ShippingOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShippingOrderRepository extends JpaRepository<ShippingOrder, Long> {
    
    Optional<ShippingOrder> findByOrderNumber(String orderNumber);
    
    Optional<ShippingOrder> findByTrackingNumber(String trackingNumber);
    
    List<ShippingOrder> findByContractId(Long contractId);
    
    List<ShippingOrder> findByStatus(ShippingOrder.ShippingStatus status);
    
    @Query("SELECT s FROM ShippingOrder s WHERE s.contractId = :contractId AND s.status = :status")
    List<ShippingOrder> findByContractIdAndStatus(@Param("contractId") Long contractId, 
                                                  @Param("status") ShippingOrder.ShippingStatus status);
}
