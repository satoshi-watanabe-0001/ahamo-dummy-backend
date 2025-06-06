package com.ahamo.device.repository;

import com.ahamo.device.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    List<Reservation> findByCustomerId(Long customerId);
    
    List<Reservation> findByStatus(Reservation.ReservationStatus status);
    
    @Query("SELECT r FROM Reservation r WHERE r.status = 'RESERVED' AND r.expiresAt < :now")
    List<Reservation> findExpiredReservations(@Param("now") LocalDateTime now);
    
    @Query("SELECT r FROM Reservation r WHERE r.inventoryId = :inventoryId AND r.status IN ('RESERVED', 'ALLOCATED')")
    List<Reservation> findActiveReservationsByInventoryId(@Param("inventoryId") Long inventoryId);
    
    @Query("SELECT SUM(r.quantity) FROM Reservation r WHERE r.inventoryId = :inventoryId AND r.status = 'RESERVED'")
    Integer getTotalReservedQuantityByInventoryId(@Param("inventoryId") Long inventoryId);
}
