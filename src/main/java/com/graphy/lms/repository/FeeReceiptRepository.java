package com.graphy.lms.repository;

import com.graphy.lms.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeeReceiptRepository extends JpaRepository<FeeReceipt, Long> {
    Optional<FeeReceipt> findByPaymentId(Long paymentId);
    Optional<FeeReceipt> findByReceiptNumber(String receiptNumber);
    List<FeeReceipt> findByUserId(Long userId);
}