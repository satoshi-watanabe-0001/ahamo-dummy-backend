package com.ahamo.payment.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "token", unique = true, nullable = false)
    private String token;
    
    @Column(name = "masked_card_number")
    private String maskedCardNumber;
    
    @Column(name = "card_type")
    private String cardType;
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "is_revoked")
    private boolean isRevoked;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
