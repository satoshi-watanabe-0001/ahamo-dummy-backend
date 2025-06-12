package com.ahamo.mnp.service;

import com.ahamo.mnp.dto.TransferOutRequest;
import com.ahamo.mnp.dto.TransferOutResponse;
import com.ahamo.mnp.integration.CarrierApiClient;
import com.ahamo.mnp.model.MnpRequest;
import com.ahamo.mnp.repository.MnpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferOutServiceImpl implements TransferOutService {

    private final MnpRepository mnpRepository;
    private final MnpStateManager stateManager;
    private final List<CarrierApiClient> carrierClients;

    private Map<String, CarrierApiClient> getCarrierClientMap() {
        return carrierClients.stream()
                .collect(Collectors.toMap(CarrierApiClient::getCarrierCode, Function.identity()));
    }

    @Override
    @Transactional
    public TransferOutResponse processTransferOut(TransferOutRequest request) {
        log.info("Processing transfer-out for phone: {} to carrier: {}", 
                request.getPhoneNumber(), request.getToCarrier());

        String transferId = UUID.randomUUID().toString();
        
        MnpRequest mnpRequestEntity = MnpRequest.builder()
                .mnpId(transferId)
                .contractId(request.getContractId())
                .phoneNumber(request.getPhoneNumber())
                .currentCarrier("AHAMO")
                .status(MnpRequest.MnpStatus.PENDING)
                .type(MnpRequest.MnpType.TRANSFER_OUT)
                .estimatedCompletionDate(LocalDate.now().plusDays(1))
                .createdBy("SYSTEM")
                .build();

        mnpRepository.save(mnpRequestEntity);

        stateManager.transitionState(mnpRequestEntity.getId(), MnpRequest.MnpStatus.PENDING, 
                "転出申請を受付しました", "SYSTEM");

        return TransferOutResponse.builder()
                .transferId(transferId)
                .phoneNumber(request.getPhoneNumber())
                .reservationNumber(null)
                .status("pending")
                .expirationDate(LocalDate.now().plusDays(15))
                .createdAt(LocalDateTime.now())
                .message("転出申請を受付しました。MNP予約番号を発行します。")
                .build();
    }

    @Override
    public TransferOutResponse getTransferStatus(String transferId) {
        MnpRequest mnpRequest = mnpRepository.findByMnpId(transferId)
                .orElseThrow(() -> new IllegalArgumentException("Transfer request not found: " + transferId));

        return TransferOutResponse.builder()
                .transferId(mnpRequest.getMnpId())
                .phoneNumber(mnpRequest.getPhoneNumber())
                .reservationNumber(mnpRequest.getReservationNumber())
                .status(mnpRequest.getStatus().name().toLowerCase())
                .expirationDate(LocalDate.now().plusDays(15))
                .createdAt(mnpRequest.getCreatedAt())
                .message(getStatusMessage(mnpRequest.getStatus()))
                .build();
    }

    @Override
    public void processOutgoingTransfers() {
        log.info("Processing outgoing transfer requests");
        
        List<MnpRequest> pendingTransfers = mnpRepository.findByStatus(MnpRequest.MnpStatus.PENDING);
        
        for (MnpRequest request : pendingTransfers) {
            if (request.getType() == MnpRequest.MnpType.TRANSFER_OUT) {
                try {
                    processIndividualTransferOut(request);
                } catch (Exception e) {
                    log.error("Failed to process transfer-out request: {}", request.getMnpId(), e);
                    stateManager.transitionStateWithError(request.getId(), MnpRequest.MnpStatus.FAILED,
                            "転出処理中にエラーが発生しました", e.getMessage(), "SYSTEM");
                }
            }
        }
    }

    private void processIndividualTransferOut(MnpRequest request) {
        log.info("Processing transfer-out request: {}", request.getMnpId());
        
        stateManager.transitionState(request.getId(), MnpRequest.MnpStatus.IN_PROGRESS,
                "転出処理を開始しました", "SYSTEM");

        try {
            String reservationNumber = "AH" + System.currentTimeMillis() % 100000;
            
            request.setReservationNumber(reservationNumber);
            request.setActualCompletionDate(LocalDate.now());
            mnpRepository.save(request);

            stateManager.transitionState(request.getId(), MnpRequest.MnpStatus.COMPLETED,
                    "MNP予約番号を発行しました", "SYSTEM");
            
            log.info("Transfer-out request {} completed with reservation number: {}", 
                    request.getMnpId(), reservationNumber);
        } catch (Exception e) {
            stateManager.transitionStateWithError(request.getId(), MnpRequest.MnpStatus.FAILED,
                    "予約番号発行でエラーが発生しました", e.getMessage(), "SYSTEM");
        }
    }

    private String getStatusMessage(MnpRequest.MnpStatus status) {
        switch (status) {
            case PENDING:
                return "転出申請を受付しました。MNP予約番号を発行します。";
            case IN_PROGRESS:
                return "MNP予約番号を発行中です。";
            case COMPLETED:
                return "MNP予約番号を発行しました。転出先キャリアでの手続きを進めてください。";
            case FAILED:
                return "転出処理でエラーが発生しました。";
            case CANCELLED:
                return "転出申請がキャンセルされました。";
            default:
                return "状態不明";
        }
    }
}
