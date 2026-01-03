package com.graphy.lms.repository;

import com.graphy.lms.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // <--- This was missing

@Repository 
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}