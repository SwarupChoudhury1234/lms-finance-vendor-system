package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "certificate_blocks")
public class CertificateBlock {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "user_id", nullable = false) private Long userId;
    @Column(name = "certificate_type") private String certificateType;
    @Column(name = "block_reason") private String blockReason;
    @Column(name = "blocked_by") private Long blockedBy;
    @Column(name = "blocked_at") private LocalDateTime blockedAt = LocalDateTime.now();
    @Column(name = "unblocked_at") private LocalDateTime unblockedAt;
    private String status = "BLOCKED";
    
    public CertificateBlock() {}
    
    // Getters/Setters
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; } public void setUserId(Long userId) { this.userId = userId; }
    public String getCertificateType() { return certificateType; } public void setCertificateType(String certificateType) { this.certificateType = certificateType; }
    public String getBlockReason() { return blockReason; } public void setBlockReason(String blockReason) { this.blockReason = blockReason; }
    public Long getBlockedBy() { return blockedBy; } public void setBlockedBy(Long blockedBy) { this.blockedBy = blockedBy; }
    public LocalDateTime getBlockedAt() { return blockedAt; } public void setBlockedAt(LocalDateTime blockedAt) { this.blockedAt = blockedAt; }
    public LocalDateTime getUnblockedAt() { return unblockedAt; } public void setUnblockedAt(LocalDateTime unblockedAt) { this.unblockedAt = unblockedAt; }
    public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
}