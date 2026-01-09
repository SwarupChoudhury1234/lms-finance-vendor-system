package com.graphy.lms.repository;

import com.graphy.lms.entity.FeeReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface FeeReceiptRepository extends JpaRepository<FeeReceipt, Long> {
    
    // Method returns Optional to handle null cases properly
    Optional<FeeReceipt> findByStudentFeePaymentId(Long paymentId);
    
    // Find receipts by date range
    List<FeeReceipt> findByReceiptDateBetween(java.time.LocalDate start, java.time.LocalDate end);
    
    // Find all receipts for a specific user (student)
    @Query("SELECT r FROM FeeReceipt r WHERE r.studentFeePayment.studentFeeAllocation.userId = :userId")
    List<FeeReceipt> findByUserId(Long userId);
    
    // Optional: Find by receipt number if you need it
    Optional<FeeReceipt> findByReceiptNumber(String receiptNumber);
}