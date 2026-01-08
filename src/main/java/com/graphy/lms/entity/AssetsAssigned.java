package com.graphy.lms.entity;

import javax.persistence.*; 
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "assets_assigned")
@Data
public class AssetsAssigned {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;          // student / faculty ID 
    private String userRole;      // STUDENT / FACULTY 

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private InventoryItem item;

    private Integer quantity;
    private LocalDate givenDate;
    private String status = "ACTIVE";
    private LocalDateTime createdAt = LocalDateTime.now();
}