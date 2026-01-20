package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.ToString;

@Entity
@ToString
@Table(name = "payment_alternatives")
public class PaymentAlternative {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "alternative_name", nullable = false, length = 100)
    private String alternativeName;
    
    @Column(name = "number_of_installments", nullable = false)
    private Integer numberOfInstallments;
    
    @Column(length = 255)
    private String description;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public PaymentAlternative() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getAlternativeName() {
        return alternativeName;
    }
    
    public void setAlternativeName(String alternativeName) {
        this.alternativeName = alternativeName;
    }
    
    public Integer getNumberOfInstallments() {
        return numberOfInstallments;
    }
    
    public void setNumberOfInstallments(Integer numberOfInstallments) {
        this.numberOfInstallments = numberOfInstallments;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Long getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}