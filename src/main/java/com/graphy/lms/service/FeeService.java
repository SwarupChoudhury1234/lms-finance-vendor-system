package com.graphy.lms.service;

import com.graphy.lms.entity.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface FeeService {
    
    // ============================================
    // 1. FEE TYPES CRUD
    // ============================================
    FeeType createFeeType(FeeType feeType);
    FeeType getFeeTypeById(Long id);
    List<FeeType> getAllFeeTypes();
    List<FeeType> getActiveFeeTypes();
    FeeType updateFeeType(Long id, FeeType feeType);
    void deleteFeeType(Long id);
    
    // ============================================
    // 2. FEE STRUCTURES CRUD
    // ============================================
    FeeStructure createFeeStructure(FeeStructure feeStructure);
    FeeStructure getFeeStructureById(Long id);
    List<FeeStructure> getAllFeeStructures();
    List<FeeStructure> getFeeStructuresByCourse(Long courseId);
    List<FeeStructure> getFeeStructuresByAcademicYear(String academicYear);
    List<FeeStructure> getFeeStructuresByBatch(Long batchId);
    FeeStructure updateFeeStructure(Long id, FeeStructure feeStructure);
    void deleteFeeStructure(Long id);
    
    // ============================================
    // 3. FEE DISCOUNTS CRUD
    // ============================================
    FeeDiscount createFeeDiscount(FeeDiscount feeDiscount);
    FeeDiscount getFeeDiscountById(Long id);
    List<FeeDiscount> getAllFeeDiscounts();
    List<FeeDiscount> getFeeDiscountsByUserId(Long userId);
    FeeDiscount updateFeeDiscount(Long id, FeeDiscount feeDiscount);
    void deleteFeeDiscount(Long id);
    
    // ============================================
    // 4. STUDENT FEE ALLOCATIONS CRUD + BUSINESS LOGIC
    // ============================================
    StudentFeeAllocation createStudentFeeAllocation(StudentFeeAllocation allocation);
    StudentFeeAllocation getFeeAllocationById(Long id);
    List<StudentFeeAllocation> getAllFeeAllocations();
    List<StudentFeeAllocation> getFeeAllocationsByUserId(Long userId);
    StudentFeeAllocation updateFeeAllocation(Long id, StudentFeeAllocation allocation);
    void deleteFeeAllocation(Long id);
    
    // Calculate payable amount after discounts
    BigDecimal calculatePayableAmount(Long userId, Long feeStructureId);
    
    // ============================================
    // 5. PAYMENT ALTERNATIVES CRUD
    // ============================================
    PaymentAlternative createPaymentAlternative(PaymentAlternative alternative);
    PaymentAlternative getPaymentAlternativeById(Long id);
    List<PaymentAlternative> getAllPaymentAlternatives();
    List<PaymentAlternative> getActivePaymentAlternatives();
    PaymentAlternative updatePaymentAlternative(Long id, PaymentAlternative alternative);
    void deletePaymentAlternative(Long id);
    
    // ============================================
    // 6. STUDENT INSTALLMENT PLANS CRUD + BUSINESS LOGIC
    // ============================================
    StudentInstallmentPlan createInstallmentPlan(StudentInstallmentPlan plan);
    StudentInstallmentPlan getInstallmentPlanById(Long id);
    List<StudentInstallmentPlan> getAllInstallmentPlans();
    List<StudentInstallmentPlan> getInstallmentPlansByAllocationId(Long allocationId);
    StudentInstallmentPlan updateInstallmentPlan(Long id, StudentInstallmentPlan plan);
    void deleteInstallmentPlan(Long id);
    
    // Create multiple installments for a student
 // Create multiple installments for a student (Ad-Hoc / Custom Plan)
    List<StudentInstallmentPlan> createInstallmentsForStudent(
            Long allocationId, 
            List<StudentInstallmentPlan> installmentDetails // <--- Updated Type & Removed alternativeId
    );
    // Reset installments
    List<StudentInstallmentPlan> resetInstallments(Long allocationId, Long alternativeId, List<Map<String, Object>> newInstallmentDetails);
    // Get overdue installments
    List<StudentInstallmentPlan> getOverdueInstallments();
    
    // ============================================
    // 7. STUDENT FEE PAYMENTS CRUD + BUSINESS LOGIC
    // ============================================
    StudentFeePayment createPayment(StudentFeePayment payment);
    StudentFeePayment getPaymentById(Long id);
    List<StudentFeePayment> getAllPayments();
    List<StudentFeePayment> getPaymentsByAllocationId(Long allocationId);
    StudentFeePayment updatePayment(Long id, StudentFeePayment payment);
    void deletePayment(Long id);
    
    // Process online payment
    StudentFeePayment processOnlinePayment(Long allocationId, 
            Long installmentPlanId, 
            BigDecimal amount, 
            String paymentMode, 
            String transactionRef, 
            String gatewayResponse,
            String screenshotUrl,
            String studentName,   // New Param
            String studentEmail); // New Param
    // Record manual payment
    
    // Update payment status after installment payment
    void updateInstallmentStatus(Long installmentPlanId, BigDecimal paidAmount);
    
    // ============================================
    // 8. LATE FEE CONFIG CRUD
    // ============================================
    LateFeeConfig createLateFeeConfig(LateFeeConfig config);
    LateFeeConfig getLateFeeConfigById(Long id);
    List<LateFeeConfig> getAllLateFeeConfigs();
    List<LateFeeConfig> getActiveLateFeeConfigs();
    LateFeeConfig updateLateFeeConfig(Long id, LateFeeConfig config);
    
    void deleteLateFeeConfig(Long id);
    
    // ============================================
    // 9. LATE FEE PENALTIES CRUD + AUTO CALCULATION
    // ============================================
    LateFeePenalty createLateFeePenalty(LateFeePenalty penalty);
    LateFeePenalty getLateFeePenaltyById(Long id);
    List<LateFeePenalty> getAllLateFeePenalties();
    List<LateFeePenalty> getLateFeePenaltiesByInstallmentId(Long installmentId);
    LateFeePenalty updateLateFeePenalty(Long id, LateFeePenalty penalty);
    void deleteLateFeePenalty(Long id);
    
    // Auto-apply late fees for overdue installments
    void applyLateFees(String manualEmail);
    void applyLateFees();
    
    // Waive late fee
    LateFeePenalty waiveLateFee(Long penaltyId, Long waivedBy);
    
    // ============================================
    // 10. ATTENDANCE PENALTIES CRUD + AUTO APPLICATION
    // ============================================
    AttendancePenalty createAttendancePenalty(AttendancePenalty penalty);
    AttendancePenalty getAttendancePenaltyById(Long id);
    List<AttendancePenalty> getAllAttendancePenalties();
    List<AttendancePenalty> getAttendancePenaltiesByUserId(Long userId);
    AttendancePenalty updateAttendancePenalty(Long id, AttendancePenalty penalty);
    void deleteAttendancePenalty(Long id);
    
    // Auto-apply attendance penalty when student is marked absent
    AttendancePenalty applyAttendancePenalty(Long userId, Long allocationId, 
                                             LocalDate absenceDate, BigDecimal penaltyAmount, 
                                             String reason, Long appliedBy);
    
    // ============================================
    // 11. EXAM FEE LINKAGE CRUD + AUTO LINKING
    // ============================================
    ExamFeeLinkage createExamFeeLinkage(ExamFeeLinkage linkage);
    ExamFeeLinkage getExamFeeLinkageById(Long id);
    List<ExamFeeLinkage> getAllExamFeeLinkages();
    List<ExamFeeLinkage> getExamFeeLinkagesByUserId(Long userId);
 // ... inside FeeService interface ...

    // Bulk Link Exam Fees (Batch / Course)
 // Old: ... String type ...
 // New:
 List<ExamFeeLinkage> linkExamFeeInBulk(Long examId, BigDecimal amount, BulkLinkType type, Long typeId);
 public enum BulkLinkType {
     BATCH,
     COURSE
 }
    ExamFeeLinkage updateExamFeeLinkage(Long id, ExamFeeLinkage linkage);
    void deleteExamFeeLinkage(Long id);
    
    // Auto-link exam fee when exam is scheduled
    ExamFeeLinkage linkExamFeeToStudent(Long examId, Long userId, Long allocationId, 
                                        BigDecimal examFeeAmount);
    
    // ============================================
    // 12. FEE REFUNDS CRUD + WORKFLOW
    // ============================================
    FeeRefund createRefundRequest(FeeRefund refund);
    FeeRefund getRefundById(Long id);
    List<FeeRefund> getAllRefunds();
    List<FeeRefund> getRefundsByUserId(Long userId);
    List<FeeRefund> getRefundsByStatus(FeeRefund.RefundStatus status);
    FeeRefund updateRefund(Long id, FeeRefund refund);
    void deleteRefund(Long id);
    
    // Refund workflow
    FeeRefund approveRefund(Long refundId, Long approvedBy);
    FeeRefund processRefund(Long refundId, String refundMode, String transactionRef);
    FeeRefund rejectRefund(Long refundId, Long rejectedBy, String reason);
    
 // ... existing code ...

    // Payment history per student (Raw)
    

    // 🔴 ADD THIS: Transformed History (Includes Online, Offline, Refunds & Labels)
    List<Map<String, Object>> getStudentTransactionHistory(Long userId);

    // ... existing code ...

    
    // ============================================
    // 13. FEE RECEIPTS (AUTO-GENERATED - READ ONLY)
    // ============================================
    FeeReceipt getReceiptById(Long id);
    List<FeeReceipt> getAllReceipts();
    List<FeeReceipt> getReceiptsByUserId(Long userId);
    FeeReceipt getReceiptByPaymentId(Long paymentId);
    
    // Auto-generate receipt after successful payment
    FeeReceipt generateReceipt(Long paymentId);
    FeeReceipt generateReceipt(Long paymentId, String manualEmail);
    
    // ============================================
    // 14. PAYMENT NOTIFICATIONS CRUD
    // ============================================
    PaymentNotification createNotification(PaymentNotification notification);
    PaymentNotification getNotificationById(Long id);
    List<PaymentNotification> getAllNotifications();
    List<PaymentNotification> getNotificationsByUserId(Long userId);
    PaymentNotification updateNotification(Long id, PaymentNotification notification);
    void deleteNotification(Long id);
    
    // Send notifications
    void sendPaymentSuccessNotification(Long userId, Long paymentId, String email);
    void sendPaymentFailedNotification(Long userId, String email, String reason);
    void sendDueReminderNotification(Long userId, Long installmentPlanId, String email);
    void sendOverdueWarningNotification(Long userId, Long installmentPlanId, String email);
    
    // ============================================
    // 15. AUTO DEBIT CONFIG CRUD
    // ============================================
    AutoDebitConfig createAutoDebitConfig(AutoDebitConfig config);
    AutoDebitConfig getAutoDebitConfigById(Long id);
    List<AutoDebitConfig> getAllAutoDebitConfigs();
    List<AutoDebitConfig> getAutoDebitConfigsByUserId(Long userId);
    AutoDebitConfig updateAutoDebitConfig(Long id, AutoDebitConfig config);
    void deleteAutoDebitConfig(Long id);
    
    // Process auto-debit
    
    
    // ============================================
    // 16. CURRENCY RATES CRUD
    // ============================================
    CurrencyRate createCurrencyRate(CurrencyRate rate);
    CurrencyRate getCurrencyRateById(Long id);
    List<CurrencyRate> getAllCurrencyRates();
    CurrencyRate updateCurrencyRate(Long id, CurrencyRate rate);
    void deleteCurrencyRate(Long id);
    
 // Existing method signature update
    void runAutoBlockCheck(BigDecimal blockThreshold);
    
    // Convert currency
    BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency, LocalDate date);
    
    StudentFeeAllocation createAllocationWithDiscount(
            StudentFeeAllocation allocation, 
            List<Long> discountIds // <--- List of IDs
    );

    // 2. Bulk (Updated to use IDs)
    List<StudentFeeAllocation> createBulkAllocation(
        List<Long> userIds,       
        Long feeStructureId,      
        List<Long> discountIds,    // <--- List of IDs (Templates)
        BigDecimal originalAmount,
        BigDecimal advancePayment
    );
    
    // ============================================
    // 17. AUDIT LOGS (AUTO-GENERATED - READ ONLY)
    // ============================================
    AuditLog getAuditLogById(Long id);
    List<AuditLog> getAllAuditLogs();
    List<AuditLog> getAuditLogsByModule(String module);
    List<AuditLog> getAuditLogsByEntity(String entityName, Long entityId);
    
    // ============================================
    // 18. CERTIFICATE BLOCK LIST CRUD
    // ============================================
    CertificateBlockList createCertificateBlock(CertificateBlockList block);
    CertificateBlockList getCertificateBlockById(Long id);
    List<CertificateBlockList> getAllCertificateBlocks();
    CertificateBlockList updateCertificateBlock(Long id, CertificateBlockList block);
    void deleteCertificateBlock(Long id);
    
    // Check if student can receive certificate
    boolean canIssueCertificate(Long userId);
    
    // Block certificate if fees unpaid
 // Remove 'BigDecimal pendingAmount' from here too
    public CertificateBlockList blockCertificate(Long userId, String reason, Long blockedBy);
    
    // Unblock certificate after payment
    void unblockCertificate(Long userId);
    
    // ============================================
    // REPORTS & ANALYTICS
    // ============================================
    
    // Student-wise report
    Map<String, Object> getStudentFeeReport(Long userId);
    
    // Batch-wise report
    Map<String, Object> getBatchFeeReport(Long batchId);
    
    // Course-wise report
    Map<String, Object> getCourseFeeReport(Long courseId);
    
    // Monthly revenue report
    Map<String, Object> getMonthlyRevenueReport(int year, int month);
    
    // Quarterly revenue report
    Map<String, Object> getQuarterlyRevenueReport(int year, int quarter);
    
    // Yearly revenue report
    Map<String, Object> getYearlyRevenueReport(int year);
    
    // Overall collection and pending
    Map<String, Object> getOverallFinancialSummary();
    
    Map<String, Object> getDashboardAnalytics(int year);
 // Dashboard: Recent Transactions Table
    List<Map<String, Object>> getRecentTransactions();
    
    // Payment history per student
    List<StudentFeePayment> getPaymentHistory(Long userId);
 // Add inside FeeService interface
    String createRazorpayOrder(Long allocationId, BigDecimal amount);
    boolean verifyRazorpayPayment(String orderId, String paymentId, String signature);
    FeeRefund getRefundByTransactionRef(String transactionRef);
 // Inside FeeService.java

    StudentFeePayment recordManualPayment(Long allocationId, Long installmentPlanId, 
            BigDecimal amount, String paymentMode, 
            String transactionRef, String screenshotUrl,
            Long recordedBy,
            String studentName, String studentEmail); // <--- Add these types
 // Inside FeeService.java interface

    // Existing method
    void processAutoDebit(String manualEmail);
    void processAutoDebit();

    // 🔴 NEW METHOD: Trigger for specific user with Email params
    void processAutoDebitForUser(Long userId, String studentName, String studentEmail);
}