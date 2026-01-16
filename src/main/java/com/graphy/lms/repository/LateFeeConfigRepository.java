package com.graphy.lms.repository;

import com.graphy.lms.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LateFeeConfigRepository extends JpaRepository<LateFeeConfig, Long> {
    List<LateFeeConfig> findByIsActive(Boolean isActive);
    List<LateFeeConfig> findByPaymentSchedule(LateFeeConfig.PaymentSchedule paymentSchedule);
    
    @Query("SELECT l FROM LateFeeConfig l WHERE l.isActive = true AND :currentDate BETWEEN l.effectiveFrom AND COALESCE(l.effectiveTo, :currentDate)")
    List<LateFeeConfig> findActiveConfigsForDate(@Param("currentDate") LocalDate currentDate);
}