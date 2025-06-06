package com.ahamo.device.service;

import com.ahamo.device.dto.ReservationRequest;
import com.ahamo.device.dto.ReservationResponse;
import com.ahamo.device.model.Reservation;

import java.util.List;

public interface ReservationService {
    
    ReservationResponse createReservation(ReservationRequest request);
    
    ReservationResponse allocateReservation(Long reservationId);
    
    void cancelReservation(Long reservationId);
    
    List<ReservationResponse> getCustomerReservations(Long customerId);
    
    void processExpiredReservations();
    
    ReservationResponse getReservation(Long reservationId);
}
