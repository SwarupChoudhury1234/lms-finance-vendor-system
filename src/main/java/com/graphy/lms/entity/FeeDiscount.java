package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fee_discounts")
public class FeeDiscount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "user_id", nullable = false) private Long userId;
    
    @ManyToOne
    @JoinColumn(name = "fee_structure_id", nullable = false)
    private FeeStructure feeStructure;
    
    @Column(name = "discount_type") private String discountType;
    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2) private BigDecimal discountValue;
    private String reason;
    @Column(name = "approved_by") private Long approvedBy;
    @Column(name = "approved_date") private LocalDate approvedDate;
    @Column(name = "auto_apply") private Boolean autoApply = false;
    @Column(name = "valid_from") private LocalDate validFrom;
    @Column(name = "valid_to") private LocalDate validTo;
    @Column(name = "is_active") private Boolean isActive = true;
    @Column(name = "created_at") private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "updated_at") private LocalDateTime updatedAt;
    
    public FeeDiscount() {}
    
    // Getters/Setters
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; } public void setUserId(Long userId) { this.userId = userId; }
    public FeeStructure getFeeStructure() { return feeStructure; } public void setFeeStructure(FeeStructure feeStructure) { this.feeStructure = feeStructure; }
    public String getDiscountType() { return discountType; } public void setDiscountType(String discountType) { this.discountType = discountType; }
    public BigDecimal getDiscountValue() { return discountValue; } public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue; }
    public String getReason() { return reason; } public void setReason(String reason) { this.reason = reason; }
    public Long getApprovedBy() { return approvedBy; } public void setApprovedBy(Long approvedBy) { this.approvedBy = approvedBy; }
    public LocalDate getApprovedDate() { return approvedDate; } public void setApprovedDate(LocalDate approvedDate) { this.approvedDate = approvedDate; }
    public Boolean getAutoApply() { return autoApply; } public void setAutoApply(Boolean autoApply) { this.autoApply = autoApply; }
    public LocalDate getValidFrom() { return validFrom; } public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }
    public LocalDate getValidTo() { return validTo; } public void setValidTo(LocalDate validTo) { this.validTo = validTo; }
    public Boolean getIsActive() { return isActive; } public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}