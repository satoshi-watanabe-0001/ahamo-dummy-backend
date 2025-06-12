package com.ahamo.payment.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;

@Configuration
public class PciComplianceConfig {
    
    @Bean
    public SecureRandom secureRandom() {
        return new SecureRandom();
    }
    
    @Bean
    public PasswordEncoder tokenEncoder() {
        return new BCryptPasswordEncoder(12, secureRandom());
    }
}
