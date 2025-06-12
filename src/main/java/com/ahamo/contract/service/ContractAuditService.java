package com.ahamo.contract.service;

import com.ahamo.contract.dto.ContractAuditTrailResponse;
import com.ahamo.contract.model.ContractAuditEvent;

import java.util.List;
import java.util.Map;

public interface ContractAuditService {

    void logEvent(String contractId, String eventType, String userId, Map<String, Object> details);

    ContractAuditTrailResponse getAuditTrail(String contractId);

    List<ContractAuditEvent> getEventsByType(String contractId, List<String> eventTypes);

    void logSignatureEvent(String contractId, String eventType, String userId, 
                          String certificateId, String timestampToken, String hashValue);
}
