package com.graphy.lms.entity;

import javax.persistence.*; 
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "asset_returns")
@Data
public class AssetsReturn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable = false)
    private AssetsAssigned assignment;

    private Integer returnedQuantity;
    private LocalDate returnDate;
    private String conditionStatus;  // GOOD / DAMAGED / LOST 
    private String remarks;
    private LocalDateTime createdAt = LocalDateTime.now();
}