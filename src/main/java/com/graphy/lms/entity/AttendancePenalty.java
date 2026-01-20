package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.ToString;

@Entity
@ToString
@Table(name = "attendance_penalties")
public class AttendancePenalty {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "student_fee_allocation_id", nullable = false)
    private Long studentFeeAllocationId;
    
    @Column(name = "absence_date", nullable = false)
    private LocalDate absenceDate;
    
    @Column(name = "penalty_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal penaltyAmount;
    
    @Column(length = 255)
    private String reason;
    
    @Column(name = "applied_by")
    private Long appliedBy;
    
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
    
    // Constructors
    public AttendancePenalty() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getStudentFeeAllocationId() {
        return studentFeeAllocationId;
    }
    
    public void setStudentFeeAllocationId(Long studentFeeAllocationId) {
        this.studentFeeAllocationId = studentFeeAllocationId;
    }
    
    public LocalDate getAbsenceDate() {
        return absenceDate;
    }
    
    public void setAbsenceDate(LocalDate absenceDate) {
        this.absenceDate = absenceDate;
    }
    
    public BigDecimal getPenaltyAmount() {
        return penaltyAmount;
    }
    
    public void setPenaltyAmount(BigDecimal penaltyAmount) {
        this.penaltyAmount = penaltyAmount;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public Long getAppliedBy() {
        return appliedBy;
    }
    
    public void setAppliedBy(Long appliedBy) {
        this.appliedBy = appliedBy;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}