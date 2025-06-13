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
public class BackupResult {
    
    private String backupId;
    private String backupType;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long size;
    private boolean success;
    private String errorMessage;
    private String filePath;
}
