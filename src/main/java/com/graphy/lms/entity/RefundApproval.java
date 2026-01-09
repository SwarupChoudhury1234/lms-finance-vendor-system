package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "refund_approvals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundApproval {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_refund_id", nullable = false)
    private FeeRefund feeRefund;
    
    @Column(name = "requested_by", nullable = false)
    private Long requestedBy;
    
    @Column(name = "requested_date", nullable = false)
    private LocalDate requestedDate;
    
    @Column(name = "approval_level")
    private Integer approvalLevel = 1; // 1: Accountant, 2: Manager, 3: Director
    
    @Column(name = "current_approver")
    private Long currentApprover;
    
    @Column(name = "status", length = 20)
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED
    
    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;
    
    @Column(name = "approved_date")
    private LocalDate approvedDate;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (requestedDate == null) {
            requestedDate = LocalDate.now();
        }
    }
}