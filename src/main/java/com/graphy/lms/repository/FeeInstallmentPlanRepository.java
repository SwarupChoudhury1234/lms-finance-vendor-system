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
public interface FeeInstallmentPlanRepository extends JpaRepository<FeeInstallmentPlan, Long> {
    List<FeeInstallmentPlan> findByStudentFeeAllocationId(Long studentFeeAllocationId);
    List<FeeInstallmentPlan> findByStudentFeeAllocationIdOrderByInstallmentNumberAsc(Long studentFeeAllocationId);
    List<FeeInstallmentPlan> findByStatus(String status);
    List<FeeInstallmentPlan> findByDueDateBeforeAndStatus(LocalDate date, String status);
    
    @Query("SELECT fip FROM FeeInstallmentPlan fip WHERE fip.studentFeeAllocationId = :allocationId AND fip.installmentNumber = :installmentNumber")
    Optional<FeeInstallmentPlan> findByAllocationIdAndInstallmentNumber(
        @Param("allocationId") Long allocationId,
        @Param("installmentNumber") Integer installmentNumber
    );
    
    @Query("SELECT fip FROM FeeInstallmentPlan fip WHERE fip.dueDate < :currentDate AND fip.status = 'PENDING'")
    List<FeeInstallmentPlan> findOverdueInstallments(@Param("currentDate") LocalDate currentDate);
}


