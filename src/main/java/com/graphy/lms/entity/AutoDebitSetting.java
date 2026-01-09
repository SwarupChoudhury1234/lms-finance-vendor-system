package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "auto_debit_settings")
public class AutoDebitSetting {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;
    
    @Column(name = "user_id", nullable = false) 
    private Long userId;
    
    @Column(name = "payment_method") 
    private String paymentMethod;
    
    @Column(name = "account_last_four") 
    private String accountLastFour;
    
    @Column(name = "is_active") 
    private Boolean isActive = false;
    
    @Column(name = "next_debit_date") 
    private LocalDate nextDebitDate;
    
    @Column(name = "created_at") 
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at") 
    private LocalDateTime updatedAt;
    
    // Empty constructor (REQUIRED by JPA)
    public AutoDebitSetting() {
    }
    
    // Parameterized constructor
    public AutoDebitSetting(Long userId, String paymentMethod, String accountLastFour) {
        this.userId = userId;
        this.paymentMethod = paymentMethod;
        this.accountLastFour = accountLastFour;
        this.isActive = false;
    }
    
    // Getters and Setters for ALL fields
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
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getAccountLastFour() {
        return accountLastFour;
    }
    
    public void setAccountLastFour(String accountLastFour) {
        this.accountLastFour = accountLastFour;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDate getNextDebitDate() {
        return nextDebitDate;
    }
    
    public void setNextDebitDate(LocalDate nextDebitDate) {
        this.nextDebitDate = nextDebitDate;
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
    
    // toString() method for debugging
    @Override
    public String toString() {
        return "AutoDebitSetting{" +
                "id=" + id +
                ", userId=" + userId +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", accountLastFour='" + accountLastFour + '\'' +
                ", isActive=" + isActive +
                ", nextDebitDate=" + nextDebitDate +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}