package com.ahamo.shipping.controller;

import com.ahamo.shipping.service.DeliveryTimeService;
import com.ahamo.shipping.model.DeliveryTimeSlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shipping/delivery-time-slots")
@CrossOrigin(origins = "*")
public class DeliveryTimeController {

    @Autowired
    private DeliveryTimeService deliveryTimeService;

    @GetMapping
    public ResponseEntity<List<DeliveryTimeSlot>> getAllTimeSlots() {
        List<DeliveryTimeSlot> timeSlots = deliveryTimeService.getAllActiveTimeSlots();
        return ResponseEntity.ok(timeSlots);
    }

    @GetMapping("/by-type/{slotType}")
    public ResponseEntity<List<DeliveryTimeSlot>> getTimeSlotsByType(@PathVariable String slotType) {
        try {
            DeliveryTimeSlot.SlotType type = DeliveryTimeSlot.SlotType.valueOf(slotType.toUpperCase());
            List<DeliveryTimeSlot> timeSlots = deliveryTimeService.getTimeSlotsByType(type);
            return ResponseEntity.ok(timeSlots);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
