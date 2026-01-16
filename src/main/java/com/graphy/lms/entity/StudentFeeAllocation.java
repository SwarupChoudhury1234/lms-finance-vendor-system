package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_fee_allocations")
public class StudentFeeAllocation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "fee_structure_id", nullable = false)
    private Long feeStructureId;
    
    @Column(name = "original_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal originalAmount;
    
    @Column(name = "total_discount", precision = 12, scale = 2)
    private BigDecimal totalDiscount = BigDecimal.ZERO;
    
    @Column(name = "payable_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal payableAmount;
    
    @Column(name = "advance_payment", precision = 12, scale = 2)
    private BigDecimal advancePayment = BigDecimal.ZERO;
    
    @Column(name = "remaining_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal remainingAmount;
    
    @Column(length = 10)
    private String currency = "INR";
    
    @Column(name = "allocation_date")
    private LocalDate allocationDate;
    
    @Enumerated(EnumType.STRING)
    private AllocationStatus status = AllocationStatus.ACTIVE;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum AllocationStatus {
        ACTIVE, COMPLETED, CANCELLED, REFUNDED
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (allocationDate == null) {
            allocationDate = LocalDate.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public StudentFeeAllocation() {}
    
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
    
    public Long getFeeStructureId() {
        return feeStructureId;
    }
    
    public void setFeeStructureId(Long feeStructureId) {
        this.feeStructureId = feeStructureId;
    }
    
    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }
    
    public void setOriginalAmount(BigDecimal originalAmount) {
        this.originalAmount = originalAmount;
    }
    
    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }
    
    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }
    
    public BigDecimal getPayableAmount() {
        return payableAmount;
    }
    
    public void setPayableAmount(BigDecimal payableAmount) {
        this.payableAmount = payableAmount;
    }
    
    public BigDecimal getAdvancePayment() {
        return advancePayment;
    }
    
    public void setAdvancePayment(BigDecimal advancePayment) {
        this.advancePayment = advancePayment;
    }
    
    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }
    
    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public LocalDate getAllocationDate() {
        return allocationDate;
    }
    
    public void setAllocationDate(LocalDate allocationDate) {
        this.allocationDate = allocationDate;
    }
    
    public AllocationStatus getStatus() {
        return status;
    }
    
    public void setStatus(AllocationStatus status) {
        this.status = status;
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