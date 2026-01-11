package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fee_receipts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeeReceipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "payment_id", nullable = false)
    private Long paymentId;
    
    @Column(name = "receipt_number", unique = true, nullable = false, length = 100)
    private String receiptNumber;
    
    @Column(name = "student_name", length = 100)
    private String studentName;
    
    @Column(name = "course_name", length = 100)
    private String courseName;
    
    @Column(name = "amount_paid", precision = 10, scale = 2)
    private BigDecimal amountPaid;
    
    @Column(name = "payment_mode", length = 50)
    private String paymentMode;
    
    @Column(name = "pdf_path", length = 255)
    private String pdfPath;
    
    @Column(name = "email_sent")
    private Boolean emailSent = false;
    
    @Column(name = "email_sent_at")
    private LocalDateTime emailSentAt;
    
    @Column(name = "generated_at", updatable = false)
    private LocalDateTime generatedAt;
    
    @PrePersist
    protected void onCreate() {
        generatedAt = LocalDateTime.now();
    }
}