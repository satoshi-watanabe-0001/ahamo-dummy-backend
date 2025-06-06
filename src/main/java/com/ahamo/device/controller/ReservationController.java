package com.ahamo.device.controller;

import com.ahamo.device.dto.ReservationRequest;
import com.ahamo.device.dto.ReservationResponse;
import com.ahamo.device.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class ReservationController {
    
    private final ReservationService reservationService;
    
    @PostMapping("/reserve")
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest request) {
        ReservationResponse reservation = reservationService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }
    
    @PutMapping("/{reservationId}/allocate")
    public ResponseEntity<ReservationResponse> allocateReservation(@PathVariable Long reservationId) {
        ReservationResponse reservation = reservationService.allocateReservation(reservationId);
        return ResponseEntity.ok(reservation);
    }
    
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long reservationId) {
        reservationService.cancelReservation(reservationId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> getCustomerReservations(@RequestParam Long customerId) {
        List<ReservationResponse> reservations = reservationService.getCustomerReservations(customerId);
        return ResponseEntity.ok(reservations);
    }
    
    @GetMapping("/reservations/{reservationId}")
    public ResponseEntity<ReservationResponse> getReservation(@PathVariable Long reservationId) {
        ReservationResponse reservation = reservationService.getReservation(reservationId);
        return ResponseEntity.ok(reservation);
    }
}
