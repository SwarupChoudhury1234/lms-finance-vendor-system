package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fee_structures")
public class FeeStructure {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    
    @ManyToOne
    @JoinColumn(name = "fee_type_id", nullable = false)
    private FeeType feeType;
    
    @Column(name = "academic_year", nullable = false) private String academicYear;
    @Column(name = "course_id", nullable = false) private Long courseId;
    @Column(name = "batch_id") private Long batchId;
    @Column(name = "student_category") private String studentCategory;
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2) private BigDecimal totalAmount;
    @Column(name = "payment_schedule") private String paymentSchedule;
    private String currency = "INR";
    @Column(name = "conversion_rate", precision = 10, scale = 4) private BigDecimal conversionRate = BigDecimal.ONE;
    @Column(name = "is_active") private Boolean isActive = true;
    @Column(name = "created_at") private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "updated_at") private LocalDateTime updatedAt;
    
    public FeeStructure() {}
    
    // Getters/Setters (ALL FIELDS)
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public FeeType getFeeType() { return feeType; } public void setFeeType(FeeType feeType) { this.feeType = feeType; }
    public String getAcademicYear() { return academicYear; } public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    public Long getCourseId() { return courseId; } public void setCourseId(Long courseId) { this.courseId = courseId; }
    public Long getBatchId() { return batchId; } public void setBatchId(Long batchId) { this.batchId = batchId; }
    public String getStudentCategory() { return studentCategory; } public void setStudentCategory(String studentCategory) { this.studentCategory = studentCategory; }
    public BigDecimal getTotalAmount() { return totalAmount; } public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getPaymentSchedule() { return paymentSchedule; } public void setPaymentSchedule(String paymentSchedule) { this.paymentSchedule = paymentSchedule; }
    public String getCurrency() { return currency; } public void setCurrency(String currency) { this.currency = currency; }
    public BigDecimal getConversionRate() { return conversionRate; } public void setConversionRate(BigDecimal conversionRate) { this.conversionRate = conversionRate; }
    public Boolean getIsActive() { return isActive; } public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}