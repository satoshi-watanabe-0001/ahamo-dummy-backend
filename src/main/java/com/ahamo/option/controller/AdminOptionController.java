package com.ahamo.option.controller;

import com.ahamo.option.dto.CompatibilityResponse;
import com.ahamo.option.dto.OptionRequest;
import com.ahamo.option.dto.OptionResponse;
import com.ahamo.option.service.OptionRuleService;
import com.ahamo.option.service.OptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/options")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminOptionController {
    
    private final OptionService optionService;
    private final OptionRuleService optionRuleService;
    
    @PostMapping
    public ResponseEntity<OptionResponse> createOption(@Valid @RequestBody OptionRequest request) {
        OptionResponse response = optionService.createOption(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{optionId}")
    public ResponseEntity<OptionResponse> updateOption(
            @PathVariable String optionId,
            @Valid @RequestBody OptionRequest request) {
        OptionResponse response = optionService.updateOption(optionId, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{optionId}")
    public ResponseEntity<Map<String, String>> deleteOption(@PathVariable String optionId) {
        optionService.deleteOption(optionId);
        return ResponseEntity.ok(Map.of("message", "Option deleted successfully"));
    }
    
    @GetMapping
    public ResponseEntity<List<OptionResponse>> getAllOptions() {
        List<OptionResponse> options = optionService.getAllOptions();
        return ResponseEntity.ok(options);
    }
    
    @GetMapping("/{optionId}/compatibility")
    public ResponseEntity<CompatibilityResponse> checkCompatibility(
            @PathVariable String optionId,
            @RequestParam List<String> selectedOptions) {
        CompatibilityResponse response = optionRuleService.checkCompatibility(optionId, selectedOptions);
        return ResponseEntity.ok(response);
    }
}
