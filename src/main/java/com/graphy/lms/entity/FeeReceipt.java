package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.Data;              // Missing Import 1: For @Data
import java.time.LocalDateTime;   // Missing Import 2: For LocalDateTime

@Entity
@Table(name = "fee_receipts")
@Data 
public class FeeReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long receiptId;

    @OneToOne
    @JoinColumn(name = "payment_id")
    private StudentFeePayment studentFeePayment;

    private String receiptNumber;

    // Set automatically at the time of creation
    private LocalDateTime generatedAt = LocalDateTime.now();

    // Mentor Rule: Required for all tables. 
    // Will be null at POST and updated during system-triggered updates.
    private LocalDateTime updatedAt;
}