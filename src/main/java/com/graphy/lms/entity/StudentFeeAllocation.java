package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.math.BigDecimal;

@Entity
@Table(name = "student_fee_allocations")
public class StudentFeeAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "fee_structure_id", nullable = false)
    private FeeStructure feeStructure;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "allocation_date", updatable = false)
    private LocalDate allocationDate = LocalDate.now();

    @Column(name = "status")
    private String status; 
    
    @Column(name = "amount_paid", precision = 10, scale = 2)
    private BigDecimal amountPaid;

    // --- GETTERS AND SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public FeeStructure getFeeStructure() { return feeStructure; }
    public void setFeeStructure(FeeStructure feeStructure) { this.feeStructure = feeStructure; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getAllocationDate() { return allocationDate; }
    public void setAllocationDate(LocalDate allocationDate) { this.allocationDate = allocationDate; }

    public String getStatus() { return status; } 
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getAmountPaid() { return amountPaid; } 
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }
}