package com.ahamo.backup.repository;

import com.ahamo.backup.model.BackupRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BackupRecordRepository extends JpaRepository<BackupRecord, Long> {
    
    Optional<BackupRecord> findByBackupId(String backupId);
    
    List<BackupRecord> findAllByOrderByStartTimeDesc();
    
    List<BackupRecord> findByStatus(String status);
    
    List<BackupRecord> findByBackupType(String backupType);
    
    List<BackupRecord> findByStartTimeBefore(LocalDateTime cutoffDate);
    
    @Query("SELECT b FROM BackupRecord b WHERE b.startTime >= :startTime AND b.startTime <= :endTime")
    List<BackupRecord> findByStartTimeBetween(@Param("startTime") LocalDateTime startTime, 
                                              @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT COUNT(b) FROM BackupRecord b WHERE b.status = 'COMPLETED'")
    long countSuccessfulBackups();
    
    @Query("SELECT COUNT(b) FROM BackupRecord b WHERE b.status = 'FAILED'")
    long countFailedBackups();
}
