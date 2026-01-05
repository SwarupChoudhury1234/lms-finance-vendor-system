package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
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

    // Link to the allocation (Financial Liability)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_fee_allocation_id", nullable = false)
    private StudentFeeAllocation studentFeeAllocation;

    @Column(nullable = false)
    private Double paidAmount;

    @Column(nullable = false)
    private LocalDate paymentDate;

    @Column(nullable = false)
    private String paymentMode; // e.g., UPI, CARD, CASH, NET_BANKING

    @Column(unique = true)
    private String transactionReference;

    // Set automatically on record creation
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /** * Mentor Rule: Although the matrix says UPDATE ❌ No one, 
     * we keep this field to satisfy the "All Columns" rule in Postman responses.
     */
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.paymentDate == null) {
            this.paymentDate = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}