package com.ahamo.shipping.controller;

import com.ahamo.shipping.service.ConvenienceStoreService;
import com.ahamo.shipping.model.ConvenienceStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shipping/convenience-stores")
@CrossOrigin(origins = "*")
public class ConvenienceStoreController {

    @Autowired
    private ConvenienceStoreService convenienceStoreService;

    @GetMapping("/search")
    public ResponseEntity<List<ConvenienceStore>> searchStores(
            @RequestParam(required = false) String prefecture,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String postalCode,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(defaultValue = "5.0") Double radiusKm) {
        
        List<ConvenienceStore> stores = convenienceStoreService.searchStores(
            prefecture, city, postalCode, latitude, longitude, radiusKm);
        return ResponseEntity.ok(stores);
    }

    @GetMapping("/{storeCode}")
    public ResponseEntity<ConvenienceStore> getStoreByCode(@PathVariable String storeCode) {
        ConvenienceStore store = convenienceStoreService.findByStoreCode(storeCode);
        if (store != null) {
            return ResponseEntity.ok(store);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<ConvenienceStore>> getAllStores() {
        List<ConvenienceStore> stores = convenienceStoreService.findAll();
        return ResponseEntity.ok(stores);
    }
}
