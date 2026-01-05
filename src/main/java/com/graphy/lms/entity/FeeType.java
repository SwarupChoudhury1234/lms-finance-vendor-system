package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "fee_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeeType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    // Satisfies: Access Matrix Rule (GET Active only for Students/Parents)
    @Column(nullable = false)
    private Boolean isActive = true;

    // Mentor Rule: Set automatically on record creation
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // Mentor Rule: Updated during PUT methods to reflect "Fetch-then-Update"
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