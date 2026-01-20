package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.ToString;

@Entity
@ToString
@Table(name = "fee_receipts")
public class FeeReceipt {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "payment_id", nullable = false)
    private Long paymentId;
    
    @Column(name = "receipt_number", unique = true, nullable = false, length = 100)
    private String receiptNumber;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "receipt_pdf_url", length = 500)
    private String receiptPdfUrl;
    
    @Column(name = "generated_at", updatable = false)
    private LocalDateTime generatedAt;
    
    @PrePersist
    protected void onCreate() {
        generatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public FeeReceipt() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getPaymentId() {
        return paymentId;
    }
    
    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }
    
    public String getReceiptNumber() {
        return receiptNumber;
    }
    
    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getReceiptPdfUrl() {
        return receiptPdfUrl;
    }
    
    public void setReceiptPdfUrl(String receiptPdfUrl) {
        this.receiptPdfUrl = receiptPdfUrl;
    }
    
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
    
    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
}