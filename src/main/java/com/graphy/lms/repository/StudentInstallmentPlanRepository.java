package com.graphy.lms.repository;

import com.graphy.lms.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StudentInstallmentPlanRepository extends JpaRepository<StudentInstallmentPlan, Long> {
    List<StudentInstallmentPlan> findByStudentFeeAllocationId(Long studentFeeAllocationId);
    List<StudentInstallmentPlan> findByStatus(StudentInstallmentPlan.InstallmentStatus status);
    List<StudentInstallmentPlan> findByDueDateBefore(LocalDate date);
    
    @Query("SELECT s FROM StudentInstallmentPlan s WHERE s.dueDate < :currentDate AND s.status IN ('PENDING', 'PARTIALLY_PAID')")
    List<StudentInstallmentPlan> findOverdueInstallments(@Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT s FROM StudentInstallmentPlan s WHERE s.studentFeeAllocationId = :allocationId ORDER BY s.installmentNumber")
    List<StudentInstallmentPlan> findByStudentFeeAllocationIdOrderByInstallmentNumber(@Param("allocationId") Long allocationId);
}