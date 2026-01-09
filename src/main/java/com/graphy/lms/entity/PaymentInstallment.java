package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_installments")
public class PaymentInstallment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    
    @ManyToOne
    @JoinColumn(name = "student_fee_allocation_id", nullable = false)
    private StudentFeeAllocation studentFeeAllocation;
    
    @Column(name = "installment_number", nullable = false) private Integer installmentNumber;
    @Column(name = "due_date", nullable = false) private LocalDate dueDate;
    @Column(name = "amount", nullable = false, precision = 12, scale = 2) private BigDecimal amount;
    @Column(name = "paid_amount", precision = 12, scale = 2) private BigDecimal paidAmount = BigDecimal.ZERO;
    @Column(name = "late_fee", precision = 12, scale = 2) private BigDecimal lateFee = BigDecimal.ZERO;
    private String status = "PENDING";
    @Column(name = "adjustment_notes", columnDefinition = "TEXT") private String adjustmentNotes;
    @Column(name = "created_at") private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "updated_at") private LocalDateTime updatedAt;
    
    public PaymentInstallment() {}
    
    // Getters/Setters
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public StudentFeeAllocation getStudentFeeAllocation() { return studentFeeAllocation; } public void setStudentFeeAllocation(StudentFeeAllocation studentFeeAllocation) { this.studentFeeAllocation = studentFeeAllocation; }
    public Integer getInstallmentNumber() { return installmentNumber; } public void setInstallmentNumber(Integer installmentNumber) { this.installmentNumber = installmentNumber; }
    public LocalDate getDueDate() { return dueDate; } public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public BigDecimal getAmount() { return amount; } public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getPaidAmount() { return paidAmount; } public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }
    public BigDecimal getLateFee() { return lateFee; } public void setLateFee(BigDecimal lateFee) { this.lateFee = lateFee; }
    public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
    public String getAdjustmentNotes() { return adjustmentNotes; } public void setAdjustmentNotes(String adjustmentNotes) { this.adjustmentNotes = adjustmentNotes; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}