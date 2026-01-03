package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.Data;              // Missing Import 1: Resolves @Data
import java.time.LocalDate;
import java.time.LocalDateTime;   // Missing Import 2: Resolves LocalDateTime

@Entity
@Table(name = "fee_refunds")
@Data // Automatically generates getters, setters, and other boilerplate
public class FeeRefund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_fee_payment_id")
    private StudentFeePayment studentFeePayment;

    private Double refundAmount;

    private LocalDate refundDate;

    private String reason;

    // Required for the "all columns" Postman rule
    private LocalDateTime createdAt = LocalDateTime.now();

    // Mentor Rule: Set to null on POST, updated automatically on PUT
    private LocalDateTime updatedAt;
}