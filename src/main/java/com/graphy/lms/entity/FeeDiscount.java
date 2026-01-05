package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fee_discounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeeDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Critical for Access Matrix: Used for GET (self/child) filtering
    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fee_structure_id", nullable = false)
    private FeeStructure feeStructure;

    // 'PERCENTAGE' or 'FLAT'
    @Column(nullable = false)
    private String discountType; 

    @Column(nullable = false)
    private Double discountValue;

    private String reason;

    // Ties back to the Admin actorId who approved it
    private Long approvedBy;

    private LocalDate approvedDate;

    // Mentor Rule: Set automatically on record creation
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // Mentor Rule: Updated during PUT methods
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.approvedDate == null) {
            this.approvedDate = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}