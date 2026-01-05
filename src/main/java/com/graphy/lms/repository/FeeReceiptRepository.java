package com.graphy.lms.repository;

import com.graphy.lms.entity.FeeReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeeReceiptRepository extends JpaRepository<FeeReceipt, Long> {

    /**
     * Satisfies Access Matrix:
     * GET (self/child) ✅ Student, ✅ Parent
     * Logic: Navigates Receipt -> Payment -> Allocation -> userId.
     * This ensures students can only download/view their own receipts in Postman.
     */
    List<FeeReceipt> findByStudentFeePaymentStudentFeeAllocationUserId(Long userId);

    /**
     * Finds a specific receipt by the auto-generated receipt number.
     */
    FeeReceipt findByReceiptNumber(String receiptNumber);
}