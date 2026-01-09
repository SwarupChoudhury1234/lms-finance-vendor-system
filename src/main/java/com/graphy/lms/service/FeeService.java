package com.graphy.lms.service;

import com.graphy.lms.entity.*;
import java.util.List;
import java.math.BigDecimal;

public interface FeeService {
    
    // =============== FEE TYPES ===============
    FeeType createFeeType(FeeType feeType, Long actorId, String role);
    List<FeeType> getAllFeeTypes(Long actorId, String role);
    List<FeeType> getActiveFeeTypes();
    FeeType getFeeTypeById(Long id, Long actorId, String role);
    FeeType updateFeeType(Long id, FeeType feeType, Long actorId, String role);
    void deleteFeeType(Long id, Long actorId, String role);
    
    // =============== FEE STRUCTURES ===============
    FeeStructure createFeeStructure(FeeStructure feeStructure, Long actorId, String role);
    List<FeeStructure> getFeeStructures(Long actorId, String role, Long courseId, String academicYear);
    FeeStructure getFeeStructureById(Long id, Long actorId, String role);
    FeeStructure updateFeeStructure(Long id, FeeStructure feeStructure, Long actorId, String role);
    void deleteFeeStructure(Long id, Long actorId, String role);
    
    // =============== STUDENT FEE ALLOCATIONS ===============
    StudentFeeAllocation allocateFee(StudentFeeAllocation allocation, Long actorId, String role);
    List<StudentFeeAllocation> getAllocations(Long actorId, String role, Long userId);
    StudentFeeAllocation getAllocationById(Long id, Long actorId, String role);
    StudentFeeAllocation updateAllocation(Long id, StudentFeeAllocation allocation, Long actorId, String role);
    
    // =============== PAYMENT ALTERNATIVES ===============
    PaymentAlternative createPaymentAlternative(PaymentAlternative alternative, Long actorId, String role);
    List<PaymentAlternative> getAllPaymentAlternatives(Long actorId, String role);
    List<PaymentAlternative> getActivePaymentAlternatives();
    PaymentAlternative getPaymentAlternativeById(Long id, Long actorId, String role);
    PaymentAlternative updatePaymentAlternative(Long id, PaymentAlternative alternative, Long actorId, String role);
    void deletePaymentAlternative(Long id, Long actorId, String role);
    
    // =============== PAYMENT INSTALLMENTS ===============
    PaymentInstallment createPaymentInstallment(PaymentInstallment installment, Long actorId, String role);
    List<PaymentInstallment> getInstallmentsByAllocationId(Long allocationId, Long actorId, String role);
    PaymentInstallment updateInstallment(Long id, PaymentInstallment installment, Long actorId, String role);
    
    // =============== STUDENT FEE PAYMENTS ===============
    StudentFeePayment processPayment(StudentFeePayment payment, Long actorId, String role);
    List<StudentFeePayment> getPayments(Long actorId, String role);
    StudentFeePayment getPaymentById(Long id, Long actorId, String role);
    
    // =============== FEE DISCOUNTS ===============
    FeeDiscount applyDiscount(FeeDiscount discount, Long actorId, String role);
    List<FeeDiscount> getDiscounts(Long actorId, String role);
    FeeDiscount getDiscountById(Long id, Long actorId, String role);
    FeeDiscount updateDiscount(Long id, FeeDiscount discount, Long actorId, String role);
    void deleteDiscount(Long id, Long actorId, String role);
    
    // =============== FEE REFUNDS ===============
    FeeRefund processRefund(FeeRefund refund, Long actorId, String role);
    List<FeeRefund> getRefunds(Long actorId, String role);
    FeeRefund getRefundById(Long id, Long actorId, String role);
    
    // =============== FEE RECEIPTS ===============
    List<FeeReceipt> getReceipts(Long actorId, String role);
    FeeReceipt getReceiptById(Long id, Long actorId, String role);
    
    // =============== AUDIT LOGS ===============
    List<AuditLog> getAuditLogs(Long actorId, String role);
    AuditLog getAuditLogById(Long id, Long actorId, String role);
    
    // =============== OTHER ENTITIES ===============
    CurrencyRate createCurrencyRate(CurrencyRate rate, Long actorId, String role);
    List<CurrencyRate> getCurrencyRates(Long actorId, String role);
    
    NotificationLog createNotificationLog(NotificationLog log, Long actorId, String role);
    
    AttendancePenalty createAttendancePenalty(AttendancePenalty penalty, Long actorId, String role);
    
    CertificateBlock createCertificateBlock(CertificateBlock block, Long actorId, String role);
    
    AutoDebitSetting createAutoDebitSetting(AutoDebitSetting setting, Long actorId, String role);
    
    FeeReport generateFeeReport(FeeReport report, Long actorId, String role);
    
    // =============== NEW INSTALLMENT LOGIC ===============
    StudentFeeAllocation setAdvancePayment(Long allocationId, BigDecimal advancePayment, Long actorId, String role);
    StudentFeeAllocation selectPaymentPlan(Long allocationId, Long alternativeId, List<BigDecimal> customAmounts, Long actorId, String role);
    void recalculateInstallments(Long allocationId, List<BigDecimal> newAmounts, Long actorId, String role);
}