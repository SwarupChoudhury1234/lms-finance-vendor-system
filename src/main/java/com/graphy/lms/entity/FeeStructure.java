package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "fee_structures")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeeStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relationship to Master Data (FeeType)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fee_type_id", nullable = false)
    private FeeType feeType;

    @Column(nullable = false)
    private String academicYear;

    // Critical for Access Matrix: Used to filter by Faculty/Student roles
    @Column(nullable = false)
    private Long courseId;

    @Column(nullable = false)
    private Double totalAmount;

    // Mentor Rule: Set automatically on record creation
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // Mentor Rule: Reflects the "Fetch-then-Update" pattern in the response
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}