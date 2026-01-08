package com.graphy.lms.entity;

import javax.persistence.*; 
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

    @Column(nullable = false)
    private String itemName;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private InventoryCategory category;

    private Integer totalQuantity;
    private BigDecimal unitPrice;
    private String status = "ACTIVE";
    private LocalDateTime createdAt = LocalDateTime.now();
}