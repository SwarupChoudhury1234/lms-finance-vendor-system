package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_fee_payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentFeePayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "student_fee_allocation_id", nullable = false)
    private Long studentFeeAllocationId;
    
    @Column(name = "installment_plan_id")
    private Long installmentPlanId;
    
    @Column(name = "paid_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal paidAmount;
    
    @Column(name = "late_fee_paid", precision = 10, scale = 2)
    private BigDecimal lateFeePaid = BigDecimal.ZERO;
    
    @Column(name = "total_paid", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPaid;
    
    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;
    
    @Column(name = "payment_mode", nullable = false, length = 50)
    private String paymentMode; // CASH, CARD, BANK_TRANSFER, UPI, ONLINE
    
    @Column(name = "transaction_reference", length = 100)
    private String transactionReference;
    
    @Column(name = "payment_gateway", length = 50)
    private String paymentGateway;
    
    @Column(name = "payment_status", length = 20)
    private String paymentStatus = "SUCCESS"; // SUCCESS, FAILED, PENDING
    
    @Column(name = "collected_by")
    private Long collectedBy;
    
    @Column(length = 255)
    private String remarks;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        totalPaid = paidAmount.add(lateFeePaid != null ? lateFeePaid : BigDecimal.ZERO);
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}