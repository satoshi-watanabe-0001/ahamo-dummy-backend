package com.ahamo.backup.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "backup_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BackupRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "backup_id", nullable = false, unique = true, length = 100)
    private String backupId;
    
    @Column(name = "backup_type", nullable = false, length = 50)
    private String backupType;
    
    @Column(nullable = false, length = 20)
    private String status;
    
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column
    private Long size;
    
    @Column(name = "file_path", length = 500)
    private String filePath;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @PrePersist
    public void prePersist() {
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }
        if (status == null) {
            status = "PENDING";
        }
    }
}
