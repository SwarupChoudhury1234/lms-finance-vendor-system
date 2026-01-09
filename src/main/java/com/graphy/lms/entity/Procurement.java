package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "procurement")
@Data
public class Procurement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private InventoryItem item;

    private Long vendorId;   // comes from Vendor module 
    private Integer quantity;
    private LocalDate purchaseDate;
    private BigDecimal cost;
    private LocalDateTime createdAt = LocalDateTime.now();
}