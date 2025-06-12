package com.ahamo.mnp.repository;

import com.ahamo.mnp.model.MnpRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MnpRepository extends JpaRepository<MnpRequest, Long> {

    Optional<MnpRequest> findByMnpId(String mnpId);

    Optional<MnpRequest> findByPhoneNumber(String phoneNumber);

    List<MnpRequest> findByContractId(String contractId);

    List<MnpRequest> findByStatus(MnpRequest.MnpStatus status);

    List<MnpRequest> findByCurrentCarrier(String currentCarrier);

    @Query("SELECT m FROM MnpRequest m WHERE m.status = :status AND m.createdAt < :before")
    List<MnpRequest> findByStatusAndCreatedAtBefore(
        @Param("status") MnpRequest.MnpStatus status,
        @Param("before") LocalDateTime before);

    @Query("SELECT m FROM MnpRequest m WHERE m.phoneNumber = :phoneNumber AND m.status IN :statuses")
    List<MnpRequest> findActiveRequestsByPhoneNumber(
        @Param("phoneNumber") String phoneNumber,
        @Param("statuses") List<MnpRequest.MnpStatus> statuses);

    boolean existsByPhoneNumberAndStatusIn(String phoneNumber, List<MnpRequest.MnpStatus> statuses);
    
    boolean existsByReservationNumber(String reservationNumber);

    @Query("SELECT COUNT(m) FROM MnpRequest m WHERE m.currentCarrier = :carrier AND m.status = :status")
    Long countByCarrierAndStatus(
        @Param("carrier") String carrier,
        @Param("status") MnpRequest.MnpStatus status);
}
