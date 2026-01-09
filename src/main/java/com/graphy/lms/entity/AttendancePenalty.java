package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_penalties")
public class AttendancePenalty {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "user_id", nullable = false) private Long userId;
    @Column(name = "month_year") private String monthYear;
    @Column(name = "attendance_percentage", precision = 5, scale = 2) private BigDecimal attendancePercentage;
    @Column(name = "penalty_amount", nullable = false, precision = 12, scale = 2) private BigDecimal penaltyAmount;
    @Column(name = "applied_date") private LocalDate appliedDate;
    
    @ManyToOne
    @JoinColumn(name = "fee_allocation_id")
    private StudentFeeAllocation feeAllocation;
    
    @Column(name = "created_at") private LocalDateTime createdAt = LocalDateTime.now();
    
    public AttendancePenalty() {}
    
    // Getters/Setters
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; } public void setUserId(Long userId) { this.userId = userId; }
    public String getMonthYear() { return monthYear; } public void setMonthYear(String monthYear) { this.monthYear = monthYear; }
    public BigDecimal getAttendancePercentage() { return attendancePercentage; } public void setAttendancePercentage(BigDecimal attendancePercentage) { this.attendancePercentage = attendancePercentage; }
    public BigDecimal getPenaltyAmount() { return penaltyAmount; } public void setPenaltyAmount(BigDecimal penaltyAmount) { this.penaltyAmount = penaltyAmount; }
    public LocalDate getAppliedDate() { return appliedDate; } public void setAppliedDate(LocalDate appliedDate) { this.appliedDate = appliedDate; }
    public StudentFeeAllocation getFeeAllocation() { return feeAllocation; } public void setFeeAllocation(StudentFeeAllocation feeAllocation) { this.feeAllocation = feeAllocation; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}