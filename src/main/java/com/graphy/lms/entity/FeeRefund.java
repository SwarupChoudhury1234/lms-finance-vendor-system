package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.ToString;

@Entity
@ToString
@Table(name = "fee_refunds")
public class FeeRefund {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "student_fee_payment_id", nullable = false)
    private Long studentFeePaymentId;
    
    @Column(name = "student_fee_allocation_id", nullable = false)
    private Long studentFeeAllocationId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "refund_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal refundAmount;
    
    @Column(name = "refund_reason", length = 500)
    private String refundReason;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "refund_status")
    private RefundStatus refundStatus = RefundStatus.PENDING;
    
    @Column(name = "requested_date", nullable = false)
    private LocalDate requestedDate;
    
    @Column(name = "approved_by")
    private Long approvedBy;
    
    @Column(name = "approved_date")
    private LocalDate approvedDate;
    
    @Column(name = "processed_date")
    private LocalDate processedDate;
    
    @Column(name = "refund_mode", length = 50)
    private String refundMode;
    
    @Column(name = "transaction_reference", length = 100)
    private String transactionReference;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum RefundStatus {
        PENDING, APPROVED, PROCESSED, REJECTED
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
    public FeeRefund() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getStudentFeePaymentId() {
        return studentFeePaymentId;
    }
    
    public void setStudentFeePaymentId(Long studentFeePaymentId) {
        this.studentFeePaymentId = studentFeePaymentId;
    }
    
    public Long getStudentFeeAllocationId() {
        return studentFeeAllocationId;
    }
    
    public void setStudentFeeAllocationId(Long studentFeeAllocationId) {
        this.studentFeeAllocationId = studentFeeAllocationId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public BigDecimal getRefundAmount() {
        return refundAmount;
    }
    
    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }
    
    public String getRefundReason() {
        return refundReason;
    }
    
    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }
    
    public RefundStatus getRefundStatus() {
        return refundStatus;
    }
    
    public void setRefundStatus(RefundStatus refundStatus) {
        this.refundStatus = refundStatus;
    }
    
    public LocalDate getRequestedDate() {
        return requestedDate;
    }
    
    public void setRequestedDate(LocalDate requestedDate) {
        this.requestedDate = requestedDate;
    }
    
    public Long getApprovedBy() {
        return approvedBy;
    }
    
    public void setApprovedBy(Long approvedBy) {
        this.approvedBy = approvedBy;
    }
    
    public LocalDate getApprovedDate() {
        return approvedDate;
    }
    
    public void setApprovedDate(LocalDate approvedDate) {
        this.approvedDate = approvedDate;
    }
    
    public LocalDate getProcessedDate() {
        return processedDate;
    }
    
    public void setProcessedDate(LocalDate processedDate) {
        this.processedDate = processedDate;
    }
    
    public String getRefundMode() {
        return refundMode;
    }
    
    public void setRefundMode(String refundMode) {
        this.refundMode = refundMode;
    }
    
    public String getTransactionReference() {
        return transactionReference;
    }
    
    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
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