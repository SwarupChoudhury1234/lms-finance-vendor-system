package com.graphy.lms.repository;

import com.graphy.lms.entity.FeeRefund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeeRefundRepository extends JpaRepository<FeeRefund, Long> {

    /**
     * Satisfies Access Matrix:
     * GET (self/child) ✅ Student, ✅ Parent
     * Logic: Navigates Refund -> StudentFeePayment -> StudentFeeAllocation -> userId.
     * This ensures students only see their own refund records in the Postman console.
     */
    List<FeeRefund> findByStudentFeePaymentStudentFeeAllocationUserId(Long userId);

    /**
     * Supports fetching all refunds associated with a specific payment reference
     */
    List<FeeRefund> findByStudentFeePaymentId(Long paymentId);
}