package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.Data;              // Fixed: Added missing Lombok import
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_fee_payments")
@Data // Generates getters, setters, toString, equals, and hashCode
public class StudentFeePayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_fee_allocation_id", nullable = false)
    private StudentFeeAllocation studentFeeAllocation;

    private Double paidAmount;

    private LocalDate paymentDate;

    private String paymentMode;

    private String transactionReference;

    // Set automatically on POST (Creation)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Mentor Rule: Null at creation, automatically refreshed on PUT (Update)
    private LocalDateTime updatedAt;
}