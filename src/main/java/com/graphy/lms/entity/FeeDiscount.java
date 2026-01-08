package com.graphy.lms.entity;

import javax.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fee_discounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeDiscount {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_structure_id", nullable = false)
    private FeeStructure feeStructure;
    
    @Column(name = "discount_type", length = 20)
    private String discountType; // PERCENTAGE, FIXED
    
    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;
    
    @Column(name = "reason", length = 255)
    private String reason;
    
    @Column(name = "approved_by")
    private Long approvedBy;
    
    @Column(name = "approved_date")
    private LocalDate approvedDate;
    
    @Column(name = "auto_apply")
    private Boolean autoApply = false;
    
    @Column(name = "valid_from")
    private LocalDate validFrom;
    
    @Column(name = "valid_to")
    private LocalDate validTo;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Link to allocation
    @Column(name = "fee_allocation_id")
    private Long feeAllocationId;
    
    // For tracking discount hierarchy
    @Column(name = "discount_category", length = 50)
    private String discountCategory; // MERIT, SIBLING, EARLY_BIRD, etc.
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (approvedDate == null && approvedBy != null) {
            approvedDate = LocalDate.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}