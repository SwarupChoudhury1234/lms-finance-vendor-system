package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fee_refunds")
public class FeeRefund {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    
    @ManyToOne
    @JoinColumn(name = "student_fee_payment_id", nullable = false)
    private StudentFeePayment studentFeePayment;
    
    @Column(name = "refund_amount", nullable = false, precision = 12, scale = 2) private BigDecimal refundAmount;
    @Column(name = "refund_date", nullable = false) private LocalDate refundDate;
    private String reason;
    @Column(name = "approved_by") private Long approvedBy;
    @Column(name = "approval_date") private LocalDate approvalDate;
    private String status = "PENDING";
    @Column(name = "created_at") private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "updated_at") private LocalDateTime updatedAt;
    
    public FeeRefund() {}
    
    // Getters/Setters
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public StudentFeePayment getStudentFeePayment() { return studentFeePayment; } public void setStudentFeePayment(StudentFeePayment studentFeePayment) { this.studentFeePayment = studentFeePayment; }
    public BigDecimal getRefundAmount() { return refundAmount; } public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }
    public LocalDate getRefundDate() { return refundDate; } public void setRefundDate(LocalDate refundDate) { this.refundDate = refundDate; }
    public String getReason() { return reason; } public void setReason(String reason) { this.reason = reason; }
    public Long getApprovedBy() { return approvedBy; } public void setApprovedBy(Long approvedBy) { this.approvedBy = approvedBy; }
    public LocalDate getApprovalDate() { return approvalDate; } public void setApprovalDate(LocalDate approvalDate) { this.approvalDate = approvalDate; }
    public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}