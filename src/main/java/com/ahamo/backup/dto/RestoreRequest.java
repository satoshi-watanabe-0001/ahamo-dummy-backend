package com.ahamo.backup.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestoreRequest {
    
    @NotBlank(message = "バックアップIDは必須です")
    private String backupId;
    
    @NotBlank(message = "復元タイプは必須です")
    private String restoreType;
    
    private boolean verifyBeforeRestore;
    private boolean createBackupBeforeRestore;
}
