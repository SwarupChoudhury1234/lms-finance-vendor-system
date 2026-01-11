package com.graphy.lms.repository;

import com.graphy.lms.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudentFeeAllocationRepository extends JpaRepository<StudentFeeAllocation, Long> {
    List<StudentFeeAllocation> findByUserId(Long userId);
    List<StudentFeeAllocation> findByUserIdAndStatus(Long userId, String status);
    List<StudentFeeAllocation> findByStatus(String status);
    List<StudentFeeAllocation> findByDueDateBefore(LocalDate date);
    
    @Query("SELECT sfa FROM StudentFeeAllocation sfa WHERE sfa.userId = :userId AND sfa.feeStructureId = :feeStructureId")
    Optional<StudentFeeAllocation> findByUserIdAndFeeStructureId(
        @Param("userId") Long userId,
        @Param("feeStructureId") Long feeStructureId
    );
    
    @Query("SELECT sfa FROM StudentFeeAllocation sfa JOIN FeeStructure fs ON sfa.feeStructureId = fs.id WHERE fs.courseId = :courseId")
    List<StudentFeeAllocation> findByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT sfa FROM StudentFeeAllocation sfa JOIN FeeStructure fs ON sfa.feeStructureId = fs.id WHERE fs.batchId = :batchId")
    List<StudentFeeAllocation> findByBatchId(@Param("batchId") Long batchId);
}