package com.ahamo.option.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "options")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Option {
    
    @Id
    private String id;
    
    @Column(nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OptionCategory category;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "monthly_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyFee;
    
    @Column(name = "one_time_fee", precision = 10, scale = 2)
    private BigDecimal oneTimeFee;
    
    @Column(name = "is_active")
    private Boolean isActive;
    
    @Column(name = "effective_start_date")
    private LocalDateTime effectiveStartDate;
    
    @Column(name = "effective_end_date")
    private LocalDateTime effectiveEndDate;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "updated_by")
    private String updatedBy;
    
    public enum OptionCategory {
        insurance, accessory, service
    }
}
