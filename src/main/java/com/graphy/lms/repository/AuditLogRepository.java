package com.graphy.lms.repository;

import com.graphy.lms.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository 
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Supports Admin filtering:
     * Find all actions performed by a specific user (Admin/Faculty).
     */
    List<AuditLog> findByPerformedBy(Long actorId);

    /**
     * Supports Admin filtering:
     * Find all actions related to a specific module (e.g., "PAYMENT", "FEE_TYPE").
     */
    List<AuditLog> findByModule(String module);

    /**
     * Find logs for a specific record across its entire lifecycle.
     */
    List<AuditLog> findByModuleAndEntityId(String module, Long entityId);
}