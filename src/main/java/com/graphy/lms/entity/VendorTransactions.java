package com.graphy.lms.entity;

import jakarta.persistence.*; 
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "vendor_transactions")
@Data
public class VendorTransactions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendors vendor;

    private String transactionType;
    private Long referenceId;
    private String remarks;
    private LocalDate transactionDate;
    
    private LocalDateTime createdAt = LocalDateTime.now();
}