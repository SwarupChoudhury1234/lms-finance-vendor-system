package com.graphy.lms.entity;

import jakarta.persistence.*; 
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_categories")
@Data
public class InventoryCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String categoryName;

    private String description;
    private String status = "ACTIVE";
    private LocalDateTime createdAt = LocalDateTime.now();
}