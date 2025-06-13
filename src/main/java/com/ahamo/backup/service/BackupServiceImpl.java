package com.ahamo.backup.service;

import com.ahamo.backup.dto.BackupResult;
import com.ahamo.backup.dto.RestoreRequest;
import com.ahamo.backup.dto.RestoreResult;
import com.ahamo.backup.model.BackupRecord;
import com.ahamo.backup.repository.BackupRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BackupServiceImpl implements BackupService {
    
    private final BackupRecordRepository backupRecordRepository;
    
    @Override
    @Transactional
    public BackupResult createBackup() {
        return createBackup("FULL");
    }
    
    @Override
    @Transactional
    public BackupResult createBackup(String backupType) {
        log.info("バックアップ作成開始: type={}", backupType);
        
        try {
            String backupId = UUID.randomUUID().toString();
            LocalDateTime startTime = LocalDateTime.now();
            
            BackupRecord record = BackupRecord.builder()
                    .backupId(backupId)
                    .backupType(backupType)
                    .status("IN_PROGRESS")
                    .startTime(startTime)
                    .build();
            
            backupRecordRepository.save(record);
            
            boolean success = performBackup(backupType, backupId);
            
            LocalDateTime endTime = LocalDateTime.now();
            record.setStatus(success ? "COMPLETED" : "FAILED");
            record.setEndTime(endTime);
            record.setSize(calculateBackupSize(backupId));
            
            backupRecordRepository.save(record);
            
            BackupResult result = BackupResult.builder()
                    .backupId(backupId)
                    .backupType(backupType)
                    .status(record.getStatus())
                    .startTime(startTime)
                    .endTime(endTime)
                    .size(record.getSize())
                    .success(success)
                    .build();
            
            log.info("バックアップ作成完了: backupId={}, status={}", backupId, record.getStatus());
            
            return result;
            
        } catch (Exception e) {
            log.error("バックアップ作成中にエラーが発生", e);
            return BackupResult.builder()
                    .backupType(backupType)
                    .status("ERROR")
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }
    
    @Override
    public List<BackupResult> getBackupHistory() {
        log.debug("バックアップ履歴取得");
        
        return backupRecordRepository.findAllByOrderByStartTimeDesc().stream()
                .map(this::convertToResult)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public RestoreResult restoreFromBackup(RestoreRequest restoreRequest) {
        log.info("バックアップからの復元開始: backupId={}", restoreRequest.getBackupId());
        
        try {
            BackupRecord backupRecord = backupRecordRepository.findByBackupId(restoreRequest.getBackupId())
                    .orElseThrow(() -> new RuntimeException("バックアップが見つかりません: " + restoreRequest.getBackupId()));
            
            if (!"COMPLETED".equals(backupRecord.getStatus())) {
                throw new RuntimeException("バックアップが完了していません: " + restoreRequest.getBackupId());
            }
            
            boolean success = performRestore(restoreRequest.getBackupId(), restoreRequest.getRestoreType());
            
            RestoreResult result = RestoreResult.builder()
                    .backupId(restoreRequest.getBackupId())
                    .restoreType(restoreRequest.getRestoreType())
                    .success(success)
                    .restoredAt(LocalDateTime.now())
                    .build();
            
            log.info("復元完了: backupId={}, success={}", restoreRequest.getBackupId(), success);
            
            return result;
            
        } catch (Exception e) {
            log.error("復元中にエラーが発生", e);
            return RestoreResult.builder()
                    .backupId(restoreRequest.getBackupId())
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }
    
    @Override
    public boolean verifyBackup(String backupId) {
        log.info("バックアップ検証開始: backupId={}", backupId);
        
        try {
            BackupRecord record = backupRecordRepository.findByBackupId(backupId)
                    .orElseThrow(() -> new RuntimeException("バックアップが見つかりません: " + backupId));
            
            boolean isValid = performBackupVerification(backupId);
            
            log.info("バックアップ検証完了: backupId={}, valid={}", backupId, isValid);
            
            return isValid;
            
        } catch (Exception e) {
            log.error("バックアップ検証中にエラーが発生", e);
            return false;
        }
    }
    
    @Override
    @Scheduled(cron = "0 0 2 * * ?")
    public void scheduleBackup() {
        log.info("スケジュールバックアップ開始");
        createBackup("SCHEDULED");
    }
    
    @Override
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanupOldBackups() {
        log.info("古いバックアップのクリーンアップ開始");
        
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
            List<BackupRecord> oldBackups = backupRecordRepository.findByStartTimeBefore(cutoffDate);
            
            for (BackupRecord backup : oldBackups) {
                deleteBackupFiles(backup.getBackupId());
                backupRecordRepository.delete(backup);
                log.info("古いバックアップを削除: backupId={}", backup.getBackupId());
            }
            
            log.info("クリーンアップ完了: 削除されたバックアップ数={}", oldBackups.size());
            
        } catch (Exception e) {
            log.error("バックアップクリーンアップ中にエラーが発生", e);
        }
    }
    
    private boolean performBackup(String backupType, String backupId) {
        log.debug("バックアップ実行: type={}, id={}", backupType, backupId);
        
        try {
            Thread.sleep(1000);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    private boolean performRestore(String backupId, String restoreType) {
        log.debug("復元実行: backupId={}, type={}", backupId, restoreType);
        
        try {
            Thread.sleep(2000);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    private boolean performBackupVerification(String backupId) {
        log.debug("バックアップ検証実行: backupId={}", backupId);
        
        try {
            Thread.sleep(500);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    private long calculateBackupSize(String backupId) {
        return 1024 * 1024 * 100;
    }
    
    private void deleteBackupFiles(String backupId) {
        log.debug("バックアップファイル削除: backupId={}", backupId);
    }
    
    private BackupResult convertToResult(BackupRecord record) {
        return BackupResult.builder()
                .backupId(record.getBackupId())
                .backupType(record.getBackupType())
                .status(record.getStatus())
                .startTime(record.getStartTime())
                .endTime(record.getEndTime())
                .size(record.getSize())
                .success("COMPLETED".equals(record.getStatus()))
                .build();
    }
}
