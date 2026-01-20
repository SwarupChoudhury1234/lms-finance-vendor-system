package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.ToString;

@Entity
@ToString
@Table(name = "student_installment_plans")
public class StudentInstallmentPlan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "student_fee_allocation_id", nullable = false)
    private Long studentFeeAllocationId;
    
    @Column(name = "payment_alternative_id", nullable = false)
    private Long paymentAlternativeId;
    
    @Column(name = "installment_number", nullable = false)
    private Integer installmentNumber;
    
    @Column(name = "installment_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal installmentAmount;
    
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;
    
    @Column(name = "paid_amount", precision = 12, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;
    
    @Enumerated(EnumType.STRING)
    private InstallmentStatus status = InstallmentStatus.PENDING;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum InstallmentStatus {
        PENDING, PARTIALLY_PAID, PAID, OVERDUE
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
    public StudentInstallmentPlan() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getStudentFeeAllocationId() {
        return studentFeeAllocationId;
    }
    
    public void setStudentFeeAllocationId(Long studentFeeAllocationId) {
        this.studentFeeAllocationId = studentFeeAllocationId;
    }
    
    public Long getPaymentAlternativeId() {
        return paymentAlternativeId;
    }
    
    public void setPaymentAlternativeId(Long paymentAlternativeId) {
        this.paymentAlternativeId = paymentAlternativeId;
    }
    
    public Integer getInstallmentNumber() {
        return installmentNumber;
    }
    
    public void setInstallmentNumber(Integer installmentNumber) {
        this.installmentNumber = installmentNumber;
    }
    
    public BigDecimal getInstallmentAmount() {
        return installmentAmount;
    }
    
    public void setInstallmentAmount(BigDecimal installmentAmount) {
        this.installmentAmount = installmentAmount;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    public BigDecimal getPaidAmount() {
        return paidAmount;
    }
    
    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }
    
    public InstallmentStatus getStatus() {
        return status;
    }
    
    public void setStatus(InstallmentStatus status) {
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