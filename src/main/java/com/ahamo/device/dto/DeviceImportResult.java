package com.ahamo.device.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceImportResult {
    
    private int totalRows;
    private int successfulRows;
    private int failedRows;
    private List<DeviceImportError> errors;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeviceImportError {
        private int rowNumber;
        private String deviceId;
        private String errorMessage;
    }
}
