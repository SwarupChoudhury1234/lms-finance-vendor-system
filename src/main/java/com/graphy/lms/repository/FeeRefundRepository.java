package com.graphy.lms.repository;

import com.graphy.lms.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeeRefundRepository extends JpaRepository<FeeRefund, Long> {
    List<FeeRefund> findByUserId(Long userId);
    List<FeeRefund> findByRefundStatus(FeeRefund.RefundStatus refundStatus);
    List<FeeRefund> findByStudentFeeAllocationId(Long studentFeeAllocationId);
    List<FeeRefund> findByStudentFeePaymentId(Long studentFeePaymentId);
    java.util.Optional<FeeRefund> findByTransactionReference(String transactionReference);
}