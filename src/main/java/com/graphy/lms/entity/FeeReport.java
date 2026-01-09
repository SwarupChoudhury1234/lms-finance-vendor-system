package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fee_reports")
public class FeeReport {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "report_type") private String reportType;
    @Column(name = "report_date", nullable = false) private LocalDate reportDate;
    @Column(columnDefinition = "JSON") private String filterCriteria;
    @Column(name = "total_collection", precision = 15, scale = 2) private BigDecimal totalCollection = BigDecimal.ZERO;
    @Column(name = "total_pending", precision = 15, scale = 2) private BigDecimal totalPending = BigDecimal.ZERO;
    @Column(name = "generated_by") private Long generatedBy;
    @Column(name = "created_at") private LocalDateTime createdAt = LocalDateTime.now();
    
    public FeeReport() {}
    
    // Getters/Setters
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getReportType() { return reportType; } public void setReportType(String reportType) { this.reportType = reportType; }
    public LocalDate getReportDate() { return reportDate; } public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }
    public String getFilterCriteria() { return filterCriteria; } public void setFilterCriteria(String filterCriteria) { this.filterCriteria = filterCriteria; }
    public BigDecimal getTotalCollection() { return totalCollection; } public void setTotalCollection(BigDecimal totalCollection) { this.totalCollection = totalCollection; }
    public BigDecimal getTotalPending() { return totalPending; } public void setTotalPending(BigDecimal totalPending) { this.totalPending = totalPending; }
    public Long getGeneratedBy() { return generatedBy; } public void setGeneratedBy(Long generatedBy) { this.generatedBy = generatedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}