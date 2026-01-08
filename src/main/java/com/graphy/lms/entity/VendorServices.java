package com.graphy.lms.entity;

import javax.persistence.*; 
import lombok.Data;

@Entity
@Table(name = "vendor_services")
@Data
public class VendorServices {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendors vendor;

    private String serviceName;        // Stationery / Hardware / Books
    private String serviceDescription;
    private String status = "ACTIVE";
}