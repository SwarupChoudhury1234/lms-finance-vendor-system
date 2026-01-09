package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fee_receipts")
public class FeeReceipt {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    
    @ManyToOne
    @JoinColumn(name = "student_fee_payment_id", nullable = false)
    private StudentFeePayment studentFeePayment;
    
    @Column(name = "receipt_number", nullable = false, unique = true) private String receiptNumber;
    @Column(name = "receipt_date") private LocalDate receiptDate;
    @Column(name = "pdf_file_path") private String pdfFilePath;
    @Column(name = "email_sent") private Boolean emailSent = false;
    @Column(name = "email_sent_at") private LocalDateTime emailSentAt;
    @Column(name = "generated_by") private Long generatedBy;
    @Column(name = "created_at") private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "updated_at") private LocalDateTime updatedAt;
    
    public FeeReceipt() {}
    
    // Getters/Setters
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public StudentFeePayment getStudentFeePayment() { return studentFeePayment; } public void setStudentFeePayment(StudentFeePayment studentFeePayment) { this.studentFeePayment = studentFeePayment; }
    public String getReceiptNumber() { return receiptNumber; } public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }
    public LocalDate getReceiptDate() { return receiptDate; } public void setReceiptDate(LocalDate receiptDate) { this.receiptDate = receiptDate; }
    public String getPdfFilePath() { return pdfFilePath; } public void setPdfFilePath(String pdfFilePath) { this.pdfFilePath = pdfFilePath; }
    public Boolean getEmailSent() { return emailSent; } public void setEmailSent(Boolean emailSent) { this.emailSent = emailSent; }
    public LocalDateTime getEmailSentAt() { return emailSentAt; } public void setEmailSentAt(LocalDateTime emailSentAt) { this.emailSentAt = emailSentAt; }
    public Long getGeneratedBy() { return generatedBy; } public void setGeneratedBy(Long generatedBy) { this.generatedBy = generatedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}