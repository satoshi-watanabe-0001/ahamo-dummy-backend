package com.ahamo.contract.service;

import com.ahamo.contract.dto.ContractAuditTrailResponse;
import com.ahamo.contract.model.ContractAuditEvent;
import com.ahamo.contract.repository.ContractAuditEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ContractAuditServiceImpl implements ContractAuditService {

    private final ContractAuditEventRepository auditEventRepository;
    private final ObjectMapper objectMapper;
    private final HttpServletRequest httpServletRequest;

    @Override
    public void logEvent(String contractId, String eventType, String userId, Map<String, Object> details) {
        log.debug("Logging audit event for contract: {} type: {}", contractId, eventType);

        try {
            ContractAuditEvent event = ContractAuditEvent.builder()
                .eventUuid(UUID.randomUUID().toString())
                .contractId(contractId)
                .eventType(ContractAuditEvent.EventType.valueOf(eventType.toUpperCase()))
                .timestamp(LocalDateTime.now())
                .userId(userId)
                .ipAddress(getClientIpAddress())
                .userAgent(httpServletRequest.getHeader("User-Agent"))
                .details(objectMapper.writeValueAsString(details))
                .build();

            auditEventRepository.save(event);
            log.debug("Audit event logged with ID: {}", event.getEventUuid());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize audit event details", e);
        } catch (Exception e) {
            log.error("Failed to log audit event", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ContractAuditTrailResponse getAuditTrail(String contractId) {
        log.info("Retrieving audit trail for contract: {}", contractId);

        List<ContractAuditEvent> events = auditEventRepository.findByContractIdOrderByTimestampDesc(contractId);

        List<ContractAuditTrailResponse.AuditEvent> auditEvents = events.stream()
            .map(this::convertToAuditEventDto)
            .collect(Collectors.toList());

        return ContractAuditTrailResponse.builder()
            .contractId(contractId)
            .auditEvents(auditEvents)
            .generatedAt(LocalDateTime.now())
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContractAuditEvent> getEventsByType(String contractId, List<String> eventTypes) {
        log.info("Retrieving audit events by type for contract: {}", contractId);

        List<ContractAuditEvent.EventType> types = eventTypes.stream()
            .map(type -> ContractAuditEvent.EventType.valueOf(type.toUpperCase()))
            .collect(Collectors.toList());

        return auditEventRepository.findByContractIdAndEventTypes(contractId, types);
    }

    @Override
    public void logSignatureEvent(String contractId, String eventType, String userId, 
                                 String certificateId, String timestampToken, String hashValue) {
        log.debug("Logging signature audit event for contract: {}", contractId);

        try {
            ContractAuditEvent event = ContractAuditEvent.builder()
                .eventUuid(UUID.randomUUID().toString())
                .contractId(contractId)
                .eventType(ContractAuditEvent.EventType.valueOf(eventType.toUpperCase()))
                .timestamp(LocalDateTime.now())
                .userId(userId)
                .ipAddress(getClientIpAddress())
                .userAgent(httpServletRequest.getHeader("User-Agent"))
                .certificateId(certificateId)
                .timestampToken(timestampToken)
                .hashValue(hashValue)
                .build();

            auditEventRepository.save(event);
            log.debug("Signature audit event logged with ID: {}", event.getEventUuid());
        } catch (Exception e) {
            log.error("Failed to log signature audit event", e);
        }
    }

    private ContractAuditTrailResponse.AuditEvent convertToAuditEventDto(ContractAuditEvent event) {
        Map<String, Object> details = null;
        try {
            if (event.getDetails() != null) {
                details = objectMapper.readValue(event.getDetails(), Map.class);
            }
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse audit event details", e);
        }

        Map<String, Object> evidence = null;
        if (event.getCertificateId() != null || event.getTimestampToken() != null || event.getHashValue() != null) {
            evidence = Map.of(
                "certificateId", event.getCertificateId() != null ? event.getCertificateId() : "",
                "timestampToken", event.getTimestampToken() != null ? event.getTimestampToken() : "",
                "hashValue", event.getHashValue() != null ? event.getHashValue() : ""
            );
        }

        return ContractAuditTrailResponse.AuditEvent.builder()
            .eventId(event.getEventUuid())
            .eventType(event.getEventType().name().toLowerCase())
            .timestamp(event.getTimestamp())
            .userId(event.getUserId())
            .ipAddress(event.getIpAddress())
            .userAgent(event.getUserAgent())
            .details(details)
            .evidence(evidence)
            .build();
    }

    private String getClientIpAddress() {
        String xForwardedFor = httpServletRequest.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = httpServletRequest.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return httpServletRequest.getRemoteAddr();
    }
}
