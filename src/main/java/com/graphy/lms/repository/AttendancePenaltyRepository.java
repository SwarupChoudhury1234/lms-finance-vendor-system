package com.graphy.lms.repository;

import com.graphy.lms.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface AttendancePenaltyRepository extends JpaRepository<AttendancePenalty, Long> {
    List<AttendancePenalty> findByUserId(Long userId);
    List<AttendancePenalty> findByStudentFeeAllocationId(Long studentFeeAllocationId);
    List<AttendancePenalty> findByIsActive(Boolean isActive);
    
    @Query("SELECT SUM(a.penaltyAmount) FROM AttendancePenalty a WHERE a.studentFeeAllocationId = :allocationId AND a.isActive = true")
    BigDecimal getTotalPenaltyByAllocationId(@Param("allocationId") Long allocationId);
}