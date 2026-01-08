package com.graphy.lms.entity;

import javax.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fee_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeReport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "report_type", length = 20)
    private String reportType; // STUDENT_WISE, BATCH_WISE, COURSE_WISE, MONTHLY, QUARTERLY, YEARLY
    
    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;
    
    @Column(name = "filter_criteria", columnDefinition = "JSON")
    private String filterCriteria;
    
    @Column(name = "total_collection", precision = 15, scale = 2)
    private BigDecimal totalCollection = BigDecimal.ZERO;
    
    @Column(name = "total_pending", precision = 15, scale = 2)
    private BigDecimal totalPending = BigDecimal.ZERO;
    
    @Column(name = "generated_by")
    private Long generatedBy;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (reportDate == null) {
            reportDate = LocalDate.now();
        }
    }
}