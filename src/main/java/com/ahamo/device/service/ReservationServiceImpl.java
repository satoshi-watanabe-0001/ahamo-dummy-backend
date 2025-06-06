package com.ahamo.device.service;

import com.ahamo.common.exception.ErrorCode;
import com.ahamo.device.dto.ReservationRequest;
import com.ahamo.device.dto.ReservationResponse;
import com.ahamo.device.model.Inventory;
import com.ahamo.device.model.Reservation;
import com.ahamo.device.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {
    
    private final ReservationRepository reservationRepository;
    private final InventoryService inventoryService;
    
    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        Inventory inventory = inventoryService.getOrCreateInventory(
                request.getDeviceId(), 
                request.getColor(), 
                request.getStorage()
        );
        
        if (!inventoryService.checkAvailability(
                request.getDeviceId(), 
                request.getColor(), 
                request.getStorage(), 
                request.getQuantity())) {
            throw new RuntimeException(ErrorCode.INVENTORY_NOT_AVAILABLE.getDefaultMessage());
        }
        
        inventoryService.reserveStock(inventory.getId(), request.getQuantity());
        
        Reservation reservation = new Reservation();
        reservation.setInventoryId(inventory.getId());
        reservation.setCustomerId(request.getCustomerId());
        reservation.setQuantity(request.getQuantity());
        reservation.setStatus(Reservation.ReservationStatus.RESERVED);
        reservation.setExpiresAt(LocalDateTime.now().plusWeeks(1));
        
        Reservation savedReservation = reservationRepository.save(reservation);
        
        log.info("Created reservation {} for customer {} - Device: {}:{}:{}, Quantity: {}", 
                savedReservation.getId(), request.getCustomerId(), 
                request.getDeviceId(), request.getColor(), request.getStorage(), request.getQuantity());
        
        return convertToResponse(savedReservation, request.getDeviceId(), request.getColor(), request.getStorage());
    }
    
    @Override
    @Transactional
    public ReservationResponse allocateReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException(ErrorCode.RESERVATION_NOT_FOUND.getDefaultMessage()));
        
        if (reservation.getStatus() != Reservation.ReservationStatus.RESERVED) {
            throw new RuntimeException("予約は既に処理済みです");
        }
        
        if (reservation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException(ErrorCode.RESERVATION_EXPIRED.getDefaultMessage());
        }
        
        inventoryService.allocateStock(reservation.getInventoryId(), reservation.getQuantity());
        
        reservation.setStatus(Reservation.ReservationStatus.ALLOCATED);
        Reservation savedReservation = reservationRepository.save(reservation);
        
        log.info("Allocated reservation {} for customer {}", reservationId, reservation.getCustomerId());
        
        return convertToResponse(savedReservation);
    }
    
    @Override
    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException(ErrorCode.RESERVATION_NOT_FOUND.getDefaultMessage()));
        
        if (reservation.getStatus() == Reservation.ReservationStatus.RESERVED) {
            inventoryService.releaseReservedStock(reservation.getInventoryId(), reservation.getQuantity());
        } else if (reservation.getStatus() == Reservation.ReservationStatus.ALLOCATED) {
            inventoryService.restoreStock(reservation.getInventoryId(), reservation.getQuantity());
        }
        
        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
        
        log.info("Cancelled reservation {} for customer {}", reservationId, reservation.getCustomerId());
    }
    
    @Override
    public List<ReservationResponse> getCustomerReservations(Long customerId) {
        List<Reservation> reservations = reservationRepository.findByCustomerId(customerId);
        
        return reservations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void processExpiredReservations() {
        List<Reservation> expiredReservations = reservationRepository.findExpiredReservations(LocalDateTime.now());
        
        for (Reservation reservation : expiredReservations) {
            try {
                inventoryService.releaseReservedStock(reservation.getInventoryId(), reservation.getQuantity());
                reservation.setStatus(Reservation.ReservationStatus.EXPIRED);
                reservationRepository.save(reservation);
                
                log.info("Processed expired reservation {} for customer {}", 
                        reservation.getId(), reservation.getCustomerId());
            } catch (Exception e) {
                log.error("Failed to process expired reservation {}: {}", reservation.getId(), e.getMessage());
            }
        }
        
        log.info("Processed {} expired reservations", expiredReservations.size());
    }
    
    @Override
    public ReservationResponse getReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException(ErrorCode.RESERVATION_NOT_FOUND.getDefaultMessage()));
        
        return convertToResponse(reservation);
    }
    
    private ReservationResponse convertToResponse(Reservation reservation) {
        return convertToResponse(reservation, null, null, null);
    }
    
    private ReservationResponse convertToResponse(Reservation reservation, String deviceId, String color, String storage) {
        ReservationResponse response = new ReservationResponse();
        response.setReservationId(reservation.getId());
        response.setDeviceId(deviceId);
        response.setColor(color);
        response.setStorage(storage);
        response.setQuantity(reservation.getQuantity());
        response.setStatus(reservation.getStatus().name());
        response.setExpiresAt(reservation.getExpiresAt());
        response.setCreatedAt(reservation.getCreatedAt());
        response.setCustomerId(reservation.getCustomerId());
        
        return response;
    }
}
