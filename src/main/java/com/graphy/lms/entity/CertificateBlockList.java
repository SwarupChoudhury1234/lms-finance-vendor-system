package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "certificate_block_list")
public class CertificateBlockList {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "blocked_reason", length = 500)
    private String blockedReason;
    
    @Column(name = "pending_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal pendingAmount;
    
    @Column(name = "blocked_by")
    private Long blockedBy;
    
    @Column(name = "blocked_date")
    private LocalDate blockedDate;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (blockedDate == null) {
            blockedDate = LocalDate.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public CertificateBlockList() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getBlockedReason() {
        return blockedReason;
    }
    
    public void setBlockedReason(String blockedReason) {
        this.blockedReason = blockedReason;
    }
    
    public BigDecimal getPendingAmount() {
        return pendingAmount;
    }
    
    public void setPendingAmount(BigDecimal pendingAmount) {
        this.pendingAmount = pendingAmount;
    }
    
    public Long getBlockedBy() {
        return blockedBy;
    }
    
    public void setBlockedBy(Long blockedBy) {
        this.blockedBy = blockedBy;
    }
    
    public LocalDate getBlockedDate() {
        return blockedDate;
    }
    
    public void setBlockedDate(LocalDate blockedDate) {
        this.blockedDate = blockedDate;
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