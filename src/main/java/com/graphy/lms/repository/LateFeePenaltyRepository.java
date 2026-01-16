package com.graphy.lms.repository;

import com.graphy.lms.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface LateFeePenaltyRepository extends JpaRepository<LateFeePenalty, Long> {
    List<LateFeePenalty> findByStudentInstallmentPlanId(Long studentInstallmentPlanId);
    List<LateFeePenalty> findByIsWaived(Boolean isWaived);
    
    @Query("SELECT SUM(l.penaltyAmount) FROM LateFeePenalty l WHERE l.studentInstallmentPlanId = :installmentPlanId AND l.isWaived = false")
    BigDecimal getTotalPenaltyByInstallmentPlanId(@Param("installmentPlanId") Long installmentPlanId);
}