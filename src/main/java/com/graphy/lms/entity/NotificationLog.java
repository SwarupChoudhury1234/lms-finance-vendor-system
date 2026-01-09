package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_logs")
public class NotificationLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "user_id", nullable = false) private Long userId;
    @Column(name = "notification_type") private String notificationType;
    private String subject;
    @Column(columnDefinition = "TEXT") private String message;
    @Column(name = "sent_at") private LocalDateTime sentAt = LocalDateTime.now();
    private String status = "SENT";
    
    public NotificationLog() {}
    
    // Getters/Setters
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; } public void setUserId(Long userId) { this.userId = userId; }
    public String getNotificationType() { return notificationType; } public void setNotificationType(String notificationType) { this.notificationType = notificationType; }
    public String getSubject() { return subject; } public void setSubject(String subject) { this.subject = subject; }
    public String getMessage() { return message; } public void setMessage(String message) { this.message = message; }
    public LocalDateTime getSentAt() { return sentAt; } public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
    public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
}