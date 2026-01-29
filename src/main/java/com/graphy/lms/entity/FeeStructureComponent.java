package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "fee_structure_components")
public class FeeStructureComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link back to the parent Structure
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_structure_id", nullable = false)
    @JsonBackReference // Prevents infinite JSON loops
    private FeeStructure feeStructure;

    // The Specific Fee Type (e.g., ID for "Lab Fee")
    @Column(name = "fee_type_id", nullable = false)
    private Long feeTypeId;

    // The Amount for this specific component
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    // Constructor
    public FeeStructureComponent() {}
    
    public FeeStructureComponent(Long feeTypeId, BigDecimal amount) {
        this.feeTypeId = feeTypeId;
        this.amount = amount;
    }
}