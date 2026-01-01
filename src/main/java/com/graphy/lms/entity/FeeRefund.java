package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "fee_refunds")
public class FeeRefund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_fee_payment_id", nullable = false)
    private StudentFeePayment studentFeePayment;

    @Column(name = "refund_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal refundAmount; 

    @Column(name = "refund_date", nullable = false)
    private LocalDate refundDate;

    private String reason;

    // --- GETTERS AND SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public StudentFeePayment getStudentFeePayment() { return studentFeePayment; }
    public void setStudentFeePayment(StudentFeePayment sfp) { this.studentFeePayment = sfp; }
    public BigDecimal getRefundAmount() { return refundAmount; } 
    public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }
    public LocalDate getRefundDate() { return refundDate; }
    public void setRefundDate(LocalDate refundDate) { this.refundDate = refundDate; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}