package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "fee_refunds")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeeRefund {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "student_fee_payment_id", nullable = false)
    private Long studentFeePaymentId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "refund_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal refundAmount;
    
    @Column(name = "refund_date", nullable = false)
    private LocalDate refundDate;
    
    @Column(name = "refund_mode", length = 50)
    private String refundMode; // CASH, BANK_TRANSFER, CHEQUE
    
    @Column(length = 255)
    private String reason;
    
    @Column(name = "requested_by")
    private Long requestedBy;
    
    @Column(name = "approved_by")
    private Long approvedBy;
    
    @Column(name = "approval_date")
    private LocalDate approvalDate;
    
    @Column(length = 20)
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED, COMPLETED
    
    @Column(name = "transaction_reference", length = 100)
    private String transactionReference;
    
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
}