package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String module;
    @Column(name = "entity_id") private Long entityId;
    @Column(nullable = false) private String action;
    @Column(name = "performed_by", nullable = false) private Long performedBy;
    @Column(name = "performed_at") private LocalDateTime performedAt = LocalDateTime.now();
    @Column(name = "ip_address") private String ipAddress;
    @Column(name = "user_agent") private String userAgent;
    @Column(columnDefinition = "JSON") private String oldValues;
    @Column(columnDefinition = "JSON") private String newValues;
    private String remarks;
    
    public AuditLog() {}
    
    // Getters/Setters
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getModule() { return module; } public void setModule(String module) { this.module = module; }
    public Long getEntityId() { return entityId; } public void setEntityId(Long entityId) { this.entityId = entityId; }
    public String getAction() { return action; } public void setAction(String action) { this.action = action; }
    public Long getPerformedBy() { return performedBy; } public void setPerformedBy(Long performedBy) { this.performedBy = performedBy; }
    public LocalDateTime getPerformedAt() { return performedAt; } public void setPerformedAt(LocalDateTime performedAt) { this.performedAt = performedAt; }
    public String getIpAddress() { return ipAddress; } public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getUserAgent() { return userAgent; } public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public String getOldValues() { return oldValues; } public void setOldValues(String oldValues) { this.oldValues = oldValues; }
    public String getNewValues() { return newValues; } public void setNewValues(String newValues) { this.newValues = newValues; }
    public String getRemarks() { return remarks; } public void setRemarks(String remarks) { this.remarks = remarks; }
}