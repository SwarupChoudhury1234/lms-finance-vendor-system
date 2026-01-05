package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String module; // e.g., "FEE_TYPE", "PAYMENT"
    
    @Column(nullable = false)
    private Long entityId; // The ID of the record being changed
    
    @Column(nullable = false)
    private String action; // POST, PUT, DELETE
    
    @Column(nullable = false)
    private Long performedBy; // The actorId (Admin/Faculty/Student)
    
    @Column(updatable = false)
    private LocalDateTime performedAt;

    // Included to satisfy your mentor's "All Columns" rule across all 7 tables
    private LocalDateTime updatedAt; 

    @PrePersist
    protected void onCreate() {
        this.performedAt = LocalDateTime.now();
    }
}