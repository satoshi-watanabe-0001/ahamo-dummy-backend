package com.ahamo.backup.controller;

import com.ahamo.backup.service.BackupService;
import com.ahamo.backup.dto.BackupResult;
import com.ahamo.backup.dto.RestoreRequest;
import com.ahamo.backup.dto.RestoreResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/backup")
@RequiredArgsConstructor
@Slf4j
public class BackupController {
    
    private final BackupService backupService;
    
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BackupResult> createBackup(@RequestParam(defaultValue = "FULL") String backupType) {
        log.info("バックアップ作成要求を受信: type={}", backupType);
        BackupResult result = backupService.createBackup(backupType);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/history")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BackupResult>> getBackupHistory() {
        log.info("バックアップ履歴取得要求を受信");
        List<BackupResult> history = backupService.getBackupHistory();
        return ResponseEntity.ok(history);
    }
    
    @PostMapping("/restore")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestoreResult> restoreFromBackup(@Valid @RequestBody RestoreRequest restoreRequest) {
        log.info("復元要求を受信: backupId={}", restoreRequest.getBackupId());
        RestoreResult result = backupService.restoreFromBackup(restoreRequest);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/verify/{backupId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> verifyBackup(@PathVariable String backupId) {
        log.info("バックアップ検証要求を受信: backupId={}", backupId);
        boolean isValid = backupService.verifyBackup(backupId);
        return ResponseEntity.ok(isValid);
    }
    
    @PostMapping("/cleanup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> cleanupOldBackups() {
        log.info("古いバックアップクリーンアップ要求を受信");
        backupService.cleanupOldBackups();
        return ResponseEntity.ok("クリーンアップが完了しました");
    }
}
