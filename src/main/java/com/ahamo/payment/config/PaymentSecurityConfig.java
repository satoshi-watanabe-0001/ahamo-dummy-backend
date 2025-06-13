package com.ahamo.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PaymentSecurityConfig {

    @Bean
    public PasswordEncoder paymentPasswordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
