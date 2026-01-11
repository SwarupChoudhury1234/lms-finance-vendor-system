package com.graphy.lms.service;

import com.graphy.lms.entity.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


public interface FeeService {
    
    // ========================================
    // FEE TYPE OPERATIONS
    // ========================================
    FeeType createFeeType(FeeType feeType, Long performedBy);
    List<FeeType> getAllFeeTypes();
    List<FeeType> getActiveFeeTypes();
    FeeType getFeeTypeById(Long id);
    FeeType updateFeeType(Long id, FeeType feeType, Long performedBy);
    void deleteFeeType(Long id, Long performedBy);
    
    // ========================================
    // FEE STRUCTURE OPERATIONS
    // ========================================
    FeeStructure createFeeStructure(FeeStructure feeStructure, Long performedBy);
    List<FeeStructure> getAllFeeStructures();
    FeeStructure getFeeStructureById(Long id);
    List<FeeStructure> getFeeStructuresByCourseAndYear(Long courseId, String academicYear);
    FeeStructure updateFeeStructure(Long id, FeeStructure feeStructure, Long performedBy);
    void deleteFeeStructure(Long id, Long performedBy);
    
    // ========================================
    // STUDENT FEE ALLOCATION OPERATIONS
    // ========================================
    StudentFeeAllocation createStudentFeeAllocation(StudentFeeAllocation allocation, Long performedBy);
    List<StudentFeeAllocation> getAllStudentFeeAllocations();
    StudentFeeAllocation getStudentFeeAllocationById(Long id);
    List<StudentFeeAllocation> getStudentFeeAllocationsByUserId(Long userId);
    StudentFeeAllocation updateStudentFeeAllocation(Long id, StudentFeeAllocation allocation, Long performedBy);
    void deleteStudentFeeAllocation(Long id, Long performedBy);
    
    // Special allocation methods
    StudentFeeAllocation allocateFeeToStudent(Long userId, Long feeStructureId, LocalDate dueDate, 
                                              BigDecimal initialPayment, Integer numberOfInstallments, Long performedBy);
    void applyDiscountToAllocation(Long allocationId, Long discountId, Long performedBy);
    void adjustInstallmentPlan(Long allocationId, List<BigDecimal> newInstallmentAmounts, Long performedBy);
    
    // ========================================
    // FEE INSTALLMENT PLAN OPERATIONS
    // ========================================
    FeeInstallmentPlan createFeeInstallmentPlan(FeeInstallmentPlan installmentPlan, Long performedBy);
    List<FeeInstallmentPlan> getAllFeeInstallmentPlans();
    FeeInstallmentPlan getFeeInstallmentPlanById(Long id);
    List<FeeInstallmentPlan> getInstallmentPlansByAllocationId(Long allocationId);
    FeeInstallmentPlan updateFeeInstallmentPlan(Long id, FeeInstallmentPlan installmentPlan, Long performedBy);
    void deleteFeeInstallmentPlan(Long id, Long performedBy);
    
    // Special installment methods
    void updateOverdueInstallments();
    void calculateLateFees(Long allocationId);
    
    // ========================================
    // STUDENT FEE PAYMENT OPERATIONS
    // ========================================
    StudentFeePayment createStudentFeePayment(StudentFeePayment payment, Long performedBy);
    List<StudentFeePayment> getAllStudentFeePayments();
    StudentFeePayment getStudentFeePaymentById(Long id);
    List<StudentFeePayment> getPaymentsByAllocationId(Long allocationId);
    StudentFeePayment updateStudentFeePayment(Long id, StudentFeePayment payment, Long performedBy);
    void deleteStudentFeePayment(Long id, Long performedBy);
    
    // Special payment methods
    StudentFeePayment recordPayment(Long allocationId, BigDecimal amount, String paymentMode, 
                                    String transactionRef, Long collectedBy);
    StudentFeePayment recordInstallmentPayment(Long installmentPlanId, BigDecimal amount, 
                                               String paymentMode, String transactionRef, Long collectedBy);
    List<StudentFeePayment> getPaymentHistory(Long userId);
    
    // ========================================
    // FEE DISCOUNT OPERATIONS
    // ========================================
    FeeDiscount createFeeDiscount(FeeDiscount discount, Long performedBy);
    List<FeeDiscount> getAllFeeDiscounts();
    FeeDiscount getFeeDiscountById(Long id);
    List<FeeDiscount> getDiscountsByUserId(Long userId);
    FeeDiscount updateFeeDiscount(Long id, FeeDiscount discount, Long performedBy);
    void deleteFeeDiscount(Long id, Long performedBy);
    
    // Special discount methods
    FeeDiscount approveDiscount(Long discountId, Long approvedBy);
    FeeDiscount rejectDiscount(Long discountId, Long approvedBy, String reason);
    BigDecimal calculateDiscountAmount(BigDecimal originalAmount, String discountType, BigDecimal discountValue);
    
    // ========================================
    // FEE REFUND OPERATIONS
    // ========================================
    FeeRefund createFeeRefund(FeeRefund refund, Long performedBy);
    List<FeeRefund> getAllFeeRefunds();
    FeeRefund getFeeRefundById(Long id);
    List<FeeRefund> getRefundsByUserId(Long userId);
    FeeRefund updateFeeRefund(Long id, FeeRefund refund, Long performedBy);
    void deleteFeeRefund(Long id, Long performedBy);
    
    // Special refund methods
    FeeRefund approveRefund(Long refundId, Long approvedBy);
    FeeRefund rejectRefund(Long refundId, Long approvedBy, String reason);
    FeeRefund processRefund(Long refundId, String transactionRef, Long performedBy);
    
    // ========================================
    // FEE RECEIPT OPERATIONS (READ-ONLY)
    // ========================================
    List<FeeReceipt> getAllFeeReceipts();
    FeeReceipt getFeeReceiptById(Long id);
    FeeReceipt getFeeReceiptByPaymentId(Long paymentId);
    List<FeeReceipt> getReceiptsByStudentUserId(Long userId);
    
    // Special receipt methods (auto-generated)
    FeeReceipt generateReceipt(Long paymentId);
    void sendReceiptEmail(Long receiptId, String studentEmail);
    
    // ========================================
    // AUDIT LOG OPERATIONS (READ-ONLY)
    // ========================================
    List<AuditLog> getAllAuditLogs();
    AuditLog getAuditLogById(Long id);
    List<AuditLog> getAuditLogsByModule(String module);
    List<AuditLog> getAuditLogsByEntity(String entityType, Long entityId);
    
    // ========================================
    // LATE FEE RULE OPERATIONS
    // ========================================
    LateFeeRule createLateFeeRule(LateFeeRule lateFeeRule, Long performedBy);
    List<LateFeeRule> getAllLateFeeRules();
    LateFeeRule getLateFeeRuleById(Long id);
    LateFeeRule updateLateFeeRule(Long id, LateFeeRule lateFeeRule, Long performedBy);
    void deleteLateFeeRule(Long id, Long performedBy);
    
    // ========================================
    // REPORTING & ANALYTICS
    // ========================================
    Map<String, Object> getStudentFeeReport(Long userId);
    Map<String, Object> getBatchFeeReport(Long batchId);
    Map<String, Object> getCourseFeeReport(Long courseId);
    Map<String, Object> getRevenueReport(LocalDate startDate, LocalDate endDate);
    Map<String, Object> getMonthlyRevenueReport(int year, int month);
    Map<String, Object> getQuarterlyRevenueReport(int year, int quarter);
    Map<String, Object> getYearlyRevenueReport(int year);
    Map<String, Object> getPendingFeesReport();
    Map<String, Object> getOverdueFeesReport();
    List<StudentFeeAllocation> getStudentsWithPendingFees();
    List<StudentFeeAllocation> getStudentsWithOverdueFees();
}