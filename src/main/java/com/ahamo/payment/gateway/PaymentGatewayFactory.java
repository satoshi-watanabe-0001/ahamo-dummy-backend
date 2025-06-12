package com.ahamo.payment.gateway;

import com.ahamo.payment.gateway.adapter.MockPaymentGatewayAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentGatewayFactory {
    
    private final MockPaymentGatewayAdapter mockAdapter;
    
    private final Map<String, PaymentGatewayAdapter> adapters = new HashMap<>();
    
    public PaymentGatewayAdapter getAdapter(String gatewayType) {
        if (adapters.isEmpty()) {
            initializeAdapters();
        }
        
        PaymentGatewayAdapter adapter = adapters.get(gatewayType.toLowerCase());
        if (adapter == null) {
            adapter = mockAdapter;
        }
        
        return adapter;
    }
    
    private void initializeAdapters() {
        adapters.put("mock", mockAdapter);
        adapters.put("default", mockAdapter);
    }
    
    public Map<String, PaymentGatewayAdapter> getAllAdapters() {
        if (adapters.isEmpty()) {
            initializeAdapters();
        }
        return new HashMap<>(adapters);
    }
}
