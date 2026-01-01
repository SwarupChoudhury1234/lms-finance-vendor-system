package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "billing_transactions")
public class BillingTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionRef; // e.g., Invoice Number
    private Double amount;
    private String paymentMode; // CASH, ONLINE

    // --- MENTOR'S SNAPSHOT REQUIREMENTS ---
    private Double previousBalance; 
    private Double newBalance;

    private LocalDateTime transactionDate;

    // Getters and Setters...
}