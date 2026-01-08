package com.graphy.lms.entity;

import javax.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_fee_allocations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentFeeAllocation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_structure_id", nullable = false)
    private FeeStructure feeStructure;
    
    @Column(name = "original_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal originalAmount;
    
    @Column(name = "discount_applied", precision = 12, scale = 2)
    private BigDecimal discountApplied = BigDecimal.ZERO;
    
    @Column(name = "payable_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal payableAmount;
    
    @Column(name = "advance_paid", precision = 12, scale = 2)
    private BigDecimal advancePaid = BigDecimal.ZERO;
    
    @Column(name = "total_paid", precision = 12, scale = 2)
    private BigDecimal totalPaid = BigDecimal.ZERO;
    
    @Column(name = "remaining_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal remainingAmount;
    
    @Column(name = "installment_count")
    private Integer installmentCount = 1;
    
    @Column(name = "per_installment_amount", precision = 12, scale = 2)
    private BigDecimal perInstallmentAmount;
    
    @Column(name = "custom_installments", columnDefinition = "JSON")
    private String customInstallments;
    
    @Column(name = "payment_plan", columnDefinition = "JSON")
    private String paymentPlan;
    
    @Column(name = "status", length = 20)
    private String status = "PENDING"; // PENDING, PARTIAL_PAID, PAID, OVERDUE
    
    @Column(name = "due_date")
    private LocalDate dueDate;
    
    @Column(name = "allocation_date")
    private LocalDate allocationDate;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "updated_by")
    private Long updatedBy;
    
    // Additional fields
    @Column(name = "next_reminder_date")
    private LocalDate nextReminderDate;
    
    @Column(name = "reminder_count")
    private Integer reminderCount = 0;
    
    @Column(name = "total_late_fee", precision = 12, scale = 2)
    private BigDecimal totalLateFee = BigDecimal.ZERO;
    
    // Links to new tables
    @Column(name = "installment_plan_template_id")
    private Long installmentPlanTemplateId;
    
    @Column(name = "payment_link_id")
    private Long paymentLinkId;
    
    @Column(name = "late_fee_rule_id")
    private Long lateFeeRuleId;
    
    @Column(name = "invoice_template_id")
    private Long invoiceTemplateId;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (allocationDate == null) {
            allocationDate = LocalDate.now();
        }
        if (remainingAmount == null && payableAmount != null && advancePaid != null) {
            remainingAmount = payableAmount.subtract(advancePaid);
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        // Auto-calculate remaining amount
        if (payableAmount != null && totalPaid != null) {
            remainingAmount = payableAmount.subtract(totalPaid);
        }
    }
}