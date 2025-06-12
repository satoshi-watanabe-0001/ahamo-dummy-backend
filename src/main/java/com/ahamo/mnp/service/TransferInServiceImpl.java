package com.ahamo.mnp.service;

import com.ahamo.mnp.dto.TransferInRequest;
import com.ahamo.mnp.dto.TransferInResponse;
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
public class TransferInServiceImpl implements TransferInService {

    private final MnpRepository mnpRepository;
    private final MnpStateManager stateManager;
    private final List<CarrierApiClient> carrierClients;

    private Map<String, CarrierApiClient> getCarrierClientMap() {
        return carrierClients.stream()
                .collect(Collectors.toMap(CarrierApiClient::getCarrierCode, Function.identity()));
    }

    @Override
    @Transactional
    public TransferInResponse processTransferIn(TransferInRequest request) {
        log.info("Processing transfer-in for phone: {} from carrier: {}", 
                request.getPhoneNumber(), request.getFromCarrier());

        String transferId = UUID.randomUUID().toString();
        
        MnpRequest mnpRequestEntity = MnpRequest.builder()
                .mnpId(transferId)
                .contractId(request.getContractId())
                .phoneNumber(request.getPhoneNumber())
                .currentCarrier(request.getFromCarrier())
                .reservationNumber(request.getReservationNumber())
                .status(MnpRequest.MnpStatus.PENDING)
                .type(MnpRequest.MnpType.TRANSFER_IN)
                .desiredPortingDate(request.getDesiredTransferDate())
                .estimatedCompletionDate(LocalDate.now().plusDays(1))
                .createdBy("SYSTEM")
                .build();

        mnpRepository.save(mnpRequestEntity);

        stateManager.transitionState(mnpRequestEntity.getId(), MnpRequest.MnpStatus.PENDING, 
                "転入申請を受付しました", "SYSTEM");

        return TransferInResponse.builder()
                .transferId(transferId)
                .phoneNumber(request.getPhoneNumber())
                .status("pending")
                .estimatedCompletionDate(mnpRequestEntity.getEstimatedCompletionDate())
                .createdAt(LocalDateTime.now())
                .message("転入申請を受付しました。処理を開始します。")
                .build();
    }

    @Override
    public TransferInResponse getTransferStatus(String transferId) {
        MnpRequest mnpRequest = mnpRepository.findByMnpId(transferId)
                .orElseThrow(() -> new IllegalArgumentException("Transfer request not found: " + transferId));

        return TransferInResponse.builder()
                .transferId(mnpRequest.getMnpId())
                .phoneNumber(mnpRequest.getPhoneNumber())
                .status(mnpRequest.getStatus().name().toLowerCase())
                .estimatedCompletionDate(mnpRequest.getEstimatedCompletionDate())
                .createdAt(mnpRequest.getCreatedAt())
                .message(getStatusMessage(mnpRequest.getStatus()))
                .build();
    }

    @Override
    public void processInProgressTransfers() {
        log.info("Processing in-progress transfer-in requests");
        
        List<MnpRequest> pendingTransfers = mnpRepository.findByStatus(MnpRequest.MnpStatus.PENDING);
        
        for (MnpRequest request : pendingTransfers) {
            if (request.getType() == MnpRequest.MnpType.TRANSFER_IN) {
                try {
                    processIndividualTransferIn(request);
                } catch (Exception e) {
                    log.error("Failed to process transfer-in request: {}", request.getMnpId(), e);
                    stateManager.transitionStateWithError(request.getId(), MnpRequest.MnpStatus.FAILED,
                            "転入処理中にエラーが発生しました", e.getMessage(), "SYSTEM");
                }
            }
        }
    }

    private void processIndividualTransferIn(MnpRequest request) {
        log.info("Processing transfer-in request: {}", request.getMnpId());
        
        stateManager.transitionState(request.getId(), MnpRequest.MnpStatus.IN_PROGRESS,
                "転入処理を開始しました", "SYSTEM");

        CarrierApiClient carrierClient = getCarrierClientMap().get(request.getCurrentCarrier().toUpperCase());
        if (carrierClient == null) {
            stateManager.transitionStateWithError(request.getId(), MnpRequest.MnpStatus.FAILED,
                    "サポートされていないキャリアです", "Unsupported carrier: " + request.getCurrentCarrier(), "SYSTEM");
            return;
        }

        try {
            boolean confirmed = carrierClient.confirmTransfer(request.getPhoneNumber(), request.getReservationNumber());
            
            if (confirmed) {
                request.setActualCompletionDate(LocalDate.now());
                mnpRepository.save(request);

                stateManager.transitionState(request.getId(), MnpRequest.MnpStatus.COMPLETED,
                        "転入処理が完了しました", "SYSTEM");
                
                log.info("Transfer-in request {} completed successfully", request.getMnpId());
            } else {
                stateManager.transitionStateWithError(request.getId(), MnpRequest.MnpStatus.FAILED,
                        "キャリアでの転入確認に失敗しました", "Transfer confirmation failed", "SYSTEM");
            }
        } catch (Exception e) {
            stateManager.transitionStateWithError(request.getId(), MnpRequest.MnpStatus.FAILED,
                    "キャリアとの通信でエラーが発生しました", e.getMessage(), "SYSTEM");
        }
    }

    private String getStatusMessage(MnpRequest.MnpStatus status) {
        switch (status) {
            case PENDING:
                return "転入申請を受付しました。処理を開始します。";
            case IN_PROGRESS:
                return "転入処理を実行中です。";
            case COMPLETED:
                return "転入処理が完了しました。";
            case FAILED:
                return "転入処理でエラーが発生しました。";
            case CANCELLED:
                return "転入申請がキャンセルされました。";
            default:
                return "状態不明";
        }
    }
}
