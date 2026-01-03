package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.Data;              // Fixed: Added Lombok import
import java.time.LocalDate;
import java.time.LocalDateTime;  // Fixed: Added LocalDateTime import

@Entity
@Table(name = "student_fee_allocations")
@Data // Automatically generates getters, setters, and required boilerplate
public class StudentFeeAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne
    @JoinColumn(name = "fee_structure_id", nullable = false)
    private FeeStructure feeStructure;

    private LocalDate dueDate;

    private LocalDate allocationDate = LocalDate.now();

    // Required for "All Columns" rule in Postman response
    private LocalDateTime createdAt = LocalDateTime.now();

    // Mentor Rule: Null on creation (POST), refreshed on every update (PUT)
    private LocalDateTime updatedAt;
}