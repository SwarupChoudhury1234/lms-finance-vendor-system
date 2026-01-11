package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "late_fee_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LateFeeRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "fee_type_id")
    private Long feeTypeId;
    
    @Column(name = "calculation_type", length = 20)
    private String calculationType = "FIXED"; // FIXED, PERCENTAGE, PER_DAY
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "grace_period_days")
    private Integer gracePeriodDays = 0;
    
    @Column(name = "max_late_fee", precision = 10, scale = 2)
    private BigDecimal maxLateFee;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}