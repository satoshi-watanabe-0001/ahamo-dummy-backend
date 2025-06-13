package com.ahamo.backup.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestoreResult {
    
    private String backupId;
    private String restoreType;
    private boolean success;
    private LocalDateTime restoredAt;
    private String errorMessage;
    private long restoredRecords;
}
