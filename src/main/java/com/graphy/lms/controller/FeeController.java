package com.graphy.lms.controller;

import com.graphy.lms.entity.*;
import com.graphy.lms.service.FeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/finance")
public class FeeController {
    
    @Autowired
    private FeeService feeService;
    
    // ========================================================================
    // 1. FEE TYPES (Access: Admin only for POST/PUT/DELETE)
    // ========================================================================
    @PostMapping("/types")
    public ResponseEntity<FeeType> createFeeType(
            @RequestBody FeeType feeType,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        FeeType created = feeService.createFeeType(feeType, userId, role);
        return ResponseEntity.ok(created);
    }
    
    @GetMapping("/types")
    public ResponseEntity<List<FeeType>> getAllFeeTypes(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        List<FeeType> feeTypes = feeService.getAllFeeTypes(userId, role);
        return ResponseEntity.ok(feeTypes);
    }
    
    @GetMapping("/types/active")
    public ResponseEntity<List<FeeType>> getActiveFeeTypes() {
        List<FeeType> feeTypes = feeService.getActiveFeeTypes();
        return ResponseEntity.ok(feeTypes);
    }
    
    @GetMapping("/types/{id}")
    public ResponseEntity<FeeType> getFeeTypeById(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        FeeType feeType = feeService.getFeeTypeById(id, userId, role);
        return ResponseEntity.ok(feeType);
    }
    
    @PutMapping("/types/{id}")
    public ResponseEntity<FeeType> updateFeeType(
            @PathVariable Long id,
            @RequestBody FeeType feeType,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        FeeType updated = feeService.updateFeeType(id, feeType, userId, role);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/types/{id}")
    public ResponseEntity<String> deleteFeeType(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        feeService.deleteFeeType(id, userId, role);
        return ResponseEntity.ok("FeeType deleted successfully");
    }
    
    // ========================================================================
    // 2. FEE STRUCTURES (Access: Admin only for POST/PUT/DELETE)
    // ========================================================================
    @PostMapping("/structures")
    public ResponseEntity<FeeStructure> createFeeStructure(
            @RequestBody FeeStructure feeStructure,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        FeeStructure created = feeService.createFeeStructure(feeStructure, userId, role);
        return ResponseEntity.ok(created);
    }
    
    @GetMapping("/structures")
    public ResponseEntity<List<FeeStructure>> getFeeStructures(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String academicYear) {
        List<FeeStructure> structures = feeService.getFeeStructures(userId, role, courseId, academicYear);
        return ResponseEntity.ok(structures);
    }
    
    @GetMapping("/structures/{id}")
    public ResponseEntity<FeeStructure> getFeeStructureById(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        FeeStructure structure = feeService.getFeeStructureById(id, userId, role);
        return ResponseEntity.ok(structure);
    }
    
    @PutMapping("/structures/{id}")
    public ResponseEntity<FeeStructure> updateFeeStructure(
            @PathVariable Long id,
            @RequestBody FeeStructure feeStructure,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        FeeStructure updated = feeService.updateFeeStructure(id, feeStructure, userId, role);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/structures/{id}")
    public ResponseEntity<String> deleteFeeStructure(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        feeService.deleteFeeStructure(id, userId, role);
        return ResponseEntity.ok("FeeStructure deleted successfully");
    }
    
    // ========================================================================
    // 3. STUDENT FEE ALLOCATIONS (Your installment logic)
    // ========================================================================
    @PostMapping("/allocations")
    public ResponseEntity<StudentFeeAllocation> allocateFee(
            @RequestBody StudentFeeAllocation allocation,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        StudentFeeAllocation created = feeService.allocateFee(allocation, userId, role);
        return ResponseEntity.ok(created);
    }
    
    @GetMapping("/allocations")
    public ResponseEntity<List<StudentFeeAllocation>> getAllocations(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role,
            @RequestParam(required = false) Long studentId) {
        List<StudentFeeAllocation> allocations = feeService.getAllocations(userId, role, studentId);
        return ResponseEntity.ok(allocations);
    }
    
    @GetMapping("/allocations/{id}")
    public ResponseEntity<StudentFeeAllocation> getAllocationById(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        StudentFeeAllocation allocation = feeService.getAllocationById(id, userId, role);
        return ResponseEntity.ok(allocation);
    }
    
    @PutMapping("/allocations/{id}")
    public ResponseEntity<StudentFeeAllocation> updateAllocation(
            @PathVariable Long id,
            @RequestBody StudentFeeAllocation allocation,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        StudentFeeAllocation updated = feeService.updateAllocation(id, allocation, userId, role);
        return ResponseEntity.ok(updated);
    }
    
    // ========================================================================
    // NEW PAYMENT ALTERNATIVES ENDPOINTS
    // ========================================================================
    
    // Admin sets advance payment
    @PostMapping("/allocations/{id}/advance")
    public ResponseEntity<StudentFeeAllocation> setAdvancePayment(
            @PathVariable Long id,
            @RequestParam BigDecimal advancePayment,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        StudentFeeAllocation updated = feeService.setAdvancePayment(id, advancePayment, userId, role);
        return ResponseEntity.ok(updated);
    }
    
    // Student selects payment plan with custom amounts
    @PostMapping("/allocations/{id}/select-plan")
    public ResponseEntity<StudentFeeAllocation> selectPaymentPlan(
            @PathVariable Long id,
            @RequestParam Long alternativeId,
            @RequestBody List<BigDecimal> customAmounts,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        StudentFeeAllocation plan = feeService.selectPaymentPlan(id, alternativeId, customAmounts, userId, role);
        return ResponseEntity.ok(plan);
    }
    
    // Admin recalculates installments (student request)
    @PostMapping("/allocations/{id}/recalculate")
    public ResponseEntity<String> recalculateInstallments(
            @PathVariable Long id,
            @RequestBody List<BigDecimal> newAmounts,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        feeService.recalculateInstallments(id, newAmounts, userId, role);
        return ResponseEntity.ok("Installments recalculated successfully");
    }
    
    // ========================================================================
    // 4. PAYMENT INSTALLMENTS
    // ========================================================================
    @GetMapping("/allocations/{allocationId}/installments")
    public ResponseEntity<List<PaymentInstallment>> getInstallmentsByAllocation(
            @PathVariable Long allocationId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        List<PaymentInstallment> installments = feeService.getInstallmentsByAllocationId(allocationId, userId, role);
        return ResponseEntity.ok(installments);
    }
    
    // ========================================================================
    // 5. PAYMENT ALTERNATIVES CRUD
    // ========================================================================
    @PostMapping("/payment-alternatives")
    public ResponseEntity<PaymentAlternative> createPaymentAlternative(
            @RequestBody PaymentAlternative alternative,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        PaymentAlternative created = feeService.createPaymentAlternative(alternative, userId, role);
        return ResponseEntity.ok(created);
    }
    
    @GetMapping("/payment-alternatives")
    public ResponseEntity<List<PaymentAlternative>> getAllPaymentAlternatives(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        List<PaymentAlternative> alternatives = feeService.getAllPaymentAlternatives(userId, role);
        return ResponseEntity.ok(alternatives);
    }
    
    @GetMapping("/payment-alternatives/active")
    public ResponseEntity<List<PaymentAlternative>> getActivePaymentAlternatives() {
        List<PaymentAlternative> alternatives = feeService.getActivePaymentAlternatives();
        return ResponseEntity.ok(alternatives);
    }
    
    @GetMapping("/payment-alternatives/{id}")
    public ResponseEntity<PaymentAlternative> getPaymentAlternativeById(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        PaymentAlternative alternative = feeService.getPaymentAlternativeById(id, userId, role);
        return ResponseEntity.ok(alternative);
    }
    
    @PutMapping("/payment-alternatives/{id}")
    public ResponseEntity<PaymentAlternative> updatePaymentAlternative(
            @PathVariable Long id,
            @RequestBody PaymentAlternative alternative,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        PaymentAlternative updated = feeService.updatePaymentAlternative(id, alternative, userId, role);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/payment-alternatives/{id}")
    public ResponseEntity<String> deletePaymentAlternative(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        feeService.deletePaymentAlternative(id, userId, role);
        return ResponseEntity.ok("Payment alternative deleted successfully");
    }
    
    // ========================================================================
    // 6. STUDENT FEE PAYMENTS (Student can POST)
    // ========================================================================
    @PostMapping("/payments")
    public ResponseEntity<StudentFeePayment> processPayment(
            @RequestBody StudentFeePayment payment,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        StudentFeePayment processed = feeService.processPayment(payment, userId, role);
        return ResponseEntity.ok(processed);
    }
    
    @GetMapping("/payments")
    public ResponseEntity<List<StudentFeePayment>> getPayments(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        List<StudentFeePayment> payments = feeService.getPayments(userId, role);
        return ResponseEntity.ok(payments);
    }
    
    @GetMapping("/payments/{id}")
    public ResponseEntity<StudentFeePayment> getPaymentById(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        StudentFeePayment payment = feeService.getPaymentById(id, userId, role);
        return ResponseEntity.ok(payment);
    }
    
    // ========================================================================
    // 7. FEE DISCOUNTS
    // ========================================================================
    @PostMapping("/discounts")
    public ResponseEntity<FeeDiscount> applyDiscount(
            @RequestBody FeeDiscount discount,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        FeeDiscount applied = feeService.applyDiscount(discount, userId, role);
        return ResponseEntity.ok(applied);
    }
    
    @GetMapping("/discounts")
    public ResponseEntity<List<FeeDiscount>> getDiscounts(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        List<FeeDiscount> discounts = feeService.getDiscounts(userId, role);
        return ResponseEntity.ok(discounts);
    }
    
    @GetMapping("/discounts/{id}")
    public ResponseEntity<FeeDiscount> getDiscountById(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        FeeDiscount discount = feeService.getDiscountById(id, userId, role);
        return ResponseEntity.ok(discount);
    }
    
    @PutMapping("/discounts/{id}")
    public ResponseEntity<FeeDiscount> updateDiscount(
            @PathVariable Long id,
            @RequestBody FeeDiscount discount,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        FeeDiscount updated = feeService.updateDiscount(id, discount, userId, role);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/discounts/{id}")
    public ResponseEntity<String> deleteDiscount(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        feeService.deleteDiscount(id, userId, role);
        return ResponseEntity.ok("Discount deleted successfully");
    }
    
    // ========================================================================
    // 8. FEE REFUNDS (Admin only)
    // ========================================================================
    @PostMapping("/refunds")
    public ResponseEntity<FeeRefund> processRefund(
            @RequestBody FeeRefund refund,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        FeeRefund processed = feeService.processRefund(refund, userId, role);
        return ResponseEntity.ok(processed);
    }
    
    @GetMapping("/refunds")
    public ResponseEntity<List<FeeRefund>> getRefunds(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        List<FeeRefund> refunds = feeService.getRefunds(userId, role);
        return ResponseEntity.ok(refunds);
    }
    
    @GetMapping("/refunds/{id}")
    public ResponseEntity<FeeRefund> getRefundById(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        FeeRefund refund = feeService.getRefundById(id, userId, role);
        return ResponseEntity.ok(refund);
    }
    
    // ========================================================================
    // 9. FEE RECEIPTS (Auto-generated, read-only)
    // ========================================================================
    @GetMapping("/receipts")
    public ResponseEntity<List<FeeReceipt>> getReceipts(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        List<FeeReceipt> receipts = feeService.getReceipts(userId, role);
        return ResponseEntity.ok(receipts);
    }
    
    @GetMapping("/receipts/{id}")
    public ResponseEntity<FeeReceipt> getReceiptById(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        FeeReceipt receipt = feeService.getReceiptById(id, userId, role);
        return ResponseEntity.ok(receipt);
    }
    
    // ========================================================================
    // 10. AUDIT LOGS (Admin only, read-only)
    // ========================================================================
    @GetMapping("/audit")
    public ResponseEntity<List<AuditLog>> getAuditLogs(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        List<AuditLog> auditLogs = feeService.getAuditLogs(userId, role);
        return ResponseEntity.ok(auditLogs);
    }
    
    @GetMapping("/audit/{id}")
    public ResponseEntity<AuditLog> getAuditLogById(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        AuditLog auditLog = feeService.getAuditLogById(id, userId, role);
        return ResponseEntity.ok(auditLog);
    }
    
    // ========================================================================
    // 11. OTHER ENTITIES - BASIC ENDPOINTS
    // ========================================================================
    
    // Currency Rates
    @PostMapping("/currency-rates")
    public ResponseEntity<CurrencyRate> createCurrencyRate(
            @RequestBody CurrencyRate rate,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        CurrencyRate created = feeService.createCurrencyRate(rate, userId, role);
        return ResponseEntity.ok(created);
    }
    
    @GetMapping("/currency-rates")
    public ResponseEntity<List<CurrencyRate>> getCurrencyRates(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        List<CurrencyRate> rates = feeService.getCurrencyRates(userId, role);
        return ResponseEntity.ok(rates);
    }
    
    // Notification Logs (Auto-generated)
    @PostMapping("/notifications")
    public ResponseEntity<NotificationLog> createNotificationLog(
            @RequestBody NotificationLog log,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        NotificationLog created = feeService.createNotificationLog(log, userId, role);
        return ResponseEntity.ok(created);
    }
    
    // Attendance Penalties
    @PostMapping("/attendance-penalties")
    public ResponseEntity<AttendancePenalty> createAttendancePenalty(
            @RequestBody AttendancePenalty penalty,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        AttendancePenalty created = feeService.createAttendancePenalty(penalty, userId, role);
        return ResponseEntity.ok(created);
    }
    
    // Certificate Blocks
    @PostMapping("/certificate-blocks")
    public ResponseEntity<CertificateBlock> createCertificateBlock(
            @RequestBody CertificateBlock block,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        CertificateBlock created = feeService.createCertificateBlock(block, userId, role);
        return ResponseEntity.ok(created);
    }
    
    // Auto Debit Settings
    @PostMapping("/auto-debit")
    public ResponseEntity<AutoDebitSetting> createAutoDebitSetting(
            @RequestBody AutoDebitSetting setting,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        AutoDebitSetting created = feeService.createAutoDebitSetting(setting, userId, role);
        return ResponseEntity.ok(created);
    }
    
    // Fee Reports
    @PostMapping("/reports")
    public ResponseEntity<FeeReport> generateFeeReport(
            @RequestBody FeeReport report,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        FeeReport generated = feeService.generateFeeReport(report, userId, role);
        return ResponseEntity.ok(generated);
    }
    
    // ========================================================================
    // HEALTH CHECK ENDPOINT
    // ========================================================================
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Fee Management API is running");
    }
}