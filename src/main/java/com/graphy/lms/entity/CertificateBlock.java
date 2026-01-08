package com.graphy.lms.entity;

import javax.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "certificate_blocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificateBlock {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "certificate_type", length = 100)
    private String certificateType;
    
    @Column(name = "block_reason", length = 255)
    private String blockReason;
    
    @Column(name = "blocked_by")
    private Long blockedBy;
    
    @Column(name = "blocked_at", updatable = false)
    private LocalDateTime blockedAt;
    
    @Column(name = "unblocked_at")
    private LocalDateTime unblockedAt;
    
    @Column(name = "status", length = 20)
    private String status = "BLOCKED"; // BLOCKED, UNBLOCKED
    
    @PrePersist
    protected void onCreate() {
        blockedAt = LocalDateTime.now();
    }
}