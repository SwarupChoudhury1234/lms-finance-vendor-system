package com.graphy.lms.repository;

import com.graphy.lms.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentNotificationRepository extends JpaRepository<PaymentNotification, Long> {
    List<PaymentNotification> findByUserId(Long userId);
    List<PaymentNotification> findByDeliveryStatus(PaymentNotification.DeliveryStatus deliveryStatus);
    List<PaymentNotification> findByNotificationType(PaymentNotification.NotificationType notificationType);
}