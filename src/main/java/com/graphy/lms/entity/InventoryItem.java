package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_items")
@Data
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // CORE IDENTITY (NON-NEGOTIABLE)
    // =========================

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "sku", nullable = false, unique = true, updatable = false)
    private String sku;   // Immutable Item Code

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "unit_of_measure", nullable = false)
    private String unitOfMeasure; // pcs, license, hours

    @Column(name = "is_trackable", nullable = false)
    private Boolean isTrackable;

    @Column(name = "is_consumable", nullable = false)
    private Boolean isConsumable;

    // =========================
    // LMS CONTEXT (OPTIONAL BUT CRITICAL)
    // =========================

    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "batch_id")
    private Long batchId;

    // =========================
    // STOCK & FINANCE
    // =========================

    @Column(name = "total_quantity")
    private Integer totalQuantity; // Only meaningful if isTrackable = true

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "status")
    private String status;

    // =========================
    // AUDIT
    // =========================

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
