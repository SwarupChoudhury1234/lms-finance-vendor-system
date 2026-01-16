package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "late_fee_penalties")
public class LateFeePenalty {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "student_installment_plan_id", nullable = false)
    private Long studentInstallmentPlanId;
    
    @Column(name = "penalty_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal penaltyAmount;
    
    @Column(name = "penalty_date", nullable = false)
    private LocalDate penaltyDate;
    
    @Column(length = 255)
    private String reason;
    
    @Column(name = "is_waived")
    private Boolean isWaived = false;
    
    @Column(name = "waived_by")
    private Long waivedBy;
    
    @Column(name = "waived_date")
    private LocalDate waivedDate;
    
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
    public LateFeePenalty() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getStudentInstallmentPlanId() {
        return studentInstallmentPlanId;
    }
    
    public void setStudentInstallmentPlanId(Long studentInstallmentPlanId) {
        this.studentInstallmentPlanId = studentInstallmentPlanId;
    }
    
    public BigDecimal getPenaltyAmount() {
        return penaltyAmount;
    }
    
    public void setPenaltyAmount(BigDecimal penaltyAmount) {
        this.penaltyAmount = penaltyAmount;
    }
    
    public LocalDate getPenaltyDate() {
        return penaltyDate;
    }
    
    public void setPenaltyDate(LocalDate penaltyDate) {
        this.penaltyDate = penaltyDate;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public Boolean getIsWaived() {
        return isWaived;
    }
    
    public void setIsWaived(Boolean isWaived) {
        this.isWaived = isWaived;
    }
    
    public Long getWaivedBy() {
        return waivedBy;
    }
    
    public void setWaivedBy(Long waivedBy) {
        this.waivedBy = waivedBy;
    }
    
    public LocalDate getWaivedDate() {
        return waivedDate;
    }
    
    public void setWaivedDate(LocalDate waivedDate) {
        this.waivedDate = waivedDate;
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