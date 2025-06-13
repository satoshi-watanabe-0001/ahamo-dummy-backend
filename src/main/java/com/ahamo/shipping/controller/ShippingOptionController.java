package com.ahamo.shipping.controller;

import com.ahamo.shipping.service.ShippingOptionService;
import com.ahamo.shipping.model.ShippingOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/shipping/options")
@CrossOrigin(origins = "*")
public class ShippingOptionController {

    @Autowired
    private ShippingOptionService shippingOptionService;

    @GetMapping
    public ResponseEntity<List<ShippingOption>> getAllOptions() {
        List<ShippingOption> options = shippingOptionService.getAllActiveOptions();
        return ResponseEntity.ok(options);
    }

    @GetMapping("/{optionCode}")
    public ResponseEntity<ShippingOption> getOptionByCode(@PathVariable String optionCode) {
        Optional<ShippingOption> option = shippingOptionService.findByOptionCode(optionCode);
        if (option.isPresent()) {
            return ResponseEntity.ok(option.get());
        }
        return ResponseEntity.notFound().build();
    }
}
