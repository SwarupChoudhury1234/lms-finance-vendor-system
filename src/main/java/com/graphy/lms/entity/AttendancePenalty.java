package com.graphy.lms.entity;

import javax.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_penalties")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendancePenalty {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "month_year", length = 7) // Format: YYYY-MM
    private String monthYear;
    
    @Column(name = "attendance_percentage", precision = 5, scale = 2)
    private BigDecimal attendancePercentage;
    
    @Column(name = "penalty_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal penaltyAmount;
    
    @Column(name = "applied_date")
    private LocalDate appliedDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_allocation_id")
    private StudentFeeAllocation feeAllocation;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (appliedDate == null) {
            appliedDate = LocalDate.now();
        }
    }
}