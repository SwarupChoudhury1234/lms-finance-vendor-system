package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.Data;              // Fixed: Import for @Data
import java.time.LocalDateTime;   // Fixed: Import for LocalDateTime

@Entity
@Table(name = "fee_structures")
@Data // Automatically generates all getters, setters, and required methods
public class FeeStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fee_type_id", nullable = false)
    private FeeType feeType;

    private String academicYear;

    // This ID is validated via the Finance-to-Academic backend handshake
    private Long courseId;

    private Double totalAmount;

    // Set automatically on POST
    private LocalDateTime createdAt = LocalDateTime.now();

    // Mentor Rule: Updated during PUT methods, otherwise null
    private LocalDateTime updatedAt;
}