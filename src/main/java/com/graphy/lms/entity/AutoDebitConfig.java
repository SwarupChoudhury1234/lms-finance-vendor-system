package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.ToString;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "auto_debit_config")
@ToString
public class AutoDebitConfig {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "student_fee_allocation_id", nullable = false)
    private Long studentFeeAllocationId;
    
    @Column(name = "bank_account_number", length = 50)
    private String bankAccountNumber;
    
    @Column(name = "card_token", length = 255)
    private String cardToken;
    
    @Column(name = "payment_gateway", length = 50)
    private String paymentGateway;
    
    @Column(name = "auto_debit_day")
    private Integer autoDebitDay;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "consent_given")
    private Boolean consentGiven = false;
    
    @Column(name = "consent_date")
    private LocalDate consentDate;
    
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
    public AutoDebitConfig() {}
    
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
    
    public Long getStudentFeeAllocationId() {
        return studentFeeAllocationId;
    }
    
    public void setStudentFeeAllocationId(Long studentFeeAllocationId) {
        this.studentFeeAllocationId = studentFeeAllocationId;
    }
    
    public String getBankAccountNumber() {
        return bankAccountNumber;
    }
    
    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }
    
    public String getCardToken() {
        return cardToken;
    }
    
    public void setCardToken(String cardToken) {
        this.cardToken = cardToken;
    }
    
    public String getPaymentGateway() {
        return paymentGateway;
    }
    
    public void setPaymentGateway(String paymentGateway) {
        this.paymentGateway = paymentGateway;
    }
    
    public Integer getAutoDebitDay() {
        return autoDebitDay;
    }
    
    public void setAutoDebitDay(Integer autoDebitDay) {
        this.autoDebitDay = autoDebitDay;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Boolean getConsentGiven() {
        return consentGiven;
    }
    
    public void setConsentGiven(Boolean consentGiven) {
        this.consentGiven = consentGiven;
    }
    
    public LocalDate getConsentDate() {
        return consentDate;
    }
    
    public void setConsentDate(LocalDate consentDate) {
        this.consentDate = consentDate;
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