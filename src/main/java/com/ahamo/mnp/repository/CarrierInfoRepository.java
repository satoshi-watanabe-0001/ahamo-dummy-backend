package com.ahamo.mnp.repository;

import com.ahamo.mnp.model.CarrierInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarrierInfoRepository extends JpaRepository<CarrierInfo, Long> {

    Optional<CarrierInfo> findByCarrierCode(String carrierCode);

    List<CarrierInfo> findByIsActiveTrue();

    boolean existsByCarrierCode(String carrierCode);
}
