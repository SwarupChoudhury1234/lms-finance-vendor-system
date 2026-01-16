package com.graphy.lms.repository;

import com.graphy.lms.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ExamFeeLinkageRepository extends JpaRepository<ExamFeeLinkage, Long> {
    List<ExamFeeLinkage> findByUserId(Long userId);
    List<ExamFeeLinkage> findByExamId(Long examId);
    List<ExamFeeLinkage> findByStudentFeeAllocationId(Long studentFeeAllocationId);
    
    @Query("SELECT SUM(e.examFeeAmount) FROM ExamFeeLinkage e WHERE e.studentFeeAllocationId = :allocationId")
    BigDecimal getTotalExamFeeByAllocationId(@Param("allocationId") Long allocationId);
}