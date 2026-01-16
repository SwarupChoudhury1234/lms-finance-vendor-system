package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50)
    private String module;
    
    @Column(name = "entity_name", nullable = false, length = 100)
    private String entityName;
    
    @Column(name = "entity_id", nullable = false)
    private Long entityId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Action action;
    
    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;
    
    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;
    
    @Column(name = "performed_by")
    private Long performedBy;
    
    @Column(name = "ip_address", length = 50)
    private String ipAddress;
    
    @Column(name = "performed_at", updatable = false)
    private LocalDateTime performedAt;
    
    public enum Action {
        CREATE, UPDATE, DELETE, VIEW
    }
    
    @PrePersist
    protected void onCreate() {
        performedAt = LocalDateTime.now();
    }
    
    // Constructors
    public AuditLog() {}
    
    public AuditLog(String module, String entityName, Long entityId, Action action, 
                    String oldValue, String newValue, Long performedBy, String ipAddress) {
        this.module = module;
        this.entityName = entityName;
        this.entityId = entityId;
        this.action = action;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.performedBy = performedBy;
        this.ipAddress = ipAddress;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getModule() {
        return module;
    }
    
    public void setModule(String module) {
        this.module = module;
    }
    
    public String getEntityName() {
        return entityName;
    }
    
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }
    
    public Long getEntityId() {
        return entityId;
    }
    
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
    
    public Action getAction() {
        return action;
    }
    
    public void setAction(Action action) {
        this.action = action;
    }
    
    public String getOldValue() {
        return oldValue;
    }
    
    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }
    
    public String getNewValue() {
        return newValue;
    }
    
    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }
    
    public Long getPerformedBy() {
        return performedBy;
    }
    
    public void setPerformedBy(Long performedBy) {
        this.performedBy = performedBy;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public LocalDateTime getPerformedAt() {
        return performedAt;
    }
    
    public void setPerformedAt(LocalDateTime performedAt) {
        this.performedAt = performedAt;
    }
}