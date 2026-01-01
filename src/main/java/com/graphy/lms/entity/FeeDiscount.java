package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "fee_discounts")
public class FeeDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "fee_structure_id", nullable = false)
    private FeeStructure feeStructure;

    @Column(name = "discount_type")
    private String discountType; 

    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    private String reason;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_date")
    private LocalDate approvedDate;

    // --- GETTERS AND SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public FeeStructure getFeeStructure() { return feeStructure; }
    public void setFeeStructure(FeeStructure fs) { this.feeStructure = fs; }
    public String getDiscountType() { return discountType; }
    public void setDiscountType(String type) { this.discountType = type; }
    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal val) { this.discountValue = val; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Long getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Long approvedBy) { this.approvedBy = approvedBy; }
    public LocalDate getApprovedDate() { return approvedDate; }
    public void setApprovedDate(LocalDate date) { this.approvedDate = date; }
}