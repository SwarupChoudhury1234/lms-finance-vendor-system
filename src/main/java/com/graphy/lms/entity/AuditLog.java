package com.graphy.lms.entity;

import javax.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "module", nullable = false, length = 50)
    private String module;
    
    @Column(name = "entity_id")
    private Long entityId;
    
    @Column(name = "action", nullable = false, length = 20)
    private String action; // CREATE, UPDATE, DELETE
    
    @Column(name = "performed_by", nullable = false)
    private Long performedBy;
    
    @Column(name = "performed_at", updatable = false)
    private LocalDateTime performedAt;
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "user_agent", length = 500)
    private String userAgent;
    
    @Column(name = "old_values", columnDefinition = "JSON")
    private String oldValues;
    
    @Column(name = "new_values", columnDefinition = "JSON")
    private String newValues;
    
    @Column(name = "remarks", length = 255)
    private String remarks;
    
    @PrePersist
    protected void onCreate() {
        performedAt = LocalDateTime.now();
    }
}