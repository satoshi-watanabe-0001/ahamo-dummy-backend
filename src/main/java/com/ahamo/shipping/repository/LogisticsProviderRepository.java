package com.ahamo.shipping.repository;

import com.ahamo.shipping.model.LogisticsProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LogisticsProviderRepository extends JpaRepository<LogisticsProvider, Long> {
    
    Optional<LogisticsProvider> findByProviderCode(String providerCode);
    
    List<LogisticsProvider> findByIsActiveTrue();
}
