package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "late_fee_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LateFeeRule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name; // "Default Late Fee Rule", "High Penalty Rule"
    
    @Column(name = "grace_period_days")
    private Integer gracePeriodDays = 5;
    
    @Column(name = "penalty_cycle_months")
    private Integer penaltyCycleMonths = 1; // k months (every k months)
    
    @Column(name = "penalty_amounts", columnDefinition = "JSON", nullable = false)
    private String penaltyAmounts; // [{months: 1, amount: 200}, {months: 2, amount: 300}]
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}