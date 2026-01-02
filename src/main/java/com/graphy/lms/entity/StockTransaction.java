package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_transactions")
@Data
public class StockTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private InventoryItem item;

    private String transactionType;
    private Integer quantity;
    private LocalDate transactionDate;
    private Long referenceId;
    private String remarks;
    
    // Snapshot fields for mentor requirement
    private Integer previousBalance;
    private Integer newBalance;

    private LocalDateTime createdAt = LocalDateTime.now();
}