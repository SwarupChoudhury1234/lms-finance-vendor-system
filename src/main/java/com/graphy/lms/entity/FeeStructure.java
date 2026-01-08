package com.graphy.lms.entity;

import javax.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fee_structures")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeStructure {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_type_id", nullable = false)
    private FeeType feeType;
    
    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear;
    
    @Column(name = "course_id", nullable = false)
    private Long courseId;
    
    @Column(name = "batch_id")
    private Long batchId;
    
    @Column(name = "student_category", length = 50)
    private String studentCategory;
    
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(name = "payment_schedule", length = 20)
    private String paymentSchedule; // MONTHLY, QUARTERLY, YEARLY
    
    @Column(name = "currency", length = 3)
    private String currency = "INR";
    
    @Column(name = "conversion_rate", precision = 10, scale = 4)
    private BigDecimal conversionRate = BigDecimal.ONE;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Additional fields for real-world usage
    @Column(name = "gst_applicable")
    private Boolean gstApplicable = true;
    
    @Column(name = "gst_percentage", precision = 5, scale = 2)
    private BigDecimal gstPercentage = new BigDecimal("18.00");
    
    @Column(name = "late_fee_per_day", precision = 10, scale = 2)
    private BigDecimal lateFeePerDay = new BigDecimal("50.00");
    
    @Column(name = "grace_period_days")
    private Integer gracePeriodDays = 5;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}