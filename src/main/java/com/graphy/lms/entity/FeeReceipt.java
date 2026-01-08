package com.graphy.lms.entity;

import javax.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fee_receipts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeReceipt {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_fee_payment_id", nullable = false, unique = true)
    private StudentFeePayment studentFeePayment;
    
    @Column(name = "receipt_number", nullable = false, unique = true, length = 100)
    private String receiptNumber;
    
    @Column(name = "receipt_date")
    private LocalDate receiptDate;
    
    @Column(name = "pdf_file_path", length = 500)
    private String pdfFilePath;
    
    @Column(name = "email_sent")
    private Boolean emailSent = false;
    
    @Column(name = "email_sent_at")
    private LocalDateTime emailSentAt;
    
    @Column(name = "generated_by")
    private Long generatedBy;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (receiptDate == null) {
            receiptDate = LocalDate.now();
        }
        // Auto-generate receipt number
        if (receiptNumber == null) {
            receiptNumber = "REC" + System.currentTimeMillis();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}