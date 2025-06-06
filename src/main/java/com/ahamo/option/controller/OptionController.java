package com.ahamo.option.controller;

import com.ahamo.option.dto.OptionResponse;
import com.ahamo.option.service.OptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/options")
@RequiredArgsConstructor
public class OptionController {
    
    private final OptionService optionService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getOptions(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String device_compatible) {
        
        List<OptionResponse> options;
        
        if (category != null) {
            options = optionService.getOptionsByCategory(category);
        } else {
            options = optionService.getActiveOptions();
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("options", options);
        response.put("total", options.size());
        
        return ResponseEntity.ok(response);
    }
}
