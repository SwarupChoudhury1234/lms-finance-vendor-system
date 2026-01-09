package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Data
public class Invoices {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private PurchaseOrders purchaseOrder;

    @Column(unique = true, nullable = false)
    private String invoiceNumber;
    
    private LocalDate billDate;
    private LocalDate dueDate;
    private BigDecimal invoiceAmount;
    private BigDecimal amountPaid;
    private BigDecimal balanceAmount;
    private String status = "UNPAID";
    private String currency = "INR";

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt; // Null by default
}