package com.ahamo.shipping.service;

import com.ahamo.shipping.model.DeliveryTimeSlot;
import com.ahamo.shipping.repository.DeliveryTimeSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeliveryTimeService {

    @Autowired
    private DeliveryTimeSlotRepository deliveryTimeSlotRepository;

    public List<DeliveryTimeSlot> getAllActiveTimeSlots() {
        return deliveryTimeSlotRepository.findByIsActiveTrueOrderByStartTime();
    }

    public List<DeliveryTimeSlot> getTimeSlotsByType(DeliveryTimeSlot.SlotType slotType) {
        return deliveryTimeSlotRepository.findBySlotTypeAndIsActiveTrueOrderByStartTime(slotType);
    }

    public Optional<DeliveryTimeSlot> findById(Long id) {
        return deliveryTimeSlotRepository.findById(id);
    }
}
