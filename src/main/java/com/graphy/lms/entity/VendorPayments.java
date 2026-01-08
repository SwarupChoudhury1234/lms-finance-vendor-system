package com.graphy.lms.entity;

import javax.persistence.*; 
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "vendor_payments")
@Data
public class VendorPayments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendors vendor;

    @ManyToOne
    @JoinColumn(name = "contract_id")
    private VendorContracts contract;

    private BigDecimal paymentAmount;
    private LocalDate paymentDate;
    private String paymentMode;      // UPI / Bank / Cash
    private String paymentStatus;    // PAID / PENDING
    private String referenceNumber;
    
    private LocalDateTime createdAt = LocalDateTime.now();
}