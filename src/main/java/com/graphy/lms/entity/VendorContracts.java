package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "vendor_contracts")
@Data
public class VendorContracts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendors vendor;

    private String contractTitle;
    private LocalDate startDate;
    private LocalDate endDate;
    private String contractStatus = "ACTIVE"; // ACTIVE / EXPIRED / TERMINATED
    
    private LocalDateTime createdAt = LocalDateTime.now();
}