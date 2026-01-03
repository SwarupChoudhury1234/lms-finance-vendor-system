package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.Data; // <--- This was missing
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data // Lombok will generate Getters, Setters, ToString, etc.
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String module;
    
    private Long entityId;
    
    private String action; // e.g., POST, PUT, DELETE
    
    private Long performedBy; // The userId taken from the token
    
    private LocalDateTime performedAt = LocalDateTime.now();

    // Added for consistency with your mentor's module-wide rules
    private LocalDateTime updatedAt; 
}