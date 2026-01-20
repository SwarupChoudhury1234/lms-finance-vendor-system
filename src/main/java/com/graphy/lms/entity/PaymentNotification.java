package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.ToString;

@Entity
@ToString
@Table(name = "payment_notifications")
public class PaymentNotification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;
    
    @Column(length = 255)
    private String email;
    
    @Column(length = 20)
    private String phone;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status")
    private DeliveryStatus deliveryStatus = DeliveryStatus.PENDING;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    public enum NotificationType {
        PAYMENT_SUCCESS, PAYMENT_FAILED, DUE_REMINDER, OVERDUE_WARNING, RECEIPT_SENT
    }
    
    public enum DeliveryStatus {
        PENDING, SENT, FAILED
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Constructors
    public PaymentNotification() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public NotificationType getNotificationType() {
        return notificationType;
    }
    
    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public LocalDateTime getSentAt() {
        return sentAt;
    }
    
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
    
    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }
    
    public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}