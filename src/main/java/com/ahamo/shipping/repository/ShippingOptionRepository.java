package com.ahamo.shipping.repository;

import com.ahamo.shipping.model.ShippingOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShippingOptionRepository extends JpaRepository<ShippingOption, Long> {

    Optional<ShippingOption> findByOptionCode(String optionCode);

    List<ShippingOption> findByIsActiveTrueOrderByOptionName();
}
