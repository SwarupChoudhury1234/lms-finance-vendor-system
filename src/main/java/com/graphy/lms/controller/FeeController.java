package com.graphy.lms.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.graphy.lms.entity.AttendancePenalty;
import com.graphy.lms.entity.AuditLog;
import com.graphy.lms.entity.AutoDebitConfig;
import com.graphy.lms.entity.CertificateBlockList;
import com.graphy.lms.entity.CurrencyRate;
import com.graphy.lms.entity.ExamFeeLinkage;
import com.graphy.lms.entity.FeeDiscount;
import com.graphy.lms.entity.FeeReceipt;
import com.graphy.lms.entity.FeeRefund;
import com.graphy.lms.entity.FeeStructure;
import com.graphy.lms.entity.FeeType;
import com.graphy.lms.entity.LateFeeConfig;
import com.graphy.lms.entity.LateFeePenalty;
import com.graphy.lms.entity.PaymentAlternative;
import com.graphy.lms.entity.PaymentNotification;
import com.graphy.lms.entity.StudentFeeAllocation;
import com.graphy.lms.entity.StudentFeePayment;
import com.graphy.lms.entity.StudentInstallmentPlan;
import com.graphy.lms.security.AccessControlService;
import com.graphy.lms.security.UserContext;
import com.graphy.lms.service.FeeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/fee-management")
public class FeeController {

    @Autowired
    private FeeService feeManagementService;

    @Autowired
    private UserContext userContext;

    @Autowired
    private AccessControlService accessControlService;

    // ============================================
    // 1. FEE TYPES ENDPOINTS
    // ============================================
    
    @PostMapping("/fee-types")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FeeType> createFeeType(@Valid @RequestBody FeeType feeType) {
        FeeType created = feeManagementService.createFeeType(feeType);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @GetMapping("/fee-types/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<FeeType> getFeeTypeById(@PathVariable Long id) {
        FeeType feeType = feeManagementService.getFeeTypeById(id);
        return ResponseEntity.ok(feeType);
    }
    
    @GetMapping("/fee-types")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<List<FeeType>> getAllFeeTypes() {
        List<FeeType> feeTypes = feeManagementService.getAllFeeTypes();
        return ResponseEntity.ok(feeTypes);
    }
    
    @GetMapping("/fee-types/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'STUDENT', 'PARENT')")
    public ResponseEntity<List<FeeType>> getActiveFeeTypes() {
        List<FeeType> feeTypes = feeManagementService.getActiveFeeTypes();
        return ResponseEntity.ok(feeTypes);
    }
    
    @PutMapping("/fee-types/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FeeType> updateFeeType(@PathVariable Long id, @RequestBody FeeType feeType) {
        FeeType updated = feeManagementService.updateFeeType(id, feeType);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/fee-types/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteFeeType(@PathVariable Long id) {
        feeManagementService.deleteFeeType(id);
        return ResponseEntity.ok("FeeType deleted successfully");
    }

    // ============================================
    // 2. FEE STRUCTURES ENDPOINTS
    // ============================================
    
    @PostMapping("/fee-structures")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FeeStructure> createFeeStructure(@RequestBody FeeStructure feeStructure) {
        FeeStructure created = feeManagementService.createFeeStructure(feeStructure);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @GetMapping("/fee-structures/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<FeeStructure> getFeeStructureById(@PathVariable Long id) {
        FeeStructure feeStructure = feeManagementService.getFeeStructureById(id);
        return ResponseEntity.ok(feeStructure);
    }
    
    @GetMapping("/fee-structures")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FeeStructure>> getAllFeeStructures() {
        List<FeeStructure> feeStructures = feeManagementService.getAllFeeStructures();
        return ResponseEntity.ok(feeStructures);
    }
    
    @GetMapping("/fee-structures/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<List<FeeStructure>> getFeeStructuresByCourse(@PathVariable Long courseId) {
        List<FeeStructure> feeStructures = feeManagementService.getFeeStructuresByCourse(courseId);
        return ResponseEntity.ok(feeStructures);
    }
    
    @GetMapping("/fee-structures/batch/{batchId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<List<FeeStructure>> getFeeStructuresByBatch(@PathVariable Long batchId) {
        List<FeeStructure> feeStructures = feeManagementService.getFeeStructuresByBatch(batchId);
        return ResponseEntity.ok(feeStructures);
    }
    
    @GetMapping("/fee-structures/academic-year/{academicYear}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<List<FeeStructure>> getFeeStructuresByAcademicYear(@PathVariable String academicYear) {
        List<FeeStructure> feeStructures = feeManagementService.getFeeStructuresByAcademicYear(academicYear);
        return ResponseEntity.ok(feeStructures);
    }
    
    @PutMapping("/fee-structures/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FeeStructure> updateFeeStructure(@PathVariable Long id, @RequestBody FeeStructure feeStructure) {
        FeeStructure updated = feeManagementService.updateFeeStructure(id, feeStructure);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/fee-structures/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteFeeStructure(@PathVariable Long id) {
        feeManagementService.deleteFeeStructure(id);
        return ResponseEntity.ok("FeeStructure deleted successfully");
    }

    // ============================================
    // 3. FEE DISCOUNTS ENDPOINTS
    // ============================================
    
    @PostMapping("/fee-discounts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FeeDiscount> createFeeDiscount(@RequestBody FeeDiscount feeDiscount) {
        FeeDiscount created = feeManagementService.createFeeDiscount(feeDiscount);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @GetMapping("/fee-discounts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FeeDiscount> getFeeDiscountById(@PathVariable Long id) {
        FeeDiscount feeDiscount = feeManagementService.getFeeDiscountById(id);
        return ResponseEntity.ok(feeDiscount);
    }
    
    @GetMapping("/fee-discounts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FeeDiscount>> getAllFeeDiscounts() {
        List<FeeDiscount> feeDiscounts = feeManagementService.getAllFeeDiscounts();
        return ResponseEntity.ok(feeDiscounts);
    }
    
    @GetMapping("/fee-discounts/user/{userId}")
    public ResponseEntity<List<FeeDiscount>> getFeeDiscountsByUserId(@PathVariable Long userId) {
        // Students and Parents can only see their own discounts
        if (userContext.isStudent() || userContext.isParent()) {
            accessControlService.validateAccess(userId, "view discounts");
        }
        
        List<FeeDiscount> feeDiscounts = feeManagementService.getFeeDiscountsByUserId(userId);
        return ResponseEntity.ok(feeDiscounts);
    }
    
    @GetMapping("/fee-discounts/my")
    @PreAuthorize("hasAnyRole('STUDENT', 'PARENT')")
    public ResponseEntity<List<FeeDiscount>> getMyFeeDiscounts() {
        Long userId = userContext.getCurrentUserId();
        List<FeeDiscount> feeDiscounts = feeManagementService.getFeeDiscountsByUserId(userId);
        return ResponseEntity.ok(feeDiscounts);
    }
    
    @PutMapping("/fee-discounts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FeeDiscount> updateFeeDiscount(@PathVariable Long id, @RequestBody FeeDiscount feeDiscount) {
        FeeDiscount updated = feeManagementService.updateFeeDiscount(id, feeDiscount);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/fee-discounts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteFeeDiscount(@PathVariable Long id) {
        feeManagementService.deleteFeeDiscount(id);
        return ResponseEntity.ok("FeeDiscount deleted successfully");
    }

    // ============================================
    // 4. STUDENT FEE ALLOCATIONS ENDPOINTS
    // ============================================
    
    @PostMapping("/fee-allocations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentFeeAllocation> createFeeAllocation(@RequestBody StudentFeeAllocation allocation) {
        StudentFeeAllocation created = feeManagementService.createStudentFeeAllocation(allocation);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @GetMapping("/fee-allocations/{id}")
    public ResponseEntity<StudentFeeAllocation> getFeeAllocationById(@PathVariable Long id) {
        StudentFeeAllocation allocation = feeManagementService.getFeeAllocationById(id);
        
        // Access control: Students/Parents can only see their own
        if (userContext.isStudent() || userContext.isParent()) {
            accessControlService.validateAccess(allocation.getUserId(), "view fee allocation");
        }
        
        return ResponseEntity.ok(allocation);
    }
    
    @GetMapping("/fee-allocations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<StudentFeeAllocation>> getAllFeeAllocations() {
        List<StudentFeeAllocation> allocations = feeManagementService.getAllFeeAllocations();
        return ResponseEntity.ok(allocations);
    }
    
    @GetMapping("/fee-allocations/user/{userId}")
    public ResponseEntity<List<StudentFeeAllocation>> getFeeAllocationsByUserId(@PathVariable Long userId) {
        // Students and Parents can only see their own data
        if (userContext.isStudent() || userContext.isParent()) {
            accessControlService.validateAccess(userId, "view fee allocations");
        }
        
        List<StudentFeeAllocation> allocations = feeManagementService.getFeeAllocationsByUserId(userId);
        return ResponseEntity.ok(allocations);
    }
    
    @GetMapping("/fee-allocations/my")
    @PreAuthorize("hasAnyRole('STUDENT', 'PARENT')")
    public ResponseEntity<List<StudentFeeAllocation>> getMyFeeAllocations() {
        Long userId = userContext.getCurrentUserId();
        List<StudentFeeAllocation> allocations = feeManagementService.getFeeAllocationsByUserId(userId);
        return ResponseEntity.ok(allocations);
    }
    
    @PutMapping("/fee-allocations/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentFeeAllocation> updateFeeAllocation(@PathVariable Long id, @RequestBody StudentFeeAllocation allocation) {
        StudentFeeAllocation updated = feeManagementService.updateFeeAllocation(id, allocation);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/fee-allocations/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteFeeAllocation(@PathVariable Long id) {
        feeManagementService.deleteFeeAllocation(id);
        return ResponseEntity.ok("StudentFeeAllocation deleted successfully");
    }
    
    @GetMapping("/fee-allocations/calculate-payable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BigDecimal> calculatePayableAmount(@RequestParam Long userId, @RequestParam Long feeStructureId) {
        BigDecimal payable = feeManagementService.calculatePayableAmount(userId, feeStructureId);
        return ResponseEntity.ok(payable);
    }

    // ============================================
    // 5. PAYMENT ALTERNATIVES ENDPOINTS
    // ============================================
    
    @PostMapping("/payment-alternatives")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentAlternative> createPaymentAlternative(@RequestBody PaymentAlternative alternative) {
        PaymentAlternative created = feeManagementService.createPaymentAlternative(alternative);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @GetMapping("/payment-alternatives/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'STUDENT', 'PARENT')")
    public ResponseEntity<PaymentAlternative> getPaymentAlternativeById(@PathVariable Long id) {
        PaymentAlternative alternative = feeManagementService.getPaymentAlternativeById(id);
        return ResponseEntity.ok(alternative);
    }
    
    @GetMapping("/payment-alternatives")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'STUDENT', 'PARENT')")
    public ResponseEntity<List<PaymentAlternative>> getAllPaymentAlternatives() {
        List<PaymentAlternative> alternatives = feeManagementService.getAllPaymentAlternatives();
        return ResponseEntity.ok(alternatives);
    }
    
    @GetMapping("/payment-alternatives/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'STUDENT', 'PARENT')")
    public ResponseEntity<List<PaymentAlternative>> getActivePaymentAlternatives() {
        List<PaymentAlternative> alternatives = feeManagementService.getActivePaymentAlternatives();
        return ResponseEntity.ok(alternatives);
    }
    
    @PutMapping("/payment-alternatives/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentAlternative> updatePaymentAlternative(@PathVariable Long id, @RequestBody PaymentAlternative alternative) {
        PaymentAlternative updated = feeManagementService.updatePaymentAlternative(id, alternative);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/payment-alternatives/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deletePaymentAlternative(@PathVariable Long id) {
        feeManagementService.deletePaymentAlternative(id);
        return ResponseEntity.ok("PaymentAlternative deleted successfully");
    }

    // ============================================
    // 6. STUDENT INSTALLMENT PLANS ENDPOINTS
    // ============================================
    
    @PostMapping("/installment-plans")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentInstallmentPlan> createInstallmentPlan(@RequestBody StudentInstallmentPlan plan) {
        StudentInstallmentPlan created = feeManagementService.createInstallmentPlan(plan);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @GetMapping("/installment-plans/{id}")
    public ResponseEntity<StudentInstallmentPlan> getInstallmentPlanById(@PathVariable Long id) {
        StudentInstallmentPlan plan = feeManagementService.getInstallmentPlanById(id);
        
        // Access control for students/parents
        if (userContext.isStudent() || userContext.isParent()) {
            StudentFeeAllocation allocation = feeManagementService.getFeeAllocationById(plan.getStudentFeeAllocationId());
            accessControlService.validateAccess(allocation.getUserId(), "view installment plan");
        }
        
        return ResponseEntity.ok(plan);
    }
    
    @GetMapping("/installment-plans")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<StudentInstallmentPlan>> getAllInstallmentPlans() {
        List<StudentInstallmentPlan> plans = feeManagementService.getAllInstallmentPlans();
        return ResponseEntity.ok(plans);
    }
    
    @GetMapping("/installment-plans/allocation/{allocationId}")
    public ResponseEntity<List<StudentInstallmentPlan>> getInstallmentPlansByAllocationId(@PathVariable Long allocationId) {
        // Access control for students/parents
        if (userContext.isStudent() || userContext.isParent()) {
            StudentFeeAllocation allocation = feeManagementService.getFeeAllocationById(allocationId);
            accessControlService.validateAccess(allocation.getUserId(), "view installment plans");
        }
        
        List<StudentInstallmentPlan> plans = feeManagementService.getInstallmentPlansByAllocationId(allocationId);
        return ResponseEntity.ok(plans);
    }
    
    @GetMapping("/installment-plans/my")
    @PreAuthorize("hasAnyRole('STUDENT', 'PARENT')")
    public ResponseEntity<List<StudentInstallmentPlan>> getMyInstallmentPlans() {
        Long userId = userContext.getCurrentUserId();
        List<StudentFeeAllocation> allocations = feeManagementService.getFeeAllocationsByUserId(userId);
        
        List<StudentInstallmentPlan> allPlans = allocations.stream()
                .flatMap(allocation -> feeManagementService.getInstallmentPlansByAllocationId(allocation.getId()).stream())
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(allPlans);
    }
    
    @GetMapping("/installment-plans/overdue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<StudentInstallmentPlan>> getOverdueInstallments() {
        List<StudentInstallmentPlan> plans = feeManagementService.getOverdueInstallments();
        return ResponseEntity.ok(plans);
    }
    
    @PutMapping("/installment-plans/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentInstallmentPlan> updateInstallmentPlan(@PathVariable Long id, @RequestBody StudentInstallmentPlan plan) {
        StudentInstallmentPlan updated = feeManagementService.updateInstallmentPlan(id, plan);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/installment-plans/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteInstallmentPlan(@PathVariable Long id) {
        feeManagementService.deleteInstallmentPlan(id);
        return ResponseEntity.ok("StudentInstallmentPlan deleted successfully");
    }
    
    @PostMapping("/installment-plans/create-multiple")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<StudentInstallmentPlan>> createInstallmentsForStudent(
            @RequestParam Long allocationId,
            @RequestParam Long alternativeId,
            @RequestBody List<Map<String, Object>> installmentDetails) {
        List<StudentInstallmentPlan> plans = feeManagementService.createInstallmentsForStudent(allocationId, alternativeId, installmentDetails);
        return new ResponseEntity<>(plans, HttpStatus.CREATED);
    }
    
    @PostMapping("/installment-plans/reset")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<StudentInstallmentPlan>> resetInstallments(
            @RequestParam Long allocationId,
            @RequestParam Long alternativeId,
            @RequestBody List<Map<String, Object>> newInstallmentDetails) {
        List<StudentInstallmentPlan> plans = feeManagementService.resetInstallments(allocationId, alternativeId, newInstallmentDetails);
        return ResponseEntity.ok(plans);
    }

    // ============================================
    // 7. STUDENT FEE PAYMENTS ENDPOINTS
    // ============================================
    
    @PostMapping("/payments")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<StudentFeePayment> createPayment(@RequestBody StudentFeePayment payment) {
        // Students can only create payments for themselves
        if (userContext.isStudent()) {
            StudentFeeAllocation allocation = feeManagementService.getFeeAllocationById(payment.getStudentFeeAllocationId());
            accessControlService.validateAccess(allocation.getUserId(), "create payment");
        }
        
        StudentFeePayment created = feeManagementService.createPayment(payment);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @GetMapping("/payments/{id}")
    public ResponseEntity<StudentFeePayment> getPaymentById(@PathVariable Long id) {
        StudentFeePayment payment = feeManagementService.getPaymentById(id);
        
        // Access control for students/parents
        if (userContext.isStudent() || userContext.isParent()) {
            StudentFeeAllocation allocation = feeManagementService.getFeeAllocationById(payment.getStudentFeeAllocationId());
            accessControlService.validateAccess(allocation.getUserId(), "view payment");
        }
        
        return ResponseEntity.ok(payment);
    }
    
    @GetMapping("/payments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<StudentFeePayment>> getAllPayments() {
        List<StudentFeePayment> payments = feeManagementService.getAllPayments();
        return ResponseEntity.ok(payments);
    }
    
    @GetMapping("/payments/allocation/{allocationId}")
    public ResponseEntity<List<StudentFeePayment>> getPaymentsByAllocationId(@PathVariable Long allocationId) {
        // Access control for students/parents
        if (userContext.isStudent() || userContext.isParent()) {
            StudentFeeAllocation allocation = feeManagementService.getFeeAllocationById(allocationId);
            accessControlService.validateAccess(allocation.getUserId(), "view payments");
        }
        
        List<StudentFeePayment> payments = feeManagementService.getPaymentsByAllocationId(allocationId);
        return ResponseEntity.ok(payments);
    }
    
    @GetMapping("/payments/history/{userId}")
    public ResponseEntity<List<StudentFeePayment>> getPaymentHistory(@PathVariable Long userId) {
        // Students and Parents can only see their own history
        if (userContext.isStudent() || userContext.isParent()) {
            accessControlService.validateAccess(userId, "view payment history");
        }
        
        List<StudentFeePayment> payments = feeManagementService.getPaymentHistory(userId);
        List<Map<String, Object>> history = feeManagementService.getStudentTransactionHistory(userId);
        return ResponseEntity.ok(payments);
    }
    
    @GetMapping("/payments/my-history")
    @PreAuthorize("hasAnyRole('STUDENT', 'PARENT')")
    public ResponseEntity<List<StudentFeePayment>> getMyPaymentHistory() {
        Long userId = userContext.getCurrentUserId();
        List<StudentFeePayment> payments = feeManagementService.getPaymentHistory(userId);
        List<Map<String, Object>> history = feeManagementService.getStudentTransactionHistory(userId);
        return ResponseEntity.ok(payments);
    }
    
    @PutMapping("/payments/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentFeePayment> updatePayment(@PathVariable Long id, @RequestBody StudentFeePayment payment) {
        StudentFeePayment updated = feeManagementService.updatePayment(id, payment);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/payments/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deletePayment(@PathVariable Long id) {
        feeManagementService.deletePayment(id);
        return ResponseEntity.ok("StudentFeePayment deleted successfully");
    }
    
    @PostMapping("/payments/process-online")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<StudentFeePayment> processOnlinePayment(
            @RequestParam Long allocationId,
            @RequestParam(required = false) Long installmentPlanId,
            @RequestParam BigDecimal amount,
            @RequestParam String paymentMode,
            @RequestParam String transactionRef,
            @RequestParam(required = false) String gatewayResponse) {
        
        if (userContext.isStudent()) {
            StudentFeeAllocation allocation = feeManagementService.getFeeAllocationById(allocationId);
            accessControlService.validateAccess(allocation.getUserId(), "process online payment");
        }
        
        // Passing 'null' for Screenshot, Name, and Email to match old behavior
        StudentFeePayment payment = feeManagementService.processOnlinePayment(
                allocationId, installmentPlanId, amount, paymentMode, transactionRef, gatewayResponse,
                null, null, null); // <--- Passing Nulls
        
        return new ResponseEntity<>(payment, HttpStatus.CREATED);
    }
    
    @PostMapping("/payments/record-manual")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentFeePayment> recordManualPayment(
            @RequestParam Long allocationId,
            @RequestParam(required = false) Long installmentPlanId,
            @RequestParam BigDecimal amount,
            @RequestParam String paymentMode,
            @RequestParam String transactionRef,
            @RequestParam Long recordedBy) {
        StudentFeePayment payment = feeManagementService.recordManualPayment(
                allocationId, installmentPlanId, amount, paymentMode, transactionRef, recordedBy);
        return new ResponseEntity<>(payment, HttpStatus.CREATED);
    }

    // ============================================
    // 8. LATE FEE CONFIG ENDPOINTS
    // ============================================
    
    @PostMapping("/late-fee-configs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LateFeeConfig> createLateFeeConfig(@RequestBody LateFeeConfig config) {
        LateFeeConfig created = feeManagementService.createLateFeeConfig(config);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @GetMapping("/late-fee-configs/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LateFeeConfig> getLateFeeConfigById(@PathVariable Long id) {
        LateFeeConfig config = feeManagementService.getLateFeeConfigById(id);
        return ResponseEntity.ok(config);
    }
    
    @GetMapping("/late-fee-configs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LateFeeConfig>> getAllLateFeeConfigs() {
        List<LateFeeConfig> configs = feeManagementService.getAllLateFeeConfigs();
        return ResponseEntity.ok(configs);
    }
    
    @GetMapping("/late-fee-configs/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LateFeeConfig>> getActiveLateFeeConfigs() {
        List<LateFeeConfig> configs = feeManagementService.getActiveLateFeeConfigs();
        return ResponseEntity.ok(configs);
    }
    
    @PutMapping("/late-fee-configs/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LateFeeConfig> updateLateFeeConfig(@PathVariable Long id, @RequestBody LateFeeConfig config) {
        LateFeeConfig updated = feeManagementService.updateLateFeeConfig(id, config);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/late-fee-configs/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteLateFeeConfig(@PathVariable Long id) {
        feeManagementService.deleteLateFeeConfig(id);
        return ResponseEntity.ok("LateFeeConfig deleted successfully");
    }

    // ============================================
    // 9. LATE FEE PENALTIES ENDPOINTS
    // ============================================
    
    @PostMapping("/late-fee-penalties")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LateFeePenalty> createLateFeePenalty(@RequestBody LateFeePenalty penalty) {
        LateFeePenalty created = feeManagementService.createLateFeePenalty(penalty);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @GetMapping("/late-fee-penalties/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LateFeePenalty> getLateFeePenaltyById(@PathVariable Long id) {
        LateFeePenalty penalty = feeManagementService.getLateFeePenaltyById(id);
        return ResponseEntity.ok(penalty);
    }
    
    @GetMapping("/late-fee-penalties")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LateFeePenalty>> getAllLateFeePenalties() {
        List<LateFeePenalty> penalties = feeManagementService.getAllLateFeePenalties();
        return ResponseEntity.ok(penalties);
    }
    
    @GetMapping("/late-fee-penalties/installment/{installmentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LateFeePenalty>> getLateFeePenaltiesByInstallmentId(@PathVariable Long installmentId) {
        List<LateFeePenalty> penalties = feeManagementService.getLateFeePenaltiesByInstallmentId(installmentId);
        return ResponseEntity.ok(penalties);
    }
    
    @PutMapping("/late-fee-penalties/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LateFeePenalty> updateLateFeePenalty(@PathVariable Long id, @RequestBody LateFeePenalty penalty) {
        LateFeePenalty updated = feeManagementService.updateLateFeePenalty(id, penalty);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/late-fee-penalties/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteLateFeePenalty(@PathVariable Long id) {
        feeManagementService.deleteLateFeePenalty(id);
        return ResponseEntity.ok("LateFeePenalty deleted successfully");
    }
    @PostMapping("/late-fee-penalties/apply-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> applyLateFees() {
        feeManagementService.applyLateFees();
        return ResponseEntity.ok("Late fees applied successfully");
    }

    @PostMapping("/late-fee-penalties/{penaltyId}/waive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LateFeePenalty> waiveLateFee(@PathVariable Long penaltyId, @RequestParam Long waivedBy) {
        LateFeePenalty waived = feeManagementService.waiveLateFee(penaltyId, waivedBy);
        return ResponseEntity.ok(waived);
    }

    // ============================================
    // 10. ATTENDANCE PENALTIES ENDPOINTS
    // ============================================

    @PostMapping("/attendance-penalties")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AttendancePenalty> createAttendancePenalty(@RequestBody AttendancePenalty penalty) {
        AttendancePenalty created = feeManagementService.createAttendancePenalty(penalty);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/attendance-penalties/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AttendancePenalty> getAttendancePenaltyById(@PathVariable Long id) {
        AttendancePenalty penalty = feeManagementService.getAttendancePenaltyById(id);
        return ResponseEntity.ok(penalty);
    }

    @GetMapping("/attendance-penalties")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AttendancePenalty>> getAllAttendancePenalties() {
        List<AttendancePenalty> penalties = feeManagementService.getAllAttendancePenalties();
        return ResponseEntity.ok(penalties);
    }

    @GetMapping("/attendance-penalties/user/{userId}")
    public ResponseEntity<List<AttendancePenalty>> getAttendancePenaltiesByUserId(@PathVariable Long userId) {
        // Students and Parents can only see their own penalties
        if (userContext.isStudent() || userContext.isParent()) {
            accessControlService.validateAccess(userId, "view attendance penalties");
        }
        
        List<AttendancePenalty> penalties = feeManagementService.getAttendancePenaltiesByUserId(userId);
        return ResponseEntity.ok(penalties);
    }

    @GetMapping("/attendance-penalties/my")
    @PreAuthorize("hasAnyRole('STUDENT', 'PARENT')")
    public ResponseEntity<List<AttendancePenalty>> getMyAttendancePenalties() {
        Long userId = userContext.getCurrentUserId();
        List<AttendancePenalty> penalties = feeManagementService.getAttendancePenaltiesByUserId(userId);
        return ResponseEntity.ok(penalties);
    }

    @PutMapping("/attendance-penalties/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AttendancePenalty> updateAttendancePenalty(@PathVariable Long id, @RequestBody AttendancePenalty penalty) {
        AttendancePenalty updated = feeManagementService.updateAttendancePenalty(id, penalty);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/attendance-penalties/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteAttendancePenalty(@PathVariable Long id) {
        feeManagementService.deleteAttendancePenalty(id);
        return ResponseEntity.ok("AttendancePenalty deleted successfully");
    }

    @PostMapping("/attendance-penalties/apply")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AttendancePenalty> applyAttendancePenalty(
            @RequestParam Long userId,
            @RequestParam Long allocationId,
            @RequestParam String absenceDate,
            @RequestParam BigDecimal penaltyAmount,
            @RequestParam String reason,
            @RequestParam Long appliedBy) {
        AttendancePenalty penalty = feeManagementService.applyAttendancePenalty(
                userId, allocationId, LocalDate.parse(absenceDate), penaltyAmount, reason, appliedBy);
        return new ResponseEntity<>(penalty, HttpStatus.CREATED);
    }
 // ============================================
    // 11. EXAM FEE LINKAGE ENDPOINTS
    // ============================================
    
    @PostMapping("/exam-fee-linkages")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExamFeeLinkage> createExamFeeLinkage(@RequestBody ExamFeeLinkage linkage) {
        ExamFeeLinkage created = feeManagementService.createExamFeeLinkage(linkage);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @GetMapping("/exam-fee-linkages/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExamFeeLinkage> getExamFeeLinkageById(@PathVariable Long id) {
        ExamFeeLinkage linkage = feeManagementService.getExamFeeLinkageById(id);
        return ResponseEntity.ok(linkage);
    }
    
    @GetMapping("/exam-fee-linkages")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ExamFeeLinkage>> getAllExamFeeLinkages() {
        List<ExamFeeLinkage> linkages = feeManagementService.getAllExamFeeLinkages();
        return ResponseEntity.ok(linkages);
    }
    
    @GetMapping("/exam-fee-linkages/user/{userId}")
    public ResponseEntity<List<ExamFeeLinkage>> getExamFeeLinkagesByUserId(@PathVariable Long userId) {
        // Students and Parents can only see their own exam fees
        if (userContext.isStudent() || userContext.isParent()) {
            accessControlService.validateAccess(userId, "view exam fee linkages");
        }
        
        List<ExamFeeLinkage> linkages = feeManagementService.getExamFeeLinkagesByUserId(userId);
        return ResponseEntity.ok(linkages);
    }
    
    @GetMapping("/exam-fee-linkages/my")
    @PreAuthorize("hasAnyRole('STUDENT', 'PARENT')")
    public ResponseEntity<List<ExamFeeLinkage>> getMyExamFeeLinkages() {
        Long userId = userContext.getCurrentUserId();
        List<ExamFeeLinkage> linkages = feeManagementService.getExamFeeLinkagesByUserId(userId);
        return ResponseEntity.ok(linkages);
    }
    
    @PutMapping("/exam-fee-linkages/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExamFeeLinkage> updateExamFeeLinkage(@PathVariable Long id, @RequestBody ExamFeeLinkage linkage) {
        ExamFeeLinkage updated = feeManagementService.updateExamFeeLinkage(id, linkage);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/exam-fee-linkages/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteExamFeeLinkage(@PathVariable Long id) {
        feeManagementService.deleteExamFeeLinkage(id);
        return ResponseEntity.ok("ExamFeeLinkage deleted successfully");
    }
    
    @PostMapping("/exam-fee-linkages/link")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExamFeeLinkage> linkExamFeeToStudent(
            @RequestParam Long examId,
            @RequestParam Long userId,
            @RequestParam Long allocationId,
            @RequestParam BigDecimal examFeeAmount) {
        ExamFeeLinkage linkage = feeManagementService.linkExamFeeToStudent(examId, userId, allocationId, examFeeAmount);
        return new ResponseEntity<>(linkage, HttpStatus.CREATED);
    }

    // ============================================
    // 12. FEE REFUNDS ENDPOINTS
    // ============================================
    
    @PostMapping("/refunds")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<FeeRefund> createRefundRequest(@RequestBody FeeRefund refund) {
        FeeRefund created = feeManagementService.createRefundRequest(refund);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @GetMapping("/refunds/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FeeRefund> getRefundById(@PathVariable Long id) {
        FeeRefund refund = feeManagementService.getRefundById(id);
        return ResponseEntity.ok(refund);
    }
    
    @GetMapping("/refunds")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FeeRefund>> getAllRefunds() {
        List<FeeRefund> refunds = feeManagementService.getAllRefunds();
        return ResponseEntity.ok(refunds);
    }
    
    @GetMapping("/refunds/user/{userId}")
    public ResponseEntity<List<FeeRefund>> getRefundsByUserId(@PathVariable Long userId) {
        // Students can only see their own refunds
        if (userContext.isStudent()) {
            accessControlService.validateAccess(userId, "view refunds");
        }
        
        List<FeeRefund> refunds = feeManagementService.getRefundsByUserId(userId);
        return ResponseEntity.ok(refunds);
    }
    
    @GetMapping("/refunds/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<FeeRefund>> getMyRefunds() {
        Long userId = userContext.getCurrentUserId();
        List<FeeRefund> refunds = feeManagementService.getRefundsByUserId(userId);
        return ResponseEntity.ok(refunds);
    }
    
    @GetMapping("/refunds/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FeeRefund>> getRefundsByStatus(@PathVariable String status) {
        List<FeeRefund> refunds = feeManagementService.getRefundsByStatus(FeeRefund.RefundStatus.valueOf(status));
        return ResponseEntity.ok(refunds);
    }
    
    @PutMapping("/refunds/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FeeRefund> updateRefund(@PathVariable Long id, @RequestBody FeeRefund refund) {
        FeeRefund updated = feeManagementService.updateRefund(id, refund);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/refunds/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteRefund(@PathVariable Long id) {
        feeManagementService.deleteRefund(id);
        return ResponseEntity.ok("FeeRefund deleted successfully");
    }
    
    @PostMapping("/refunds/{refundId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FeeRefund> approveRefund(@PathVariable Long refundId, @RequestParam Long approvedBy) {
        FeeRefund approved = feeManagementService.approveRefund(refundId, approvedBy);
        return ResponseEntity.ok(approved);
    }
    
    @PostMapping("/refunds/{refundId}/process")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FeeRefund> processRefund(
            @RequestParam Long refundId,
            @RequestParam String refundMode,
            @RequestParam String transactionRef) {
        FeeRefund processed = feeManagementService.processRefund(refundId, refundMode, transactionRef);
        return ResponseEntity.ok(processed);
    }
    
    @PostMapping("/refunds/{refundId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FeeRefund> rejectRefund(
            @PathVariable Long refundId,
            @RequestParam Long rejectedBy,
            @RequestParam String reason) {
        FeeRefund rejected = feeManagementService.rejectRefund(refundId, rejectedBy, reason);
        return ResponseEntity.ok(rejected);
    }

    // ============================================
    // 13. FEE RECEIPTS ENDPOINTS (READ ONLY)
    // ============================================
    
    @GetMapping("/receipts/{id}")
    public ResponseEntity<FeeReceipt> getReceiptById(@PathVariable Long id) {
        FeeReceipt receipt = feeManagementService.getReceiptById(id);
        
        // Access control for students/parents
        if (userContext.isStudent() || userContext.isParent()) {
            accessControlService.validateAccess(receipt.getUserId(), "view receipt");
        }
        
        return ResponseEntity.ok(receipt);
    }
 // ============================================
    // FIX: SERVE RECEIPT FILE (Download Endpoint)
    // ============================================
    @GetMapping("/receipts/download/{fileName}")
    public ResponseEntity<Resource> downloadReceipt(@PathVariable String fileName) {
        // Since we don't have a real PDF generator yet, we create a text file on the fly.
        // This tricks the browser into thinking it downloaded a real receipt.
        
        String dummyContent = "OFFICIAL RECEIPT\n" +
                              "----------------\n" +
                              "Receipt Number: " + fileName + "\n" +
                              "Status: PAID\n" +
                              "Date: " + LocalDate.now() + "\n" +
                              "\n" +
                              "Thank you for your payment!";
        
        ByteArrayResource resource = new ByteArrayResource(dummyContent.getBytes());

        return ResponseEntity.ok()
                // This header forces the browser/Postman to pop up the "Save As" dialog
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentLength(resource.contentLength())
                .contentType(MediaType.TEXT_PLAIN) 
                .body(resource);
    }
    
    @GetMapping("/receipts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FeeReceipt>> getAllReceipts() {
        List<FeeReceipt> receipts = feeManagementService.getAllReceipts();
        return ResponseEntity.ok(receipts);
    }
    
    @GetMapping("/receipts/user/{userId}")
    public ResponseEntity<List<FeeReceipt>> getReceiptsByUserId(@PathVariable Long userId) {
        // Students and Parents can only see their own receipts
        if (userContext.isStudent() || userContext.isParent()) {
            accessControlService.validateAccess(userId, "view receipts");
        }
        
        List<FeeReceipt> receipts = feeManagementService.getReceiptsByUserId(userId);
        return ResponseEntity.ok(receipts);
    }
    
    @GetMapping("/receipts/my")
    @PreAuthorize("hasAnyRole('STUDENT', 'PARENT')")
    public ResponseEntity<List<FeeReceipt>> getMyReceipts() {
        Long userId = userContext.getCurrentUserId();
        List<FeeReceipt> receipts = feeManagementService.getReceiptsByUserId(userId);
        return ResponseEntity.ok(receipts);
    }
    
    @GetMapping("/receipts/payment/{paymentId}")
    public ResponseEntity<FeeReceipt> getReceiptByPaymentId(@PathVariable Long paymentId) {
        FeeReceipt receipt = feeManagementService.getReceiptByPaymentId(paymentId);
        
        // Access control for students/parents
        if (receipt != null && (userContext.isStudent() || userContext.isParent())) {
            accessControlService.validateAccess(receipt.getUserId(), "view receipt");
        }
        
        return ResponseEntity.ok(receipt);
    }

    // ============================================
    // 14. PAYMENT NOTIFICATIONS ENDPOINTS
    // ============================================
    
    @PostMapping("/notifications")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentNotification> createNotification(@RequestBody PaymentNotification notification) {
        PaymentNotification created = feeManagementService.createNotification(notification);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @GetMapping("/notifications/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentNotification> getNotificationById(@PathVariable Long id) {
        PaymentNotification notification = feeManagementService.getNotificationById(id);
        return ResponseEntity.ok(notification);
    }
    
    @GetMapping("/notifications")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaymentNotification>> getAllNotifications() {
        List<PaymentNotification> notifications = feeManagementService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/notifications/user/{userId}")
    public ResponseEntity<List<PaymentNotification>> getNotificationsByUserId(@PathVariable Long userId) {
        // Students and Parents can only see their own notifications
        if (userContext.isStudent() || userContext.isParent()) {
            accessControlService.validateAccess(userId, "view notifications");
        }
        
        List<PaymentNotification> notifications = feeManagementService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/notifications/my")
    @PreAuthorize("hasAnyRole('STUDENT', 'PARENT')")
    public ResponseEntity<List<PaymentNotification>> getMyNotifications() {
        Long userId = userContext.getCurrentUserId();
        List<PaymentNotification> notifications = feeManagementService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }
    
    @PutMapping("/notifications/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentNotification> updateNotification(@PathVariable Long id, @RequestBody PaymentNotification notification) {
        PaymentNotification updated = feeManagementService.updateNotification(id, notification);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/notifications/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteNotification(@PathVariable Long id) {
        feeManagementService.deleteNotification(id);
        return ResponseEntity.ok("PaymentNotification deleted successfully");
    }

    // ============================================
    // 15. AUTO DEBIT CONFIG ENDPOINTS
    // ============================================
    
    @PostMapping("/auto-debit-configs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AutoDebitConfig> createAutoDebitConfig(@RequestBody AutoDebitConfig config) {
        AutoDebitConfig created = feeManagementService.createAutoDebitConfig(config);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @GetMapping("/auto-debit-configs/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AutoDebitConfig> getAutoDebitConfigById(@PathVariable Long id) {
        AutoDebitConfig config = feeManagementService.getAutoDebitConfigById(id);
        return ResponseEntity.ok(config);
    }
    
    @GetMapping("/auto-debit-configs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AutoDebitConfig>> getAllAutoDebitConfigs() {
        List<AutoDebitConfig> configs = feeManagementService.getAllAutoDebitConfigs();
        return ResponseEntity.ok(configs);
    }
    
    @GetMapping("/auto-debit-configs/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AutoDebitConfig>> getAutoDebitConfigsByUserId(@PathVariable Long userId) {
        List<AutoDebitConfig> configs = feeManagementService.getAutoDebitConfigsByUserId(userId);
        return ResponseEntity.ok(configs);
    }
    
    @PutMapping("/auto-debit-configs/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AutoDebitConfig> updateAutoDebitConfig(@PathVariable Long id, @RequestBody AutoDebitConfig config) {
        AutoDebitConfig updated = feeManagementService.updateAutoDebitConfig(id, config);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/auto-debit-configs/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteAutoDebitConfig(@PathVariable Long id) {
        feeManagementService.deleteAutoDebitConfig(id);
        return ResponseEntity.ok("AutoDebitConfig deleted successfully");
    }
    
    @PostMapping("/auto-debit-configs/process-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> processAutoDebit() {
        feeManagementService.processAutoDebit();
        return ResponseEntity.ok("Auto-debit processing completed");
    }

    // ============================================
    // 16. CURRENCY RATES ENDPOINTS
    // ============================================
    
    @PostMapping("/currency-rates")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CurrencyRate> createCurrencyRate(@RequestBody CurrencyRate rate) {
        CurrencyRate created = feeManagementService.createCurrencyRate(rate);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @GetMapping("/currency-rates/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CurrencyRate> getCurrencyRateById(@PathVariable Long id) {
        CurrencyRate rate = feeManagementService.getCurrencyRateById(id);
        return ResponseEntity.ok(rate);
    }
    
    @GetMapping("/currency-rates")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CurrencyRate>> getAllCurrencyRates() {
        List<CurrencyRate> rates = feeManagementService.getAllCurrencyRates();
        return ResponseEntity.ok(rates);
    }
    
    @PutMapping("/currency-rates/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CurrencyRate> updateCurrencyRate(@PathVariable Long id, @RequestBody CurrencyRate rate) {
        CurrencyRate updated = feeManagementService.updateCurrencyRate(id, rate);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/currency-rates/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteCurrencyRate(@PathVariable Long id) {
        feeManagementService.deleteCurrencyRate(id);
        return ResponseEntity.ok("CurrencyRate deleted successfully");
    }
    
    @GetMapping("/currency-rates/convert")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'STUDENT', 'PARENT')")
    public ResponseEntity<BigDecimal> convertCurrency(
            @RequestParam BigDecimal amount,
            @RequestParam String fromCurrency,
            @RequestParam String toCurrency,
            @RequestParam String date) {
        BigDecimal converted = feeManagementService.convertCurrency(amount, fromCurrency, toCurrency, LocalDate.parse(date));
        return ResponseEntity.ok(converted);
    }

    // ============================================
    // 17. AUDIT LOGS ENDPOINTS (READ ONLY)
    // ============================================
    
    @GetMapping("/audit-logs/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuditLog> getAuditLogById(@PathVariable Long id) {
        AuditLog log = feeManagementService.getAuditLogById(id);
        return ResponseEntity.ok(log);
    }
    
    @GetMapping("/audit-logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLog>> getAllAuditLogs() {
        List<AuditLog> logs = feeManagementService.getAllAuditLogs();
        return ResponseEntity.ok(logs);
    }
    
    @GetMapping("/audit-logs/module/{module}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLog>> getAuditLogsByModule(@PathVariable String module) {
        List<AuditLog> logs = feeManagementService.getAuditLogsByModule(module);
        return ResponseEntity.ok(logs);
    }
    
    @GetMapping("/audit-logs/entity")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLog>> getAuditLogsByEntity(
            @RequestParam String entityName,
            @RequestParam Long entityId) {
        List<AuditLog> logs = feeManagementService.getAuditLogsByEntity(entityName, entityId);
        return ResponseEntity.ok(logs);
    }

    // ============================================
    // 18. CERTIFICATE BLOCK LIST ENDPOINTS
    // ============================================
    
    @PostMapping("/certificate-blocks")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CertificateBlockList> createCertificateBlock(@RequestBody CertificateBlockList block) {
        CertificateBlockList created = feeManagementService.createCertificateBlock(block);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @GetMapping("/certificate-blocks/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CertificateBlockList> getCertificateBlockById(@PathVariable Long id) {
        CertificateBlockList block = feeManagementService.getCertificateBlockById(id);
        return ResponseEntity.ok(block);
    }
    
    @GetMapping("/certificate-blocks")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CertificateBlockList>> getAllCertificateBlocks() {
        List<CertificateBlockList> blocks = feeManagementService.getAllCertificateBlocks();
        return ResponseEntity.ok(blocks);
    }
    
    @PutMapping("/certificate-blocks/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CertificateBlockList> updateCertificateBlock(@PathVariable Long id, @RequestBody CertificateBlockList block) {
        CertificateBlockList updated = feeManagementService.updateCertificateBlock(id, block);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/certificate-blocks/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteCertificateBlock(@PathVariable Long id) {
        feeManagementService.deleteCertificateBlock(id);
        return ResponseEntity.ok("CertificateBlockList deleted successfully");
    }
    
    @GetMapping("/certificate-blocks/can-issue/{userId}")
    public ResponseEntity<Boolean> canIssueCertificate(@PathVariable Long userId) {
        // Students and Parents can check their own certificate eligibility
        if (userContext.isStudent() || userContext.isParent()) {
            accessControlService.validateAccess(userId, "check certificate eligibility");
        }
        
        boolean canIssue = feeManagementService.canIssueCertificate(userId);
        return ResponseEntity.ok(canIssue);
    }
    
    @GetMapping("/certificate-blocks/can-issue-my")
    @PreAuthorize("hasAnyRole('STUDENT', 'PARENT')")
    public ResponseEntity<Boolean> canIssueMyOwnCertificate() {
        Long userId = userContext.getCurrentUserId();
        boolean canIssue = feeManagementService.canIssueCertificate(userId);
        return ResponseEntity.ok(canIssue);
    }
    
    @PostMapping("/certificate-blocks/block")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CertificateBlockList> blockCertificate(
            @RequestParam Long userId,
            // @RequestParam BigDecimal pendingAmount,  <-- DELETE THIS LINE
            @RequestParam String reason,
            @RequestParam Long blockedBy) {
        
        // Call the new method signature
        CertificateBlockList block = feeManagementService.blockCertificate(userId, reason, blockedBy);
        
        return new ResponseEntity<>(block, HttpStatus.CREATED);
    }
    
    @PostMapping("/certificate-blocks/unblock/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> unblockCertificate(@PathVariable Long userId) {
        feeManagementService.unblockCertificate(userId);
        return ResponseEntity.ok("Certificate unblocked successfully");
    }

    // ============================================
    // 19. REPORTS & ANALYTICS ENDPOINTS
    // ============================================
    
    @GetMapping("/reports/student/{userId}")
    public ResponseEntity<Map<String, Object>> getStudentFeeReport(@PathVariable Long userId) {
        // Students and Parents can only see their own reports
        if (userContext.isStudent() || userContext.isParent()) {
            accessControlService.validateAccess(userId, "view fee report");
        }
        
        Map<String, Object> report = feeManagementService.getStudentFeeReport(userId);
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/reports/my-report")
    @PreAuthorize("hasAnyRole('STUDENT', 'PARENT')")
    public ResponseEntity<Map<String, Object>> getMyFeeReport() {
        Long userId = userContext.getCurrentUserId();
        Map<String, Object> report = feeManagementService.getStudentFeeReport(userId);
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/reports/batch/{batchId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getBatchFeeReport(@PathVariable Long batchId) {
        Map<String, Object> report = feeManagementService.getBatchFeeReport(batchId);
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/reports/course/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getCourseFeeReport(@PathVariable Long courseId) {
        Map<String, Object> report = feeManagementService.getCourseFeeReport(courseId);
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/reports/monthly")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getMonthlyRevenueReport(
            @RequestParam int year,
            @RequestParam int month) {
        Map<String, Object> report = feeManagementService.getMonthlyRevenueReport(year, month);
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/reports/quarterly")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getQuarterlyRevenueReport(
            @RequestParam int year,
            @RequestParam int quarter) {
        Map<String, Object> report = feeManagementService.getQuarterlyRevenueReport(year, quarter);
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/reports/yearly")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getYearlyRevenueReport(@RequestParam int year) {
        Map<String, Object> report = feeManagementService.getYearlyRevenueReport(year);
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/reports/overall")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getOverallFinancialSummary() {
        Map<String, Object> summary = feeManagementService.getOverallFinancialSummary();
        return ResponseEntity.ok(summary);
    }

    // 1. INITIATE PAYMENT (Get Order ID)
    @PostMapping("/payments/initiate")
    public ResponseEntity<Map<String, String>> initiatePayment(
            @RequestParam Long allocationId, 
            @RequestParam BigDecimal amount) {
        
        String orderId = feeManagementService.createRazorpayOrder(allocationId, amount);
        
        Map<String, String> response = new HashMap<>();
        response.put("orderId", orderId);
        return ResponseEntity.ok(response);
    }

    // 2. VERIFY PAYMENT (Complete the Transaction)
    // UPDATED: Added installmentPlanId as optional param to support Advance Payments (null ID)
 // 2. VERIFY PAYMENT (Complete the Transaction)
    @PostMapping("/verify-payment")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<Map<String, Object>> verifyPayment(
            @RequestBody Map<String, String> paymentData) {
        
        String paymentId = paymentData.get("razorpay_payment_id");
        Long allocationId = Long.parseLong(paymentData.get("allocationId"));
        
        Long installmentPlanId = paymentData.get("installmentPlanId") != null ? 
                               Long.parseLong(paymentData.get("installmentPlanId")) : null;

        // FIX: Still need to extract amount to call the service, or pass BigDecimal.ZERO if strictly old code
        BigDecimal amount = paymentData.containsKey("amount") ? 
                           new BigDecimal(paymentData.get("amount")) : BigDecimal.ZERO;

        // Passing 'null' for new fields
        StudentFeePayment payment = feeManagementService.processOnlinePayment(
                allocationId,
                installmentPlanId,
                amount,
                "UPI",
                paymentId,
                "Razorpay Payment Successful",
                null, null, null); // <--- Passing Nulls

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Payment verified and recorded successfully");
        response.put("paymentId", payment.getId());
        
        return ResponseEntity.ok(response);
    }
}