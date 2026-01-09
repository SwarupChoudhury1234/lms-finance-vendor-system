package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_links")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentLink {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_fee_allocation_id", nullable = false)
    private StudentFeeAllocation studentFeeAllocation;
    
    @Column(name = "unique_token", nullable = false, unique = true, length = 100)
    private String uniqueToken;
    
    @Column(name = "link_url", nullable = false, length = 500)
    private String linkUrl;
    
    @Column(name = "status", length = 20)
    private String status = "ACTIVE"; // ACTIVE, USED, EXPIRED
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Column(name = "payment_proof_path", length = 500)
    private String paymentProofPath; // Screenshot upload path
    
    @Column(name = "upi_transaction_id", length = 100)
    private String upiTransactionId;
    
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (expiresAt == null) {
            expiresAt = LocalDateTime.now().plusDays(7); // Default 7 days expiry
        }
    }
}