package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_levels")
@Data
public class StockLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "inventory_id", nullable = false)
    private InventoryItem item;

    private Integer availableQuantity;
    private Integer lowStockThreshold;
    private LocalDateTime lastUpdated = LocalDateTime.now();
}