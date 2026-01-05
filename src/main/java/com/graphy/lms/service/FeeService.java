package com.graphy.lms.service;

import com.graphy.lms.entity.*;
import java.util.List;

public interface FeeService {

    // ========================================================================
    // 1. FEE TYPES
    // ========================================================================
    FeeType createFeeType(FeeType feeType, Long actorId, String role);
    
    // Matrix Fix: Separated GET ALL (Admin/Faculty) from GET Active (Everyone)
    List<FeeType> getAllFeeTypes(Long actorId, String role);
    
    List<FeeType> getActiveFeeTypes();
    
    FeeType getFeeTypeById(Long id, Long actorId, String role);
    
    FeeType updateFeeType(Long id, FeeType feeType, Long actorId, String role);
    
    void deleteFeeType(Long id, Long actorId, String role);

    // ========================================================================
    // 2. FEE STRUCTURES
    // ========================================================================
    FeeStructure createFeeStructure(FeeStructure feeStructure, Long actorId, String role);
    
    // Matrix Fix: Added academicYear to satisfy "GET by course/year"
    List<FeeStructure> getFeeStructures(Long actorId, String role, Long courseId, String academicYear);
    
    FeeStructure getFeeStructureById(Long id, Long actorId, String role);
    
    FeeStructure updateFeeStructure(Long id, FeeStructure feeStructure, Long actorId, String role);
    
    void deleteFeeStructure(Long id, Long actorId, String role);

    // ========================================================================
    // 3. STUDENT FEE ALLOCATIONS
    // ========================================================================
    StudentFeeAllocation allocateFee(StudentFeeAllocation allocation, Long actorId, String role);
    
    // Matrix Fix: userId allows Parent to specify child; logic will verify relationship
    List<StudentFeeAllocation> getAllocations(Long actorId, String role, Long userId);
    
    StudentFeeAllocation getAllocationById(Long id, Long actorId, String role);
    
    StudentFeeAllocation updateAllocation(Long id, StudentFeeAllocation allocation, Long actorId, String role);
    
    void deleteAllocation(Long id, Long actorId, String role);

    // ========================================================================
    // 4. STUDENT FEE PAYMENTS
    // ========================================================================
    StudentFeePayment processPayment(StudentFeePayment payment, Long actorId, String role);
    
    // Matrix Fix: Logic will filter by own/child payments
    List<StudentFeePayment> getPayments(Long actorId, String role);
    
    StudentFeePayment getPaymentById(Long id, Long actorId, String role);

    // ========================================================================
    // 5. FEE DISCOUNTS
    // ========================================================================
    FeeDiscount applyDiscount(FeeDiscount discount, Long actorId, String role);
    
    List<FeeDiscount> getDiscounts(Long actorId, String role);
    
    FeeDiscount getDiscountById(Long id, Long actorId, String role);
    
    FeeDiscount updateDiscount(Long id, FeeDiscount discount, Long actorId, String role);
    
    void deleteDiscount(Long id, Long actorId, String role);

    // ========================================================================
    // 6. FEE REFUNDS
    // ========================================================================
    FeeRefund processRefund(FeeRefund refund, Long actorId, String role);
    
    List<FeeRefund> getRefunds(Long actorId, String role);
    
    FeeRefund getRefundById(Long id, Long actorId, String role);

    // ========================================================================
    // 7. AUDIT LOGS
    // ========================================================================
    List<AuditLog> getAuditLogs(Long actorId, String role);
    
    AuditLog getAuditLogById(Long id, Long actorId, String role);

    // ========================================================================
    // 8. FEE RECEIPTS
    // ========================================================================
    List<FeeReceipt> getReceipts(Long actorId, String role);
    
    FeeReceipt getReceiptById(Long id, Long actorId, String role);
}