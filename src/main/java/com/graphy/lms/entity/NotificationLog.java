package com.graphy.lms.entity;

import javax.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "notification_type", length = 20)
    private String notificationType; // EMAIL, SMS, WHATSAPP
    
    @Column(name = "subject", length = 255)
    private String subject;
    
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;
    
    @Column(name = "sent_at", updatable = false)
    private LocalDateTime sentAt;
    
    @Column(name = "status", length = 20)
    private String status = "SENT"; // SENT, FAILED, PENDING
    
    @PrePersist
    protected void onCreate() {
        sentAt = LocalDateTime.now();
    }
}