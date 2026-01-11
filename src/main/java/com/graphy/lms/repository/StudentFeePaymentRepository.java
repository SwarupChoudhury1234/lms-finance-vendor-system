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
public interface StudentFeePaymentRepository extends JpaRepository<StudentFeePayment, Long> {
    List<StudentFeePayment> findByStudentFeeAllocationId(Long studentFeeAllocationId);
    List<StudentFeePayment> findByInstallmentPlanId(Long installmentPlanId);
    List<StudentFeePayment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);
    List<StudentFeePayment> findByPaymentMode(String paymentMode);
    List<StudentFeePayment> findByPaymentStatus(String paymentStatus);
    List<StudentFeePayment> findByCollectedBy(Long collectedBy);
    
    @Query("SELECT sfp FROM StudentFeePayment sfp WHERE sfp.studentFeeAllocationId = :allocationId ORDER BY sfp.paymentDate DESC")
    List<StudentFeePayment> findPaymentHistory(@Param("allocationId") Long allocationId);
    
    @Query("SELECT SUM(sfp.paidAmount) FROM StudentFeePayment sfp WHERE sfp.studentFeeAllocationId = :allocationId AND sfp.paymentStatus = 'SUCCESS'")
    Optional<java.math.BigDecimal> getTotalPaidAmount(@Param("allocationId") Long allocationId);
    
    @Query("SELECT sfp FROM StudentFeePayment sfp JOIN StudentFeeAllocation sfa ON sfp.studentFeeAllocationId = sfa.id JOIN FeeStructure fs ON sfa.feeStructureId = fs.id WHERE fs.courseId = :courseId")
    List<StudentFeePayment> findByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT sfp FROM StudentFeePayment sfp JOIN StudentFeeAllocation sfa ON sfp.studentFeeAllocationId = sfa.id JOIN FeeStructure fs ON sfa.feeStructureId = fs.id WHERE fs.batchId = :batchId")
    List<StudentFeePayment> findByBatchId(@Param("batchId") Long batchId);
}