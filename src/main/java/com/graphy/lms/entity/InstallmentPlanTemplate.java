package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "installment_plan_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstallmentPlanTemplate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name; // "3 Installment Plan", "5 Installment Plan"
    
    @Column(name = "number_of_installments", nullable = false)
    private Integer numberOfInstallments;
    
    @Column(name = "installment_percentages", columnDefinition = "JSON", nullable = false)
    private String installmentPercentages; // [40, 30, 30]
    
    @Column(name = "advance_percentage", precision = 5, scale = 2)
    private BigDecimal advancePercentage = new BigDecimal("20.00");
    
    @Column(name = "is_default")
    private Boolean isDefault = false;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}