package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_fee_allocations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentFeeAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fee_structure_id", nullable = false)
    private FeeStructure feeStructure;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private LocalDate allocationDate;

    // ADD THESE TWO FIELDS FOR MENTOR COMPLIANCE
    private String status;      // e.g., PENDING, PAID, PARTIAL
    private Double amountPaid;  // e.g., 5000.0

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.allocationDate == null) {
            this.allocationDate = LocalDate.now();
        }
        // Initialize amountPaid if null to show in response
        if (this.amountPaid == null) {
            this.amountPaid = 0.0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}