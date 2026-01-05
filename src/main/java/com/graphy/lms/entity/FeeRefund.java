package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
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

    // Link to the original payment being refunded
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_fee_payment_id", nullable = false)
    private StudentFeePayment studentFeePayment;

    @Column(nullable = false)
    private Double refundAmount;

    @Column(nullable = false)
    private LocalDate refundDate;

    @Column(nullable = false)
    private String reason;

    // Mentor Rule: Set automatically on record creation (POST)
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // Mentor Rule: Included for "All Columns" rule; refreshed if a PUT occurs
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.refundDate == null) {
            this.refundDate = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}