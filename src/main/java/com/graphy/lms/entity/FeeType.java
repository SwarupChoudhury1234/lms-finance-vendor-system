package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.Data;              // Missing Import 1: Resolves @Data
import java.time.LocalDateTime;   // Missing Import 2: Resolves LocalDateTime

@Entity
@Table(name = "fee_types")
@Data // Automatically generates Getters, Setters, Equals, HashCode, and ToString
public class FeeType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private Boolean isActive = true;

    // Set automatically on record creation
    private LocalDateTime createdAt = LocalDateTime.now();

    // Mentor Rule: Null on POST, updated automatically during PUT methods
    private LocalDateTime updatedAt;
}