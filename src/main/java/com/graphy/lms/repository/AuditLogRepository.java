package com.graphy.lms.repository;

import com.graphy.lms.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByModuleAndEntityId(String module, Long entityId);
    List<AuditLog> findByPerformedAtBetween(LocalDateTime start, LocalDateTime end);
}