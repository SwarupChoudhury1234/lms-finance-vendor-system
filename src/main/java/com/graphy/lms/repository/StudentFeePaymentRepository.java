package com.graphy.lms.repository;

import com.graphy.lms.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StudentFeePaymentRepository extends JpaRepository<StudentFeePayment, Long> {
    List<StudentFeePayment> findByStudentFeeAllocationId(Long studentFeeAllocationId);
    List<StudentFeePayment> findByPaymentStatus(StudentFeePayment.PaymentStatus paymentStatus);
    List<StudentFeePayment> findByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<StudentFeePayment> findByStudentInstallmentPlanId(Long studentInstallmentPlanId);
    
    @Query("SELECT SUM(p.paidAmount) FROM StudentFeePayment p WHERE p.studentFeeAllocationId = :allocationId AND p.paymentStatus = 'SUCCESS'")
    BigDecimal getTotalPaidByAllocationId(@Param("allocationId") Long allocationId);
    
    @Query("SELECT p FROM StudentFeePayment p WHERE p.paymentStatus = 'SUCCESS' AND p.paymentDate BETWEEN :startDate AND :endDate")
    List<StudentFeePayment> findSuccessfulPaymentsBetween(@Param("startDate") LocalDateTime startDate, 
                                                           @Param("endDate") LocalDateTime endDate);
}