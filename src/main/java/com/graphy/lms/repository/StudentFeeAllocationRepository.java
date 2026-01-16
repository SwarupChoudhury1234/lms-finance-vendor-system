package com.graphy.lms.repository;

import com.graphy.lms.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentFeeAllocationRepository extends JpaRepository<StudentFeeAllocation, Long> {
    List<StudentFeeAllocation> findByUserId(Long userId);
    List<StudentFeeAllocation> findByStatus(StudentFeeAllocation.AllocationStatus status);
    Optional<StudentFeeAllocation> findByUserIdAndFeeStructureId(Long userId, Long feeStructureId);
    
    @Query("SELECT SUM(s.payableAmount) FROM StudentFeeAllocation s WHERE s.userId = :userId")
    BigDecimal getTotalPayableByUserId(@Param("userId") Long userId);
    
    @Query("SELECT SUM(s.remainingAmount) FROM StudentFeeAllocation s WHERE s.userId = :userId")
    BigDecimal getTotalRemainingByUserId(@Param("userId") Long userId);
}