package com.graphy.lms.service;

import com.graphy.lms.entity.*;
import java.util.List;

public interface FeeService {

    // ========================================================================
    // 1. FEE TYPES (5 Methods)
    // ========================================================================
    FeeType createFeeType(FeeType feeType, Long actorId);
    List<FeeType> getAllFeeTypes();
    FeeType getFeeTypeById(Long id);
    FeeType updateFeeType(Long id, FeeType feeType, Long actorId);
    void deleteFeeType(Long id, Long actorId);

    // ========================================================================
    // 2. FEE STRUCTURES (5 Methods)
    // ========================================================================
    FeeStructure createFeeStructure(FeeStructure feeStructure, Long actorId);
    List<FeeStructure> getAllFeeStructures();
    FeeStructure getFeeStructureById(Long id);
    FeeStructure updateFeeStructure(Long id, FeeStructure feeStructure, Long actorId);
    void deleteFeeStructure(Long id, Long actorId);

    // ========================================================================
    // 3. STUDENT FEE ALLOCATIONS (5 Methods)
    // ========================================================================
    StudentFeeAllocation allocateFee(StudentFeeAllocation allocation, Long actorId);
    List<StudentFeeAllocation> getAllAllocations();
    StudentFeeAllocation getAllocationById(Long id);
    StudentFeeAllocation updateAllocation(Long id, StudentFeeAllocation allocation, Long actorId);
    void deleteAllocation(Long id, Long actorId);

    // ========================================================================
    // 4. STUDENT FEE PAYMENTS (5 Methods) - Triggers Auto-Receipt
    // ========================================================================
    StudentFeePayment processPayment(StudentFeePayment payment, Long actorId);
    List<StudentFeePayment> getAllPayments();
    StudentFeePayment getPaymentById(Long id);
    StudentFeePayment updatePayment(Long id, StudentFeePayment payment, Long actorId);
    void deletePayment(Long id, Long actorId);

    // ========================================================================
    // 5. FEE DISCOUNTS (5 Methods)
    // ========================================================================
    FeeDiscount applyDiscount(FeeDiscount discount, Long actorId);
    List<FeeDiscount> getAllDiscounts();
    FeeDiscount getDiscountById(Long id);
    FeeDiscount updateDiscount(Long id, FeeDiscount discount, Long actorId);
    void deleteDiscount(Long id, Long actorId);

    // ========================================================================
    // 6. FEE REFUNDS (5 Methods)
    // ========================================================================
    FeeRefund processRefund(FeeRefund refund, Long actorId);
    List<FeeRefund> getAllRefunds();
    FeeRefund getRefundById(Long id);
    FeeRefund updateRefund(Long id, FeeRefund refund, Long actorId);
    void deleteRefund(Long id, Long actorId);

    // ========================================================================
    // 7. AUDIT LOGS (2 Methods - System Automated)
    // ========================================================================
    List<AuditLog> getAllAuditLogs();
    AuditLog getAuditLogById(Long id);

    // ========================================================================
    // 8. FEE RECEIPTS (2 Methods - System Automated)
    // ========================================================================
    List<FeeReceipt> getAllReceipts();
    FeeReceipt getReceiptById(Long id);
}