package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.Data; // Required for @Data
import java.time.LocalDate;
import java.time.LocalDateTime; // Required for LocalDateTime

@Entity
@Table(name = "fee_discounts")
@Data // Generates getters, setters, toString, equals, and hashcode automatically
public class FeeDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne
    @JoinColumn(name = "fee_structure_id")
    private FeeStructure feeStructure;

    // Check constraint logic: 'PERCENTAGE' or 'FLAT'
    private String discountType; 

    private Double discountValue;

    private String reason;

    private Long approvedBy;

    private LocalDate approvedDate;

    // Automated Timestamp: Set to current time on record creation
    private LocalDateTime createdAt = LocalDateTime.now();

    // Mentor Rule: Null at creation, updated only during PUT methods
    private LocalDateTime updatedAt;
}