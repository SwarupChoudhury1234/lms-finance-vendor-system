package com.graphy.lms.entity;

import javax.persistence.*; 
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "vendors")
@Data
public class Vendors {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String vendorName;

    private String vendorType;     // Inventory / IT / Marketing / Content
    private String contactPerson;
    private String email;
    private String phone;
    private String address;
    private String status = "ACTIVE"; // ACTIVE / INACTIVE / BLOCKED
    
    private LocalDateTime createdAt = LocalDateTime.now();
}