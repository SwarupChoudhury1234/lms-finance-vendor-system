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
public interface FeeRefundRepository extends JpaRepository<FeeRefund, Long> {
    List<FeeRefund> findByUserId(Long userId);
    List<FeeRefund> findByStudentFeePaymentId(Long paymentId);
    List<FeeRefund> findByStatus(String status);
    List<FeeRefund> findByApprovedBy(Long approvedBy);
    List<FeeRefund> findByRefundDateBetween(LocalDate startDate, LocalDate endDate);
}