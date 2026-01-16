package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "late_fee_config")
public class LateFeeConfig {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_schedule", nullable = false)
    private PaymentSchedule paymentSchedule;
    
    @Column(name = "period_count", nullable = false)
    private Integer periodCount;
    
    @Column(name = "penalty_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal penaltyAmount;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;
    
    @Column(name = "effective_to")
    private LocalDate effectiveTo;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum PaymentSchedule {
        MONTHLY, QUARTERLY, YEARLY
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public LateFeeConfig() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public PaymentSchedule getPaymentSchedule() {
        return paymentSchedule;
    }
    
    public void setPaymentSchedule(PaymentSchedule paymentSchedule) {
        this.paymentSchedule = paymentSchedule;
    }
    
    public Integer getPeriodCount() {
        return periodCount;
    }
    
    public void setPeriodCount(Integer periodCount) {
        this.periodCount = periodCount;
    }
    
    public BigDecimal getPenaltyAmount() {
        return penaltyAmount;
    }
    
    public void setPenaltyAmount(BigDecimal penaltyAmount) {
        this.penaltyAmount = penaltyAmount;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }
    
    public void setEffectiveFrom(LocalDate effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }
    
    public LocalDate getEffectiveTo() {
        return effectiveTo;
    }
    
    public void setEffectiveTo(LocalDate effectiveTo) {
        this.effectiveTo = effectiveTo;
    }
    
    public Long getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
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