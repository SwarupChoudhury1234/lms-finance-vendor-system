package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_fee_payments")
public class StudentFeePayment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    
    @ManyToOne
    @JoinColumn(name = "student_fee_allocation_id", nullable = false)
    private StudentFeeAllocation studentFeeAllocation;
    
    @ManyToOne
    @JoinColumn(name = "payment_installment_id")
    private PaymentInstallment paymentInstallment;
    
    @Column(name = "paid_amount", nullable = false, precision = 12, scale = 2) private BigDecimal paidAmount;
    @Column(name = "payment_date", nullable = false) private LocalDate paymentDate;
    @Column(name = "payment_mode") private String paymentMode;
    @Column(name = "transaction_reference") private String transactionReference;
    @Column(name = "collected_by") private Long collectedBy;
    @Column(name = "receipt_generated") private Boolean receiptGenerated = false;
    private String status = "SUCCESS";
    private String remarks;
    @Column(name = "created_at") private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "updated_at") private LocalDateTime updatedAt;
    @Column(name = "updated_by") private Long updatedBy;
    
    public StudentFeePayment() {}
    
    // Getters/Setters
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public StudentFeeAllocation getStudentFeeAllocation() { return studentFeeAllocation; } public void setStudentFeeAllocation(StudentFeeAllocation studentFeeAllocation) { this.studentFeeAllocation = studentFeeAllocation; }
    public PaymentInstallment getPaymentInstallment() { return paymentInstallment; } public void setPaymentInstallment(PaymentInstallment paymentInstallment) { this.paymentInstallment = paymentInstallment; }
    public BigDecimal getPaidAmount() { return paidAmount; } public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }
    public LocalDate getPaymentDate() { return paymentDate; } public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
    public String getPaymentMode() { return paymentMode; } public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }
    public String getTransactionReference() { return transactionReference; } public void setTransactionReference(String transactionReference) { this.transactionReference = transactionReference; }
    public Long getCollectedBy() { return collectedBy; } public void setCollectedBy(Long collectedBy) { this.collectedBy = collectedBy; }
    public Boolean getReceiptGenerated() { return receiptGenerated; } public void setReceiptGenerated(Boolean receiptGenerated) { this.receiptGenerated = receiptGenerated; }
    public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
    public String getRemarks() { return remarks; } public void setRemarks(String remarks) { this.remarks = remarks; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public Long getUpdatedBy() { return updatedBy; } public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }
}