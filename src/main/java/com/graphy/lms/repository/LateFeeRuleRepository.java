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
public interface LateFeeRuleRepository extends JpaRepository<LateFeeRule, Long> {
    List<LateFeeRule> findByIsActiveTrue();
    Optional<LateFeeRule> findByFeeTypeIdAndIsActiveTrue(Long feeTypeId);
    
    @Query("SELECT lfr FROM LateFeeRule lfr WHERE lfr.feeTypeId IS NULL AND lfr.isActive = true")
    Optional<LateFeeRule> findDefaultLateFeeRule();
}