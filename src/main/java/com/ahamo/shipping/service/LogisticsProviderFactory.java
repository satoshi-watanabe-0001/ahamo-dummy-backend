package com.ahamo.shipping.service;

import com.ahamo.shipping.adapter.LogisticsProviderAdapter;
import com.ahamo.shipping.adapter.YamatoAdapter;
import com.ahamo.shipping.adapter.SagawaAdapter;
import com.ahamo.shipping.adapter.JapanPostAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LogisticsProviderFactory {
    
    private final YamatoAdapter yamatoAdapter;
    private final SagawaAdapter sagawaAdapter;
    private final JapanPostAdapter japanPostAdapter;
    
    private final Map<String, LogisticsProviderAdapter> adapters = new HashMap<>();
    
    public LogisticsProviderAdapter getAdapter(String providerCode) {
        if (adapters.isEmpty()) {
            initializeAdapters();
        }
        
        LogisticsProviderAdapter adapter = adapters.get(providerCode.toUpperCase());
        if (adapter == null) {
            adapter = yamatoAdapter;
        }
        
        return adapter;
    }
    
    private void initializeAdapters() {
        adapters.put("YAMATO", yamatoAdapter);
        adapters.put("SAGAWA", sagawaAdapter);
        adapters.put("JAPAN_POST", japanPostAdapter);
    }
    
    public Map<String, LogisticsProviderAdapter> getAllAdapters() {
        if (adapters.isEmpty()) {
            initializeAdapters();
        }
        return new HashMap<>(adapters);
    }
}
