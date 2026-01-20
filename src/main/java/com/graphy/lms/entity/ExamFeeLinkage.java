package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.ToString;

@Entity
@ToString
@Table(name = "exam_fee_linkage")
public class ExamFeeLinkage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "exam_id", nullable = false)
    private Long examId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "student_fee_allocation_id", nullable = false)
    private Long studentFeeAllocationId;
    
    @Column(name = "exam_fee_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal examFeeAmount;
    
    @Column(length = 10)
    private String currency = "INR";
    
    @Column(name = "applied_date")
    private LocalDate appliedDate;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (appliedDate == null) {
            appliedDate = LocalDate.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public ExamFeeLinkage() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getExamId() {
        return examId;
    }
    
    public void setExamId(Long examId) {
        this.examId = examId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getStudentFeeAllocationId() {
        return studentFeeAllocationId;
    }
    
    public void setStudentFeeAllocationId(Long studentFeeAllocationId) {
        this.studentFeeAllocationId = studentFeeAllocationId;
    }
    
    public BigDecimal getExamFeeAmount() {
        return examFeeAmount;
    }
    
    public void setExamFeeAmount(BigDecimal examFeeAmount) {
        this.examFeeAmount = examFeeAmount;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public LocalDate getAppliedDate() {
        return appliedDate;
    }
    
    public void setAppliedDate(LocalDate appliedDate) {
        this.appliedDate = appliedDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}