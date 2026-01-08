package com.graphy.lms.repository;

import com.graphy.lms.entity.FeeReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeeReceiptRepository extends JpaRepository<FeeReceipt, Long> {
    FeeReceipt findByStudentFeePaymentId(Long paymentId);
    List<FeeReceipt> findByReceiptDateBetween(java.time.LocalDate start, java.time.LocalDate end);
}