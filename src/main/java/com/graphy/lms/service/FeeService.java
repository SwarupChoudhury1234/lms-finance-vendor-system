package com.graphy.lms.service;

import com.graphy.lms.entity.*;
import java.util.List;

public interface FeeService {

    // --- 1. FEE TYPE ---
    FeeType saveFeeType(FeeType ft);
    FeeType getFeeTypeById(Long id);
    List<FeeType> getAllFeeTypes();
    FeeType updateFeeType(Long id, FeeType details);
    void deleteFeeType(Long id);

    // --- 2. FEE STRUCTURE ---
    FeeStructure saveFeeStructure(FeeStructure fs);
    FeeStructure getFeeStructureById(Long id);
    List<FeeStructure> getAllFeeStructures();
    FeeStructure updateFeeStructure(Long id, FeeStructure details);
    void deleteFeeStructure(Long id);

    // --- 3. ALLOCATIONS ---
    StudentFeeAllocation saveAllocation(StudentFeeAllocation sfa);
    StudentFeeAllocation getAllocationById(Long id);
    List<StudentFeeAllocation> getAllAllocations();
    StudentFeeAllocation updateAllocation(Long id, StudentFeeAllocation details);
    void deleteAllocation(Long id);

    // --- 4. PAYMENTS ---
    StudentFeePayment savePayment(StudentFeePayment sfp);
    StudentFeePayment getPaymentById(Long id);
    List<StudentFeePayment> getAllPayments();
    StudentFeePayment updatePayment(Long id, StudentFeePayment details);
    void deletePayment(Long id);

    // --- 5. DISCOUNTS ---
    FeeDiscount saveDiscount(FeeDiscount fd);
    FeeDiscount getDiscountById(Long id);
    List<FeeDiscount> getAllDiscounts();
    FeeDiscount updateDiscount(Long id, FeeDiscount details);
    void deleteDiscount(Long id);

    // --- 6. REFUNDS ---
    FeeRefund saveRefund(FeeRefund fr);
    FeeRefund getRefundById(Long id);
    List<FeeRefund> getAllRefunds();
    FeeRefund updateRefund(Long id, FeeRefund details);
    void deleteRefund(Long id);

    // --- 7. AUDIT LOGS ---
    AuditLog saveAuditLog(AuditLog al);
    AuditLog getAuditLogById(Long id);
    List<AuditLog> getAllAuditLogs();
}