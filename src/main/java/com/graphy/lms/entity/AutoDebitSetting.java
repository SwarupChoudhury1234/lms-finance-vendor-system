package com.graphy.lms.entity;

import javax.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "auto_debit_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutoDebitSetting {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "payment_method", length = 50)
    private String paymentMethod; // BANK_ACCOUNT, CREDIT_CARD, DEBIT_CARD
    
    @Column(name = "account_last_four", length = 4)
    private String accountLastFour;
    
    @Column(name = "is_active")
    private Boolean isActive = false;
    
    @Column(name = "next_debit_date")
    private LocalDate nextDebitDate;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Additional fields for real debit
    @Column(name = "bank_name", length = 100)
    private String bankName;
    
    @Column(name = "account_number_encrypted", length = 255)
    private String accountNumberEncrypted;
    
    @Column(name = "ifsc_code", length = 20)
    private String ifscCode;
    
    @Column(name = "authorization_token", length = 500)
    private String authorizationToken;
    
    @Column(name = "authorization_valid_until")
    private LocalDate authorizationValidUntil;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}