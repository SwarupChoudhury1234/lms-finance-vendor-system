package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_fee_allocations")
public class StudentFeeAllocation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "user_id", nullable = false) private Long userId;
    
    @ManyToOne
    @JoinColumn(name = "fee_structure_id", nullable = false)
    private FeeStructure feeStructure;
    
    @Column(name = "original_amount", nullable = false, precision = 12, scale = 2) private BigDecimal originalAmount;
    @Column(name = "discount_applied", precision = 12, scale = 2) private BigDecimal discountApplied = BigDecimal.ZERO;
    @Column(name = "payable_amount", nullable = false, precision = 12, scale = 2) private BigDecimal payableAmount;
    @Column(name = "advance_paid", precision = 12, scale = 2) private BigDecimal advancePaid = BigDecimal.ZERO;
    @Column(name = "total_paid", precision = 12, scale = 2) private BigDecimal totalPaid = BigDecimal.ZERO;
    @Column(name = "remaining_amount", nullable = false, precision = 12, scale = 2) private BigDecimal remainingAmount;
    @Column(name = "installment_count") private Integer installmentCount = 1;
    @Column(name = "per_installment_amount", precision = 12, scale = 2) private BigDecimal perInstallmentAmount;
    @Column(columnDefinition = "JSON") private String customInstallments;
    @Column(columnDefinition = "JSON") private String paymentPlan;
    private String status = "PENDING";
    @Column(name = "due_date") private LocalDate dueDate;
    @Column(name = "allocation_date") private LocalDate allocationDate;
    @Column(name = "created_at") private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "updated_at") private LocalDateTime updatedAt;
    @Column(name = "created_by") private Long createdBy;
    @Column(name = "updated_by") private Long updatedBy;
    
    // Fix the field name: selected_alternative_id
    @Column(name = "selected_alternative_id")
    private Long selectedAlternativeId;
    
    public StudentFeeAllocation() {}
    
    // Getters/Setters
    public Long getId() { return id; } 
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; } 
    public void setUserId(Long userId) { this.userId = userId; }
    public FeeStructure getFeeStructure() { return feeStructure; } 
    public void setFeeStructure(FeeStructure feeStructure) { this.feeStructure = feeStructure; }
    public BigDecimal getOriginalAmount() { return originalAmount; } 
    public void setOriginalAmount(BigDecimal originalAmount) { this.originalAmount = originalAmount; }
    public BigDecimal getDiscountApplied() { return discountApplied; } 
    public void setDiscountApplied(BigDecimal discountApplied) { this.discountApplied = discountApplied; }
    public BigDecimal getPayableAmount() { return payableAmount; } 
    public void setPayableAmount(BigDecimal payableAmount) { this.payableAmount = payableAmount; }
    public BigDecimal getAdvancePaid() { return advancePaid; } 
    public void setAdvancePaid(BigDecimal advancePaid) { this.advancePaid = advancePaid; }
    public BigDecimal getTotalPaid() { return totalPaid; } 
    public void setTotalPaid(BigDecimal totalPaid) { this.totalPaid = totalPaid; }
    public BigDecimal getRemainingAmount() { return remainingAmount; } 
    public void setRemainingAmount(BigDecimal remainingAmount) { this.remainingAmount = remainingAmount; }
    public Integer getInstallmentCount() { return installmentCount; } 
    public void setInstallmentCount(Integer installmentCount) { this.installmentCount = installmentCount; }
    public BigDecimal getPerInstallmentAmount() { return perInstallmentAmount; } 
    public void setPerInstallmentAmount(BigDecimal perInstallmentAmount) { this.perInstallmentAmount = perInstallmentAmount; }
    public String getCustomInstallments() { return customInstallments; } 
    public void setCustomInstallments(String customInstallments) { this.customInstallments = customInstallments; }
    public String getPaymentPlan() { return paymentPlan; } 
    public void setPaymentPlan(String paymentPlan) { this.paymentPlan = paymentPlan; }
    public String getStatus() { return status; } 
    public void setStatus(String status) { this.status = status; }
    public LocalDate getDueDate() { return dueDate; } 
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public LocalDate getAllocationDate() { return allocationDate; } 
    public void setAllocationDate(LocalDate allocationDate) { this.allocationDate = allocationDate; }
    public LocalDateTime getCreatedAt() { return createdAt; } 
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } 
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public Long getCreatedBy() { return createdBy; } 
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public Long getUpdatedBy() { return updatedBy; } 
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }
    public Long getSelectedAlternativeId() { return selectedAlternativeId; } 
    public void setSelectedAlternativeId(Long selectedAlternativeId) { this.selectedAlternativeId = selectedAlternativeId; }
}