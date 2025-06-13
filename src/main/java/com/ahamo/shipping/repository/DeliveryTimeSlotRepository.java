package com.ahamo.shipping.repository;

import com.ahamo.shipping.model.DeliveryTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryTimeSlotRepository extends JpaRepository<DeliveryTimeSlot, Long> {

    List<DeliveryTimeSlot> findByIsActiveTrueOrderByStartTime();

    List<DeliveryTimeSlot> findBySlotTypeAndIsActiveTrueOrderByStartTime(DeliveryTimeSlot.SlotType slotType);
}
