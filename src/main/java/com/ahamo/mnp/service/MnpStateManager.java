package com.ahamo.mnp.service;

import com.ahamo.mnp.model.MnpRequest;
import com.ahamo.mnp.model.MnpStatusHistory;
import com.ahamo.mnp.repository.MnpRepository;
import com.ahamo.mnp.repository.MnpStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MnpStateManager {

    private final MnpRepository mnpRepository;
    private final MnpStatusHistoryRepository statusHistoryRepository;

    @Transactional
    public void transitionState(Long mnpRequestId, MnpRequest.MnpStatus newStatus, String reason, String updatedBy) {
        MnpRequest mnpRequest = mnpRepository.findById(mnpRequestId)
                .orElseThrow(() -> new IllegalArgumentException("MNP request not found: " + mnpRequestId));

        MnpRequest.MnpStatus currentStatus = mnpRequest.getStatus();

        if (!isValidTransition(currentStatus, newStatus)) {
            throw new IllegalStateException(
                String.format("Invalid state transition from %s to %s for MNP request %d", 
                    currentStatus, newStatus, mnpRequestId));
        }

        MnpRequest.MnpStatus oldStatus = mnpRequest.getStatus();
        mnpRequest.setStatus(newStatus);
        mnpRequest.setUpdatedBy(updatedBy);
        mnpRepository.save(mnpRequest);

        MnpStatusHistory history = MnpStatusHistory.builder()
                .mnpRequestId(mnpRequestId)
                .fromStatus(oldStatus)
                .toStatus(newStatus)
                .reason(reason)
                .createdBy(updatedBy)
                .build();
        statusHistoryRepository.save(history);

        log.info("MNP request {} transitioned from {} to {} by {}", 
                mnpRequestId, oldStatus, newStatus, updatedBy);
    }

    @Transactional
    public void transitionStateWithError(Long mnpRequestId, MnpRequest.MnpStatus newStatus, 
                                       String reason, String errorMessage, String updatedBy) {
        MnpRequest mnpRequest = mnpRepository.findById(mnpRequestId)
                .orElseThrow(() -> new IllegalArgumentException("MNP request not found: " + mnpRequestId));

        MnpRequest.MnpStatus oldStatus = mnpRequest.getStatus();
        mnpRequest.setStatus(newStatus);
        mnpRequest.setUpdatedBy(updatedBy);
        mnpRepository.save(mnpRequest);

        MnpStatusHistory history = MnpStatusHistory.builder()
                .mnpRequestId(mnpRequestId)
                .fromStatus(oldStatus)
                .toStatus(newStatus)
                .reason(reason)
                .errorMessage(errorMessage)
                .createdBy(updatedBy)
                .build();
        statusHistoryRepository.save(history);

        log.error("MNP request {} transitioned from {} to {} with error: {}", 
                mnpRequestId, oldStatus, newStatus, errorMessage);
    }

    public boolean isValidTransition(MnpRequest.MnpStatus from, MnpRequest.MnpStatus to) {
        if (from == to) {
            return false;
        }

        switch (from) {
            case PENDING:
                return Arrays.asList(MnpRequest.MnpStatus.IN_PROGRESS, MnpRequest.MnpStatus.CANCELLED, MnpRequest.MnpStatus.FAILED)
                        .contains(to);
            case IN_PROGRESS:
                return Arrays.asList(MnpRequest.MnpStatus.COMPLETED, MnpRequest.MnpStatus.FAILED, MnpRequest.MnpStatus.CANCELLED)
                        .contains(to);
            case COMPLETED:
            case FAILED:
            case CANCELLED:
                return false;
            default:
                return false;
        }
    }

    public List<MnpStatusHistory> getStatusHistory(Long mnpRequestId) {
        return statusHistoryRepository.findByMnpRequestIdOrderByCreatedAtDesc(mnpRequestId);
    }
}
