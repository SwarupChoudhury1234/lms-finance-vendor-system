package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoice_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceTemplate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name; // "Monthly Invoice", "Quarterly Invoice"
    
    @Column(name = "frequency", length = 20)
    private String frequency; // MONTHLY, QUARTERLY, YEARLY
    
    @Column(name = "template_content", columnDefinition = "TEXT")
    private String templateContent;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}