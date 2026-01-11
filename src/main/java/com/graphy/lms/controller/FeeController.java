package com.graphy.lms.controller;

import com.graphy.lms.entity.*;
import org.springframework.security.access.prepost.PreAuthorize;

import com.graphy.lms.service.FeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fee")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FeeController {
    
    private final FeeService feeManagementService;
    
    // Helper method to extract user ID from token (in real implementation)
    // For now, we'll use a request header
    private Long getPerformedBy(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return userId != null ? userId : 1L; // Default to 1 for testing
    }
    
    // ========================================
    // FEE TYPE ENDPOINTS
    // ========================================
    @PostMapping("/types")
    public ResponseEntity<FeeType> createFeeType(
            @RequestBody FeeType feeType,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        FeeType created = feeManagementService.createFeeType(feeType, getPerformedBy(userId));
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @GetMapping("/types")
    public ResponseEntity<List<FeeType>> getAllFeeTypes() {
        return ResponseEntity.ok(feeManagementService.getAllFeeTypes());
    }
    
    @GetMapping("/types/active")
    public ResponseEntity<List<FeeType>> getActiveFeeTypes() {
        return ResponseEntity.ok(feeManagementService.getActiveFeeTypes());
    }
    
    @GetMapping("/types/{id}")
    public ResponseEntity<FeeType> getFeeTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(feeManagementService.getFeeTypeById(id));
    }
    
    @PutMapping("/types/{id}")
    public ResponseEntity<FeeType> updateFeeType(
            @PathVariable Long id,
            @RequestBody FeeType feeType,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        FeeType updated = feeManagementService.updateFeeType(id, feeType, getPerformedBy(userId));
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/types/{id}")
    public ResponseEntity<Void> deleteFeeType(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        feeManagementService.deleteFeeType(id, getPerformedBy(userId));
        return ResponseEntity.noContent().build();
    }
    
    // ========================================
    // FEE STRUCTURE ENDPOINTS
    // ========================================
    @PostMapping("/structures")
    public ResponseEntity<FeeStructure> createFeeStructure(
            @RequestBody FeeStructure feeStructure,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        FeeStructure created = feeManagementService.createFeeStructure(feeStructure, getPerformedBy(userId));
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @GetMapping("/structures")
    public ResponseEntity<List<FeeStructure>> getAllFeeStructures() {
        return ResponseEntity.ok(feeManagementService.getAllFeeStructures());
    }
    
    @GetMapping("/structures/{id}")
    public ResponseEntity<FeeStructure> getFeeStructureById(@PathVariable Long id) {
        return ResponseEntity.ok(feeManagementService.getFeeStructureById(id));
    }
    
    @GetMapping("/structures/course/{courseId}/year/{academicYear}")
    public ResponseEntity<List<FeeStructure>> getFeeStructuresByCourseAndYear(
            @PathVariable Long courseId,
            @PathVariable String academicYear) {
        return ResponseEntity.ok(feeManagementService.getFeeStructuresByCourseAndYear(courseId, academicYear));
    }
    
    @PutMapping("/structures/{id}")
    public ResponseEntity<FeeStructure> updateFeeStructure(
            @PathVariable Long id,
            @RequestBody FeeStructure feeStructure,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        FeeStructure updated = feeManagementService.updateFeeStructure(id, feeStructure, getPerformedBy(userId));
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/structures/{id}")
    public ResponseEntity<Void> deleteFeeStructure(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        feeManagementService.deleteFeeStructure(id, getPerformedBy(userId));
        return ResponseEntity.noContent().build();
    }
    
    // ========================================
    // STUDENT FEE ALLOCATION ENDPOINTS
    // ========================================
    @PostMapping("/allocations")
    public ResponseEntity<StudentFeeAllocation> createStudentFeeAllocation(
            @RequestBody StudentFeeAllocation allocation,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        StudentFeeAllocation created = feeManagementService.createStudentFeeAllocation(allocation, getPerformedBy(userId));
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @GetMapping("/allocations")
    public ResponseEntity<List<StudentFeeAllocation>> getAllStudentFeeAllocations() {
        return ResponseEntity.ok(feeManagementService.getAllStudentFeeAllocations());
    }
    
    @GetMapping("/allocations/{id}")
    public ResponseEntity<StudentFeeAllocation> getStudentFeeAllocationById(@PathVariable Long id) {
        return ResponseEntity.ok(feeManagementService.getStudentFeeAllocationById(id));
    }
    
    @GetMapping("/allocations/user/{userId}")
    public ResponseEntity<List<StudentFeeAllocation>> getStudentFeeAllocationsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(feeManagementService.getStudentFeeAllocationsByUserId(userId));
    }
    
    @PutMapping("/allocations/{id}")
    public ResponseEntity<StudentFeeAllocation> updateStudentFeeAllocation(
            @PathVariable Long id,
            @RequestBody StudentFeeAllocation allocation,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        StudentFeeAllocation updated = feeManagementService.updateStudentFeeAllocation(id, allocation, getPerformedBy(userId));
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/allocations/{id}")
    public ResponseEntity<Void> deleteStudentFeeAllocation(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        feeManagementService.deleteStudentFeeAllocation(id, getPerformedBy(userId));
        return ResponseEntity.noContent().build();
    }
    
    // Special allocation endpoints
    @PostMapping("/allocations/allocate")
    public ResponseEntity<StudentFeeAllocation> allocateFeeToStudent(
            @RequestParam Long userId,
            @RequestParam Long feeStructureId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
            @RequestParam BigDecimal initialPayment,
            @RequestParam Integer numberOfInstallments,
            @RequestHeader(value = "X-User-Id", required = false) Long performedById) {
        StudentFeeAllocation allocated = feeManagementService.allocateFeeToStudent(
            userId, feeStructureId, dueDate, initialPayment, numberOfInstallments, getPerformedBy(performedById)
        );
        return new ResponseEntity<>(allocated, HttpStatus.CREATED);
    }
    
    @PostMapping("/allocations/{allocationId}/apply-discount/{discountId}")
    public ResponseEntity<Void> applyDiscountToAllocation(
            @PathVariable Long allocationId,
            @PathVariable Long discountId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        feeManagementService.applyDiscountToAllocation(allocationId, discountId, getPerformedBy(userId));
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/allocations/{allocationId}/adjust-installments")
    public ResponseEntity<Void> adjustInstallmentPlan(
            @PathVariable Long allocationId,
            @RequestBody List<BigDecimal> newInstallmentAmounts,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        feeManagementService.adjustInstallmentPlan(allocationId, newInstallmentAmounts, getPerformedBy(userId));
        return ResponseEntity.ok().build();
    }
    
    // ========================================
    // FEE INSTALLMENT PLAN ENDPOINTS
    // ========================================
    @PostMapping("/installments")
    public ResponseEntity<FeeInstallmentPlan> createFeeInstallmentPlan(
            @RequestBody FeeInstallmentPlan installmentPlan,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        FeeInstallmentPlan created = feeManagementService.createFeeInstallmentPlan(installmentPlan, getPerformedBy(userId));
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @GetMapping("/installments")
    public ResponseEntity<List<FeeInstallmentPlan>> getAllFeeInstallmentPlans() {
        return ResponseEntity.ok(feeManagementService.getAllFeeInstallmentPlans());
    }
    
    @GetMapping("/installments/{id}")
    public ResponseEntity<FeeInstallmentPlan> getFeeInstallmentPlanById(@PathVariable Long id) {
        return ResponseEntity.ok(feeManagementService.getFeeInstallmentPlanById(id));
    }
    
    @GetMapping("/installments/allocation/{allocationId}")
    public ResponseEntity<List<FeeInstallmentPlan>> getInstallmentPlansByAllocationId(@PathVariable Long allocationId) {
        return ResponseEntity.ok(feeManagementService.getInstallmentPlansByAllocationId(allocationId));
    }
    
    @PutMapping("/installments/{id}")
    public ResponseEntity<FeeInstallmentPlan> updateFeeInstallmentPlan(
            @PathVariable Long id,
            @RequestBody FeeInstallmentPlan installmentPlan,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        FeeInstallmentPlan updated = feeManagementService.updateFeeInstallmentPlan(id, installmentPlan, getPerformedBy(userId));
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/installments/{id}")
    public ResponseEntity<Void> deleteFeeInstallmentPlan(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        feeManagementService.deleteFeeInstallmentPlan(id, getPerformedBy(userId));
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/installments/update-overdue")
    public ResponseEntity<Void> updateOverdueInstallments() {
        feeManagementService.updateOverdueInstallments();
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/installments/calculate-late-fees/{allocationId}")
    public ResponseEntity<Void> calculateLateFees(@PathVariable Long allocationId) {
        feeManagementService.calculateLateFees(allocationId);
        return ResponseEntity.ok().build();
    }
    
    // ========================================
    // STUDENT FEE PAYMENT ENDPOINTS
    // ========================================
    @PostMapping("/payments")
    public ResponseEntity<StudentFeePayment> createStudentFeePayment(
            @RequestBody StudentFeePayment payment,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        payment.setCollectedBy(getPerformedBy(userId));
        StudentFeePayment created = feeManagementService.createStudentFeePayment(payment, getPerformedBy(userId));
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @GetMapping("/payments")
    public ResponseEntity<List<StudentFeePayment>> getAllStudentFeePayments() {
        return ResponseEntity.ok(feeManagementService.getAllStudentFeePayments());
    }
    
    @GetMapping("/payments/{id}")
    public ResponseEntity<StudentFeePayment> getStudentFeePaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(feeManagementService.getStudentFeePaymentById(id));
    }
    
    @GetMapping("/payments/allocation/{allocationId}")
    public ResponseEntity<List<StudentFeePayment>> getPaymentsByAllocationId(@PathVariable Long allocationId) {
        return ResponseEntity.ok(feeManagementService.getPaymentsByAllocationId(allocationId));
    }
    
    @GetMapping("/payments/history/{userId}")
    public ResponseEntity<List<StudentFeePayment>> getPaymentHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(feeManagementService.getPaymentHistory(userId));
    }
    
    @PutMapping("/payments/{id}")
    public ResponseEntity<StudentFeePayment> updateStudentFeePayment(
            @PathVariable Long id,
            @RequestBody StudentFeePayment payment,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        StudentFeePayment updated = feeManagementService.updateStudentFeePayment(id, payment, getPerformedBy(userId));
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/payments/{id}")
    public ResponseEntity<Void> deleteStudentFeePayment(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        feeManagementService.deleteStudentFeePayment(id, getPerformedBy(userId));
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/payments/record")
    public ResponseEntity<StudentFeePayment> recordPayment(
            @RequestParam Long allocationId,
            @RequestParam BigDecimal amount,
            @RequestParam String paymentMode,
            @RequestParam(required = false) String transactionRef,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        StudentFeePayment payment = feeManagementService.recordPayment(
            allocationId, amount, paymentMode, transactionRef, getPerformedBy(userId)
        );
        return new ResponseEntity<>(payment, HttpStatus.CREATED);
    }
    
    @PostMapping("/payments/record-installment")
    public ResponseEntity<StudentFeePayment> recordInstallmentPayment(
            @RequestParam Long installmentPlanId,
            @RequestParam BigDecimal amount,
            @RequestParam String paymentMode,
            @RequestParam(required = false) String transactionRef,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        StudentFeePayment payment = feeManagementService.recordInstallmentPayment(
            installmentPlanId, amount, paymentMode, transactionRef, getPerformedBy(userId)
        );
        return new ResponseEntity<>(payment, HttpStatus.CREATED);
    }
    
    // ========================================
    // FEE DISCOUNT ENDPOINTS
    // ========================================
    @PostMapping("/discounts")
    public ResponseEntity<FeeDiscount> createFeeDiscount(
            @RequestBody FeeDiscount discount,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        FeeDiscount created = feeManagementService.createFeeDiscount(discount, getPerformedBy(userId));
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @GetMapping("/discounts")
    public ResponseEntity<List<FeeDiscount>> getAllFeeDiscounts() {
        return ResponseEntity.ok(feeManagementService.getAllFeeDiscounts());
    }
    
    @GetMapping("/discounts/{id}")
    public ResponseEntity<FeeDiscount> getFeeDiscountById(@PathVariable Long id) {
        return ResponseEntity.ok(feeManagementService.getFeeDiscountById(id));
    }
    
    @GetMapping("/discounts/user/{userId}")
    public ResponseEntity<List<FeeDiscount>> getDiscountsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(feeManagementService.getDiscountsByUserId(userId));
    }
    
    @PutMapping("/discounts/{id}")
    public ResponseEntity<FeeDiscount> updateFeeDiscount(
            @PathVariable Long id,
            @RequestBody FeeDiscount discount,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        FeeDiscount updated = feeManagementService.updateFeeDiscount(id, discount, getPerformedBy(userId));
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/discounts/{id}")
    public ResponseEntity<Void> deleteFeeDiscount(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        feeManagementService.deleteFeeDiscount(id, getPerformedBy(userId));
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/discounts/{id}/approve")
    public ResponseEntity<FeeDiscount> approveDiscount(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        FeeDiscount approved = feeManagementService.approveDiscount(id, getPerformedBy(userId));
        return ResponseEntity.ok(approved);
    }
    
    @PostMapping("/discounts/{id}/reject")
    public ResponseEntity<FeeDiscount> rejectDiscount(
            @PathVariable Long id,
            @RequestParam String reason,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        FeeDiscount rejected = feeManagementService.rejectDiscount(id, getPerformedBy(userId), reason);
        return ResponseEntity.ok(rejected);
    }
    
    // ========================================
    // FEE REFUND ENDPOINTS
    // ========================================
    @PostMapping("/refunds")
    public ResponseEntity<FeeRefund> createFeeRefund(
            @RequestBody FeeRefund refund,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        FeeRefund created = feeManagementService.createFeeRefund(refund, getPerformedBy(userId));
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @GetMapping("/refunds")
    public ResponseEntity<List<FeeRefund>> getAllFeeRefunds() {
        return ResponseEntity.ok(feeManagementService.getAllFeeRefunds());
    }
    
    @GetMapping("/refunds/{id}")
    public ResponseEntity<FeeRefund> getFeeRefundById(@PathVariable Long id) {
        return ResponseEntity.ok(feeManagementService.getFeeRefundById(id));
    }
    
    @GetMapping("/refunds/user/{userId}")
    public ResponseEntity<List<FeeRefund>> getRefundsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(feeManagementService.getRefundsByUserId(userId));
    }
    
    @PutMapping("/refunds/{id}")
    public ResponseEntity<FeeRefund> updateFeeRefund(
            @PathVariable Long id,
            @RequestBody FeeRefund refund,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        FeeRefund updated = feeManagementService.updateFeeRefund(id, refund, getPerformedBy(userId));
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/refunds/{id}")
    public ResponseEntity<Void> deleteFeeRefund(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        feeManagementService.deleteFeeRefund(id, getPerformedBy(userId));
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/refunds/{id}/approve")
    public ResponseEntity<FeeRefund> approveRefund(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        FeeRefund approved = feeManagementService.approveRefund(id, getPerformedBy(userId));
        return ResponseEntity.ok(approved);
    }
    
    @PostMapping("/refunds/{id}/reject")
    public ResponseEntity<FeeRefund> rejectRefund(
            @PathVariable Long id,
            @RequestParam String reason,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        FeeRefund rejected = feeManagementService.rejectRefund(id, getPerformedBy(userId), reason);
        return ResponseEntity.ok(rejected);
    }
    
    @PostMapping("/refunds/{id}/process")
    public ResponseEntity<FeeRefund> processRefund(
            @PathVariable Long id,
            @RequestParam String transactionRef,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        FeeRefund processed = feeManagementService.processRefund(id, transactionRef, getPerformedBy(userId));
        return ResponseEntity.ok(processed);
    }
    
    // ========================================
    // FEE RECEIPT ENDPOINTS (READ-ONLY)
    // ========================================
    @GetMapping("/receipts")
    public ResponseEntity<List<FeeReceipt>> getAllFeeReceipts() {
        return ResponseEntity.ok(feeManagementService.getAllFeeReceipts());
    }
    
    @GetMapping("/receipts/{id}")
    public ResponseEntity<FeeReceipt> getFeeReceiptById(@PathVariable Long id) {
        return ResponseEntity.ok(feeManagementService.getFeeReceiptById(id));
    }
    
    @GetMapping("/receipts/payment/{paymentId}")
    public ResponseEntity<FeeReceipt> getFeeReceiptByPaymentId(@PathVariable Long paymentId) {
        return ResponseEntity.ok(feeManagementService.getFeeReceiptByPaymentId(paymentId));
    }
    
    @GetMapping("/receipts/student/{userId}")
    public ResponseEntity<List<FeeReceipt>> getReceiptsByStudentUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(feeManagementService.getReceiptsByStudentUserId(userId));
    }
    
    @PostMapping("/receipts/send-email/{receiptId}")
    public ResponseEntity<Void> sendReceiptEmail(
            @PathVariable Long receiptId,
            @RequestParam String studentEmail) {
        feeManagementService.sendReceiptEmail(receiptId, studentEmail);
        return ResponseEntity.ok().build();
    }
    
    // ========================================
    // AUDIT LOG ENDPOINTS (READ-ONLY)
    // ========================================
    @GetMapping("/audit-logs")
    public ResponseEntity<List<AuditLog>> getAllAuditLogs() {
        return ResponseEntity.ok(feeManagementService.getAllAuditLogs());
    }
    
    @GetMapping("/audit-logs/{id}")
    public ResponseEntity<AuditLog> getAuditLogById(@PathVariable Long id) {
        return ResponseEntity.ok(feeManagementService.getAuditLogById(id));
    }
    
    @GetMapping("/audit-logs/module/{module}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByModule(@PathVariable String module) {
        return ResponseEntity.ok(feeManagementService.getAuditLogsByModule(module));
    }
    
    @GetMapping("/audit-logs/entity/{entityType}/{entityId}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        return ResponseEntity.ok(feeManagementService.getAuditLogsByEntity(entityType, entityId));
    }
    
    // ========================================
    // LATE FEE RULE ENDPOINTS
    // ========================================
    @PostMapping("/late-fee-rules")
    public ResponseEntity<LateFeeRule> createLateFeeRule(
            @RequestBody LateFeeRule lateFeeRule,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        LateFeeRule created = feeManagementService.createLateFeeRule(lateFeeRule, getPerformedBy(userId));
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @GetMapping("/late-fee-rules")
    public ResponseEntity<List<LateFeeRule>> getAllLateFeeRules() {
        return ResponseEntity.ok(feeManagementService.getAllLateFeeRules());
    }
    
    @GetMapping("/late-fee-rules/{id}")
    public ResponseEntity<LateFeeRule> getLateFeeRuleById(@PathVariable Long id) {
        return ResponseEntity.ok(feeManagementService.getLateFeeRuleById(id));
    }
    
    @PutMapping("/late-fee-rules/{id}")
    public ResponseEntity<LateFeeRule> updateLateFeeRule(
            @PathVariable Long id,
            @RequestBody LateFeeRule lateFeeRule,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        LateFeeRule updated = feeManagementService.updateLateFeeRule(id, lateFeeRule, getPerformedBy(userId));
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/late-fee-rules/{id}")
    public ResponseEntity<Void> deleteLateFeeRule(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        feeManagementService.deleteLateFeeRule(id, getPerformedBy(userId));
        return ResponseEntity.noContent().build();
    }
    
    // ========================================
    // REPORTING ENDPOINTS
    // ========================================
    @GetMapping("/reports/student/{userId}")
    public ResponseEntity<Map<String, Object>> getStudentFeeReport(@PathVariable Long userId) {
        return ResponseEntity.ok(feeManagementService.getStudentFeeReport(userId));
    }
    
    @GetMapping("/reports/batch/{batchId}")
    public ResponseEntity<Map<String, Object>> getBatchFeeReport(@PathVariable Long batchId) {
        return ResponseEntity.ok(feeManagementService.getBatchFeeReport(batchId));
    }
    
    @GetMapping("/reports/course/{courseId}")
    public ResponseEntity<Map<String, Object>> getCourseFeeReport(@PathVariable Long courseId) {
        return ResponseEntity.ok(feeManagementService.getCourseFeeReport(courseId));
    }
    
    @GetMapping("/reports/revenue")
    public ResponseEntity<Map<String, Object>> getRevenueReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(feeManagementService.getRevenueReport(startDate, endDate));
    }
    
    @GetMapping("/reports/revenue/monthly")
    public ResponseEntity<Map<String, Object>> getMonthlyRevenueReport(
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(feeManagementService.getMonthlyRevenueReport(year, month));
    }
    
    @GetMapping("/reports/revenue/quarterly")
    public ResponseEntity<Map<String, Object>> getQuarterlyRevenueReport(
            @RequestParam int year,
            @RequestParam int quarter) {
        return ResponseEntity.ok(feeManagementService.getQuarterlyRevenueReport(year, quarter));
    }
    
    @GetMapping("/reports/revenue/yearly")
    public ResponseEntity<Map<String, Object>> getYearlyRevenueReport(@RequestParam int year) {
        return ResponseEntity.ok(feeManagementService.getYearlyRevenueReport(year));
    }
    
    @GetMapping("/reports/pending")
    public ResponseEntity<Map<String, Object>> getPendingFeesReport() {
        return ResponseEntity.ok(feeManagementService.getPendingFeesReport());
    }
    
    @GetMapping("/reports/overdue")
    public ResponseEntity<Map<String, Object>> getOverdueFeesReport() {
        return ResponseEntity.ok(feeManagementService.getOverdueFeesReport());
    }
    
    @GetMapping("/reports/students-pending")
    public ResponseEntity<List<StudentFeeAllocation>> getStudentsWithPendingFees() {
        return ResponseEntity.ok(feeManagementService.getStudentsWithPendingFees());
    }
    
    @GetMapping("/reports/students-overdue")
    public ResponseEntity<List<StudentFeeAllocation>> getStudentsWithOverdueFees() {
        return ResponseEntity.ok(feeManagementService.getStudentsWithOverdueFees());
    }
    

    
}