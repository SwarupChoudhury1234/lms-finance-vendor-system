package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.ToString;

@Entity
@ToString(exclude = "components") // Prevent infinite loop in logs
@Table(name = "fee_structures")
public class FeeStructure {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Kept as "Main Category" (Optional), but actual amounts are now in 'components'
    @Column(name = "fee_type_id") 
    private Long feeTypeId;
    
    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear;
    
    @Column(name = "course_id", nullable = false)
    private Long courseId;
    
    @Column(name = "batch_id")
    private Long batchId;
    
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(length = 10)
    private String currency = "INR";
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 🔴 NEW: Configuration for Automation
    @Column(name = "trigger_on_creation")
    private Boolean triggerOnCreation;

    // 🔴 NEW: Child Table Relationship for Fee Breakdown (Lab, Exam, Tuition)
    @OneToMany(mappedBy = "feeStructure", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Handles JSON serialization safely
    private List<FeeStructureComponent> components = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public FeeStructure() {}

    // 🔴 Helper Method to Add Components easily
    public void addComponent(FeeStructureComponent component) {
        components.add(component);
        component.setFeeStructure(this);
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getFeeTypeId() { return feeTypeId; }
    public void setFeeTypeId(Long feeTypeId) { this.feeTypeId = feeTypeId; }
    
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    
    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Boolean getTriggerOnCreation() { return triggerOnCreation; }
    public void setTriggerOnCreation(Boolean triggerOnCreation) { this.triggerOnCreation = triggerOnCreation; }

    public List<FeeStructureComponent> getComponents() { return components; }
    public void setComponents(List<FeeStructureComponent> components) { this.components = components; }
}