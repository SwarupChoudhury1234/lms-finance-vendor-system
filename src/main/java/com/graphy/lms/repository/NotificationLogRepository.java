package com.graphy.lms.repository;

import com.graphy.lms.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    
    List<NotificationLog> findByTypeAndStatus(String type, String status);
    
    List<NotificationLog> findByRecipientAndSentAtAfter(String recipient, LocalDateTime sentAfter);
    
    List<NotificationLog> findByReferenceId(Long referenceId);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM NotificationLog n WHERE n.sentAt < :cutoffDate")
    int deleteBySentAtBefore(LocalDateTime cutoffDate);
    
    @Query("SELECT COUNT(n) FROM NotificationLog n WHERE n.type = :type AND DATE(n.sentAt) = CURRENT_DATE")
    long countByTypeToday(String type);
}