package com.ahamo.mnp.controller;

import com.ahamo.mnp.dto.TransferInRequest;
import com.ahamo.mnp.dto.TransferInResponse;
import com.ahamo.mnp.dto.TransferOutRequest;
import com.ahamo.mnp.dto.TransferOutResponse;
import com.ahamo.mnp.service.TransferInService;
import com.ahamo.mnp.service.TransferOutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/mnp")
@RequiredArgsConstructor
public class TransferController {
    
    private final TransferInService transferInService;
    private final TransferOutService transferOutService;
    
    @PostMapping("/transfer-in")
    public ResponseEntity<TransferInResponse> processTransferIn(
            @Valid @RequestBody TransferInRequest request) {
        TransferInResponse response = transferInService.processTransferIn(request);
        return ResponseEntity.status(201).body(response);
    }
    
    @GetMapping("/transfer-in/{transferId}")
    public ResponseEntity<TransferInResponse> getTransferInStatus(@PathVariable String transferId) {
        TransferInResponse response = transferInService.getTransferStatus(transferId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/transfer-out")
    public ResponseEntity<TransferOutResponse> processTransferOut(
            @Valid @RequestBody TransferOutRequest request) {
        TransferOutResponse response = transferOutService.processTransferOut(request);
        return ResponseEntity.status(201).body(response);
    }
    
    @GetMapping("/transfer-out/{transferId}")
    public ResponseEntity<TransferOutResponse> getTransferOutStatus(@PathVariable String transferId) {
        TransferOutResponse response = transferOutService.getTransferStatus(transferId);
        return ResponseEntity.ok(response);
    }
}
