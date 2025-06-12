package com.ahamo.mnp.controller;

import com.ahamo.mnp.dto.MnpEligibilityRequest;
import com.ahamo.mnp.dto.MnpEligibilityResponse;
import com.ahamo.mnp.dto.MnpRequest;
import com.ahamo.mnp.dto.MnpResponse;
import com.ahamo.mnp.dto.ReservationValidationRequest;
import com.ahamo.mnp.dto.ReservationValidationResponse;
import com.ahamo.mnp.service.MnpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class MnpController {
    
    private final MnpService mnpService;
    
    @PostMapping("/mnp/eligibility")
    public ResponseEntity<MnpEligibilityResponse> checkEligibility(
            @Valid @RequestBody MnpEligibilityRequest request) {
        MnpEligibilityResponse response = mnpService.checkEligibility(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/mnp/request")
    public ResponseEntity<MnpResponse> submitMnpRequest(
            @Valid @RequestBody MnpRequest request) {
        MnpResponse response = mnpService.submitMnpRequest(request);
        return ResponseEntity.status(201).body(response);
    }
    
    @GetMapping("/mnp/status/{mnpId}")
    public ResponseEntity<MnpResponse> getMnpStatus(@PathVariable String mnpId) {
        MnpResponse response = mnpService.getMnpStatus(mnpId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/mnp/validate-reservation")
    public ResponseEntity<ReservationValidationResponse> validateReservationNumber(
            @Valid @RequestBody ReservationValidationRequest request) {
        ReservationValidationResponse response = mnpService.validateReservationNumber(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/mnp/detect-carrier")
    public ResponseEntity<String> detectCarrier(@RequestParam String reservationNumber) {
        String carrier = mnpService.detectCarrierFromReservationNumber(reservationNumber);
        return ResponseEntity.ok(carrier);
    }
}
