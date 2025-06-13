package com.ahamo.shipping.service;

import com.ahamo.shipping.model.ShippingOption;
import com.ahamo.shipping.repository.ShippingOptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShippingOptionService {

    @Autowired
    private ShippingOptionRepository shippingOptionRepository;

    public List<ShippingOption> getAllActiveOptions() {
        return shippingOptionRepository.findByIsActiveTrueOrderByOptionName();
    }

    public Optional<ShippingOption> findByOptionCode(String optionCode) {
        return shippingOptionRepository.findByOptionCode(optionCode);
    }

    public List<ShippingOption> findAll() {
        return shippingOptionRepository.findAll();
    }
}
