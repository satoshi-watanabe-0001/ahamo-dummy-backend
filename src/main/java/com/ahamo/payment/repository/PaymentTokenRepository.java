package com.ahamo.payment.repository;

import com.ahamo.payment.model.PaymentToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PaymentTokenRepository extends JpaRepository<PaymentToken, Long> {
    
    Optional<PaymentToken> findByToken(String token);
    
    @Query("SELECT pt FROM PaymentToken pt WHERE pt.token = :token AND pt.expiresAt > :now AND pt.isRevoked = false")
    Optional<PaymentToken> findValidToken(@Param("token") String token, @Param("now") LocalDateTime now);
    
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
    
    void deleteByToken(String token);
}
