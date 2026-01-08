package com.graphy.lms.entity;

import javax.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_installments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentInstallment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_fee_allocation_id", nullable = false)
    private StudentFeeAllocation studentFeeAllocation;
    
    @Column(name = "installment_number", nullable = false)
    private Integer installmentNumber;
    
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;
    
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "paid_amount", precision = 12, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;
    
    @Column(name = "late_fee", precision = 12, scale = 2)
    private BigDecimal lateFee = BigDecimal.ZERO;
    
    @Column(name = "status", length = 20)
    private String status = "PENDING"; // PENDING, PAID, OVERDUE
    
    @Column(name = "adjustment_notes", columnDefinition = "TEXT")
    private String adjustmentNotes;
    
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