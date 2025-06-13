package com.ahamo.backup.service;

import com.ahamo.backup.dto.BackupResult;
import com.ahamo.backup.dto.RestoreRequest;
import com.ahamo.backup.dto.RestoreResult;

import java.util.List;

public interface BackupService {
    
    BackupResult createBackup();
    
    BackupResult createBackup(String backupType);
    
    List<BackupResult> getBackupHistory();
    
    RestoreResult restoreFromBackup(RestoreRequest restoreRequest);
    
    boolean verifyBackup(String backupId);
    
    void scheduleBackup();
    
    void cleanupOldBackups();
}
