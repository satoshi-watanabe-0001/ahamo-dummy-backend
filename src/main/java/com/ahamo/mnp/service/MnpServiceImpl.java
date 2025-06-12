package com.ahamo.mnp.service;

import com.ahamo.mnp.dto.*;
import com.ahamo.mnp.integration.CarrierApiClient;
import com.ahamo.mnp.model.MnpRequest;
import com.ahamo.mnp.repository.MnpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MnpServiceImpl implements MnpService {

    private final MnpRepository mnpRepository;
    private final MnpStateManager stateManager;
    private final List<CarrierApiClient> carrierClients;

    private Map<String, CarrierApiClient> getCarrierClientMap() {
        return carrierClients.stream()
                .collect(Collectors.toMap(CarrierApiClient::getCarrierCode, Function.identity()));
    }

    @Override
    public MnpEligibilityResponse checkEligibility(MnpEligibilityRequest request) {
        log.info("Checking MNP eligibility for phone: {} with carrier: {}", 
                request.getPhoneNumber(), request.getCurrentCarrier());

        List<MnpRequest.MnpStatus> activeStatuses = Arrays.asList(
                MnpRequest.MnpStatus.PENDING, MnpRequest.MnpStatus.IN_PROGRESS);
        
        if (mnpRepository.existsByPhoneNumberAndStatusIn(request.getPhoneNumber(), activeStatuses)) {
            return MnpEligibilityResponse.builder()
                    .eligible(false)
                    .phoneNumber(request.getPhoneNumber())
                    .currentCarrier(request.getCurrentCarrier())
                    .estimatedPortingTime(null)
                    .additionalRequirements(Arrays.asList())
                    .restrictions(Arrays.asList("既存のMNP申請が進行中です"))
                    .build();
        }

        CarrierApiClient carrierClient = getCarrierClientMap().get(request.getCurrentCarrier().toUpperCase());
        if (carrierClient == null) {
            return MnpEligibilityResponse.builder()
                    .eligible(false)
                    .phoneNumber(request.getPhoneNumber())
                    .currentCarrier(request.getCurrentCarrier())
                    .estimatedPortingTime(null)
                    .additionalRequirements(Arrays.asList())
                    .restrictions(Arrays.asList("サポートされていないキャリアです"))
                    .build();
        }

        try {
            return carrierClient.checkEligibility(request);
        } catch (Exception e) {
            log.error("Failed to check eligibility with carrier: {}", request.getCurrentCarrier(), e);
            return MnpEligibilityResponse.builder()
                    .eligible(false)
                    .phoneNumber(request.getPhoneNumber())
                    .currentCarrier(request.getCurrentCarrier())
                    .estimatedPortingTime(null)
                    .additionalRequirements(Arrays.asList())
                    .restrictions(Arrays.asList("キャリアとの通信でエラーが発生しました"))
                    .build();
        }
    }

    @Override
    @Transactional
    public MnpResponse submitMnpRequest(com.ahamo.mnp.dto.MnpRequest request) {
        log.info("Submitting MNP request for phone: {} with carrier: {}", 
                request.getPhoneNumber(), request.getCurrentCarrier());

        List<MnpRequest.MnpStatus> activeStatuses = Arrays.asList(
                MnpRequest.MnpStatus.PENDING, MnpRequest.MnpStatus.IN_PROGRESS);
        
        if (mnpRepository.existsByPhoneNumberAndStatusIn(request.getPhoneNumber(), activeStatuses)) {
            throw new IllegalStateException("既存のMNP申請が進行中です");
        }

        String mnpId = UUID.randomUUID().toString();
        
        MnpRequest mnpRequestEntity = MnpRequest.builder()
                .mnpId(mnpId)
                .contractId(request.getContractId())
                .phoneNumber(request.getPhoneNumber())
                .currentCarrier(request.getCurrentCarrier())
                .accountName(request.getAccountInfo().getAccountName())
                .accountNumber(request.getAccountInfo().getAccountNumber())
                .accountPassword(request.getAccountInfo().getPassword())
                .status(MnpRequest.MnpStatus.PENDING)
                .type(MnpRequest.MnpType.TRANSFER_IN)
                .desiredPortingDate(request.getDesiredPortingDate())
                .estimatedCompletionDate(LocalDate.now().plusDays(3))
                .createdBy("SYSTEM")
                .build();

        mnpRepository.save(mnpRequestEntity);

        stateManager.transitionState(mnpRequestEntity.getId(), MnpRequest.MnpStatus.PENDING, 
                "MNP申請を受付しました", "SYSTEM");

        return MnpResponse.builder()
                .mnpId(mnpId)
                .reservationNumber(null)
                .status("pending")
                .estimatedCompletionDate(mnpRequestEntity.getEstimatedCompletionDate())
                .nextSteps(Arrays.asList("申請内容の確認", "キャリアとの連携", "予約番号の発行"))
                .build();
    }

    @Override
    public MnpResponse getMnpStatus(String mnpId) {
        MnpRequest mnpRequest = mnpRepository.findByMnpId(mnpId)
                .orElseThrow(() -> new IllegalArgumentException("MNP request not found: " + mnpId));

        return MnpResponse.builder()
                .mnpId(mnpRequest.getMnpId())
                .reservationNumber(mnpRequest.getReservationNumber())
                .status(mnpRequest.getStatus().name().toLowerCase())
                .estimatedCompletionDate(mnpRequest.getEstimatedCompletionDate())
                .nextSteps(getNextSteps(mnpRequest.getStatus()))
                .build();
    }

    @Override
    public void processPendingRequests() {
        log.info("Processing pending MNP requests");
        
        List<MnpRequest> pendingRequests = mnpRepository.findByStatus(MnpRequest.MnpStatus.PENDING);
        
        for (MnpRequest request : pendingRequests) {
            try {
                processIndividualRequest(request);
            } catch (Exception e) {
                log.error("Failed to process MNP request: {}", request.getMnpId(), e);
                stateManager.transitionStateWithError(request.getId(), MnpRequest.MnpStatus.FAILED,
                        "処理中にエラーが発生しました", e.getMessage(), "SYSTEM");
            }
        }
    }

    private void processIndividualRequest(MnpRequest request) {
        log.info("Processing MNP request: {}", request.getMnpId());
        
        stateManager.transitionState(request.getId(), MnpRequest.MnpStatus.IN_PROGRESS,
                "処理を開始しました", "SYSTEM");

        CarrierApiClient carrierClient = getCarrierClientMap().get(request.getCurrentCarrier().toUpperCase());
        if (carrierClient == null) {
            stateManager.transitionStateWithError(request.getId(), MnpRequest.MnpStatus.FAILED,
                    "サポートされていないキャリアです", "Unsupported carrier: " + request.getCurrentCarrier(), "SYSTEM");
            return;
        }

        try {
            String reservationNumber = carrierClient.requestReservationNumber(
                    request.getPhoneNumber(), 
                    request.getAccountName() + ":" + request.getAccountNumber());
            
            request.setReservationNumber(reservationNumber);
            request.setActualCompletionDate(LocalDate.now());
            mnpRepository.save(request);

            stateManager.transitionState(request.getId(), MnpRequest.MnpStatus.COMPLETED,
                    "MNP予約番号を発行しました", "SYSTEM");
            
            log.info("MNP request {} completed with reservation number: {}", 
                    request.getMnpId(), reservationNumber);
        } catch (Exception e) {
            stateManager.transitionStateWithError(request.getId(), MnpRequest.MnpStatus.FAILED,
                    "キャリアとの通信でエラーが発生しました", e.getMessage(), "SYSTEM");
        }
    }

    private List<String> getNextSteps(MnpRequest.MnpStatus status) {
        switch (status) {
            case PENDING:
                return Arrays.asList("申請内容の確認", "キャリアとの連携", "予約番号の発行");
            case IN_PROGRESS:
                return Arrays.asList("キャリアでの処理中", "予約番号発行待ち");
            case COMPLETED:
                return Arrays.asList("MNP予約番号を確認してください", "新しいキャリアでの手続きを進めてください");
            case FAILED:
                return Arrays.asList("エラー内容を確認してください", "必要に応じて再申請してください");
            case CANCELLED:
                return Arrays.asList("申請がキャンセルされました");
            default:
                return Arrays.asList();
        }
    }
}
