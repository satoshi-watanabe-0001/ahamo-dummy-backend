package com.ahamo.payment.service;

import com.ahamo.payment.repository.PaymentTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentTokenCleanupService {
    
    private final PaymentTokenRepository paymentTokenRepository;
    
    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void cleanupExpiredTokens() {
        try {
            LocalDateTime cutoff = LocalDateTime.now();
            paymentTokenRepository.deleteByExpiresAtBefore(cutoff);
            log.info("Cleaned up expired payment tokens before: {}", cutoff);
        } catch (Exception e) {
            log.error("Failed to cleanup expired payment tokens", e);
        }
    }
}
