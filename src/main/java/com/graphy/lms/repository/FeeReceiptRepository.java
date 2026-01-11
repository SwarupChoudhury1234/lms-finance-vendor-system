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
public interface FeeReceiptRepository extends JpaRepository<FeeReceipt, Long> {
    Optional<FeeReceipt> findByPaymentId(Long paymentId);
    Optional<FeeReceipt> findByReceiptNumber(String receiptNumber);
    List<FeeReceipt> findByEmailSent(Boolean emailSent);
    
    @Query("SELECT fr FROM FeeReceipt fr JOIN StudentFeePayment sfp ON fr.paymentId = sfp.id JOIN StudentFeeAllocation sfa ON sfp.studentFeeAllocationId = sfa.id WHERE sfa.userId = :userId")
    List<FeeReceipt> findByStudentUserId(@Param("userId") Long userId);
}