CREATE TABLE payment_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    masked_card_number VARCHAR(50),
    card_type VARCHAR(20),
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_revoked BOOLEAN DEFAULT FALSE,
    INDEX idx_token (token),
    INDEX idx_expires_at (expires_at)
);
