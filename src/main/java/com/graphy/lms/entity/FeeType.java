package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.ToString;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@ToString
@Table(name = "fee_types")
public class FeeType {
    
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Fee type name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    @Column(nullable = false, unique = true, length = 100)
    private String name;
    
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    @Column(length = 255)
    private String description;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
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
    public FeeType() {}
    
    public FeeType(String name, String description, Boolean isActive) {
        this.name = name;
        this.description = description;
        this.isActive = isActive;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
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