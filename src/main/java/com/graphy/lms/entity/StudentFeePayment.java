package com.graphy.lms.entity;

import javax.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_fee_payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentFeePayment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_fee_allocation_id", nullable = false)
    private StudentFeeAllocation studentFeeAllocation;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_installment_id")
    private PaymentInstallment paymentInstallment;
    
    @Column(name = "paid_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal paidAmount;
    
    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;
    
    @Column(name = "payment_mode", length = 50)
    private String paymentMode; // CASH, CHEQUE, UPI, CARD, ONLINE
    
    @Column(name = "transaction_reference", length = 100)
    private String transactionReference;
    
    @Column(name = "collected_by")
    private Long collectedBy;
    
    @Column(name = "receipt_generated")
    private Boolean receiptGenerated = false;
    
    @Column(name = "status", length = 20)
    private String status = "SUCCESS"; // SUCCESS, FAILED, PENDING
    
    @Column(name = "remarks", length = 255)
    private String remarks;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "updated_by")
    private Long updatedBy;
    
    // Additional fields for real-world
    @Column(name = "bank_name", length = 100)
    private String bankName;
    
    @Column(name = "cheque_number", length = 50)
    private String chequeNumber;
    
    @Column(name = "transaction_type", length = 20)
    private String transactionType = "FEE_PAYMENT";
    
    @Column(name = "gst_amount", precision = 12, scale = 2)
    private BigDecimal gstAmount = BigDecimal.ZERO;
    
    @Column(name = "tds_amount", precision = 12, scale = 2)
    private BigDecimal tdsAmount = BigDecimal.ZERO;
    
    @Column(name = "net_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal netAmount;
    
    // Online payment fields
    @Column(name = "payment_gateway", length = 50)
    private String paymentGateway; // RAZORPAY, PAYTM, CCAVENUE
    
    @Column(name = "gateway_transaction_id", length = 100)
    private String gatewayTransactionId;
    
    @Column(name = "payment_link_id")
    private Long paymentLinkId;
    
    @Column(name = "invoice_number", length = 100)
    private String invoiceNumber;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (paymentDate == null) {
            paymentDate = LocalDate.now();
        }
        if (netAmount == null) {
            netAmount = paidAmount;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}