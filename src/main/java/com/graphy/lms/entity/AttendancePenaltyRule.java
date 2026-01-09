package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_penalty_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendancePenaltyRule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "min_attendance_percentage", precision = 5, scale = 2)
    private BigDecimal minAttendancePercentage; // Below this, penalty applies
    
    @Column(name = "penalty_per_absent", nullable = false, precision = 10, scale = 2)
    private BigDecimal penaltyPerAbsent; // ₹ per absent day
    
    @Column(name = "max_penalty_per_month", precision = 12, scale = 2)
    private BigDecimal maxPenaltyPerMonth;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}