package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "fee_receipts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeeReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Changed to 'id' to maintain consistency with other entities

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_id", nullable = false)
    private StudentFeePayment studentFeePayment;

    @Column(unique = true, nullable = false)
    private String receiptNumber;

    // Mentor Rule: Set automatically on record creation (POST)
    @Column(updatable = false)
    private LocalDateTime generatedAt;

    // Mentor Rule: Required for consistency across all 7 tables
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.generatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}