package com.graphy.lms.entity;

import javax.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "auto_debit_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutoDebitTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auto_debit_setting_id", nullable = false)
    private AutoDebitSetting autoDebitSetting;
    
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;
    
    @Column(name = "status", length = 20)
    private String status = "PENDING"; // PENDING, SUCCESS, FAILED
    
    @Column(name = "failure_reason", length = 255)
    private String failureReason;
    
    @Column(name = "retry_count")
    private Integer retryCount = 0;
    
    @Column(name = "next_retry_date")
    private LocalDate nextRetryDate;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (transactionDate == null) {
            transactionDate = LocalDate.now();
        }
    }
}