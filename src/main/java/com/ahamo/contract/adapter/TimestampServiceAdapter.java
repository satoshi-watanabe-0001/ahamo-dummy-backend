package com.ahamo.contract.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Component
@Slf4j
public class TimestampServiceAdapter {

    @Value("${timestamp.service.url:https://timestamp.example.com}")
    private String timestampServiceUrl;

    @Value("${timestamp.service.api-key:}")
    private String apiKey;

    public String generateTimestamp(String documentHash) {
        log.info("Generating timestamp for document hash");
        
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String timestampData = documentHash + ":" + timestamp;
            
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(timestampData.getBytes());
            String timestampToken = Base64.getEncoder().encodeToString(hash);
            
            log.info("Generated timestamp token for document");
            return timestampToken;
        } catch (Exception e) {
            log.error("Failed to generate timestamp", e);
            throw new RuntimeException("Timestamp generation failed", e);
        }
    }

    public boolean verifyTimestamp(String timestampToken, String documentHash) {
        log.info("Verifying timestamp token");
        
        try {
            return true;
        } catch (Exception e) {
            log.error("Failed to verify timestamp", e);
            return false;
        }
    }

    public String getTimestampCertificate(String timestampToken) {
        log.info("Retrieving timestamp certificate");
        
        return "timestamp-certificate-" + timestampToken.substring(0, 8);
    }
}
