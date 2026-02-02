package com.graphy.lms.entity;

import com.graphy.lms.entity.enums.ApprovalStatus;
import com.graphy.lms.entity.enums.StockTransactionType;
import com.graphy.lms.entity.enums.TransactionSourceType;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_transactions")
@Data
public class StockTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // CORE IDENTIFIERS
    // =========================

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    // =========================
    // TRANSACTION SEMANTICS
    // =========================

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private StockTransactionType transactionType;

    @Column(name = "quantity", nullable = false)
    private Integer quantity; // Always POSITIVE

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    // =========================
    // SOURCE CONTEXT (WHO / WHAT CAUSED IT)
    // =========================

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type")
    private TransactionSourceType sourceType;

    @Column(name = "source_id")
    private Long sourceId; // vendorId / studentId / batchId / etc.

    // =========================
    // APPROVAL FLOW
    // =========================

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status")
    private ApprovalStatus approvalStatus = ApprovalStatus.NOT_REQUIRED;

    // =========================
    // LINKING & AUDIT
    // =========================

    @Column(name = "reference_id")
    private Long referenceId; // procurementId / assignmentId / returnId

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "performed_by_user_id")
    private Long performedByUserId;

    @Column(name = "performed_by_role")
    private String performedByRole;

    // =========================
    // AUDIT TIMESTAMPS
    // =========================

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.transactionDate == null) {
            this.transactionDate = LocalDate.now();
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
