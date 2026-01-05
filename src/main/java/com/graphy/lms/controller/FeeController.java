package com.graphy.lms.controller;

import com.graphy.lms.entity.*;
import com.graphy.lms.service.FeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/finance")
public class FeeController {

    @Autowired private FeeService service;

    // ========================================================================
    // 1. FEE TYPES (Separated GET ALL and GET ACTIVE)
    // ========================================================================
    
    // Matrix: Admin/Faculty Only
    @GetMapping("/types") 
    public List<FeeType> getAllTypes(@RequestHeader("X-Actor-Id") Long actorId, 
                                     @RequestHeader("X-Role") String role) { 
        return service.getAllFeeTypes(actorId, role); 
    }

    // Matrix: Everyone (Active Only)
    @GetMapping("/types/active")
    public List<FeeType> getActiveTypes() {
        return service.getActiveFeeTypes();
    }

    @PostMapping("/types") 
    public FeeType postType(@Valid @RequestBody FeeType t, @RequestHeader("X-Actor-Id") Long actorId, @RequestHeader("X-Role") String role) { 
        return service.createFeeType(t, actorId, role); 
    }

    @GetMapping("/types/{id}")
    public FeeType getTypeById(@PathVariable Long id, @RequestHeader("X-Actor-Id") Long actorId, @RequestHeader("X-Role") String role) {
        return service.getFeeTypeById(id, actorId, role);
    }

    @PutMapping("/types/{id}") 
    public FeeType putType(@PathVariable Long id, @Valid @RequestBody FeeType t, @RequestHeader("X-Actor-Id") Long actorId, @RequestHeader("X-Role") String role) { 
        return service.updateFeeType(id, t, actorId, role); 
    }

    @DeleteMapping("/types/{id}") 
    public void delType(@PathVariable Long id, @RequestHeader("X-Actor-Id") Long actorId, @RequestHeader("X-Role") String role) { 
        service.deleteFeeType(id, actorId, role); 
    }

    // ========================================================================
    // 2. FEE STRUCTURES (Added Academic Year & GET OWN)
    // ========================================================================
    @PostMapping("/structures") 
    public FeeStructure postStruct(@Valid @RequestBody FeeStructure s, @RequestHeader("X-Actor-Id") Long actorId, @RequestHeader("X-Role") String role) { 
        return service.createFeeStructure(s, actorId, role); 
    }

    @GetMapping("/structures") 
    public List<FeeStructure> getStructs(@RequestHeader("X-Actor-Id") Long actorId, 
                                         @RequestHeader("X-Role") String role,
                                         @RequestParam(required = false) Long courseId,
                                         @RequestParam(required = false) String academicYear) { 
        // Logic inside service now handles Faculty bypass and Student "Own" filtering
        return service.getFeeStructures(actorId, role, courseId, academicYear); 
    }

    @GetMapping("/structures/{id}")
    public FeeStructure getStructById(@PathVariable Long id, @RequestHeader("X-Actor-Id") Long actorId, @RequestHeader("X-Role") String role) {
        return service.getFeeStructureById(id, actorId, role);
    }

    @PutMapping("/structures/{id}") 
    public FeeStructure putStruct(@PathVariable Long id, @Valid @RequestBody FeeStructure s, @RequestHeader("X-Actor-Id") Long actorId, @RequestHeader("X-Role") String role) { 
        return service.updateFeeStructure(id, s, actorId, role); 
    }

    @DeleteMapping("/structures/{id}")
    public void delStruct(@PathVariable Long id, @RequestHeader("X-Actor-Id") Long actorId, @RequestHeader("X-Role") String role) {
        service.deleteFeeStructure(id, actorId, role);
    }

    // ========================================================================
    // 3. STUDENT FEE ALLOCATIONS (Parent-Child Verification Logic)
    // ========================================================================
    @PostMapping("/allocations") 
    public StudentFeeAllocation postAlloc(@Valid @RequestBody StudentFeeAllocation a, @RequestHeader("X-Actor-Id") Long actorId, @RequestHeader("X-Role") String role) { 
        return service.allocateFee(a, actorId, role); 
    }

    @GetMapping("/allocations") 
    public List<StudentFeeAllocation> getAllAlloc(@RequestHeader("X-Actor-Id") Long actorId, 
                                                  @RequestHeader("X-Role") String role,
                                                  @RequestParam(required = false) Long userId) { 
        return service.getAllocations(actorId, role, userId); 
    }

    @GetMapping("/allocations/{id}")
    public StudentFeeAllocation getAllocById(@PathVariable Long id, @RequestHeader("X-Actor-Id") Long actorId, @RequestHeader("X-Role") String role) {
        return service.getAllocationById(id, actorId, role);
    }

    @PutMapping("/allocations/{id}") 
    public StudentFeeAllocation putAlloc(@PathVariable Long id, @Valid @RequestBody StudentFeeAllocation a, @RequestHeader("X-Actor-Id") Long actorId, @RequestHeader("X-Role") String role) { 
        return service.updateAllocation(id, a, actorId, role); 
    }
    
    @DeleteMapping("/allocations/{id}")
    public void delAlloc(@PathVariable Long id, @RequestHeader("X-Actor-Id") Long actorId, @RequestHeader("X-Role") String role) {
        service.deleteAllocation(id, actorId, role); // Service layer throws Exception as per Matrix
    }

    // ========================================================================
    // 4. STUDENT FEE PAYMENTS (Secure GET by ID)
    // ========================================================================
    @PostMapping("/payments") 
    public StudentFeePayment postPay(@Valid @RequestBody StudentFeePayment p, @RequestHeader("X-Actor-Id") Long actorId, @RequestHeader("X-Role") String role) { 
        return service.processPayment(p, actorId, role); 
    }

    @GetMapping("/payments") 
    public List<StudentFeePayment> getAllPay(@RequestHeader("X-Actor-Id") Long actorId, @RequestHeader("X-Role") String role) { 
        return service.getPayments(actorId, role); 
    }

    @GetMapping("/payments/{id}")
    public StudentFeePayment getPayById(@PathVariable Long id, @RequestHeader("X-Actor-Id") Long actorId, @RequestHeader("X-Role") String role) {
        return service.getPaymentById(id, actorId, role);
    }

    // ========================================================================
    // 5. FEE DISCOUNTS (Parent-Child Secure GET)
    // ========================================================================
    @PostMapping("/discounts") 
    public FeeDiscount postDisc(@Valid @RequestBody FeeDiscount d, @RequestHeader("X-Actor-Id") Long actorId, @RequestHeader("X-Role") String role) { 
        return service.applyDiscount(d, actorId, role); 
    }

    @GetMapping("/discounts") 
    public List<FeeDiscount> getAllDisc(@RequestHeader("X-Actor-Id") Long actorId, @RequestHeader("X-Role") String role) { 
        return service.getDiscounts(actorId, role); 
    }

    @GetMapping("/discounts/{id}")
    public FeeDiscount getDiscById(@PathVariable Long id, @RequestHeader("X-Actor-Id") Long actorId, @RequestHeader("X-Role") String role) {
        return service.getDiscountById(id, actorId, role);
    }

    @PutMapping("/discounts/{id}")
    public FeeDiscount putDisc(@PathVariable Long id, @Valid @RequestBody FeeDiscount d, @RequestHeader("X-Actor-Id") Long actorId, @RequestHeader("X-Role") String role) {
        return service.updateDiscount(id, d, actorId, role);
    }

    @DeleteMapping("/discounts/{id}")
    public void delDisc(@PathVariable Long id, @RequestHeader("X-Actor-Id") Long actorId, @RequestHeader("X-Role") String role) {
        service.deleteDiscount(id, actorId, role);
    }

    // ========================================================================
    // 6. FEE REFUNDS (Parent ❌ Restriction in Service)
    // ========================================================================
    @PostMapping("/refunds") 
    public FeeRefund postRef(@Valid @RequestBody FeeRefund r, @RequestHeader("X-Actor-Id") Long actorId, @RequestHeader("X-Role") String role) { 
        return service.processRefund(r, actorId, role); 
    }

    @GetMapping("/refunds") 
    public List<FeeRefund> getAllRef(@RequestHeader("X-Actor-Id") Long actorId, @RequestHeader("X-Role") String role) { 
        return service.getRefunds(actorId, role); 
    }

    @GetMapping("/refunds/{id}")
    public FeeRefund getRefById(@PathVariable Long id, @RequestHeader("X-Actor-Id") Long actorId, @RequestHeader("X-Role") String role) {
        return service.getRefundById(id, actorId, role);
    }

    // ========================================================================
    // 7. AUDIT LOGS
    // ========================================================================
    @GetMapping("/audit") 
    public List<AuditLog> getAllAudit(@RequestHeader("X-Actor-Id") Long actorId, @RequestHeader("X-Role") String role) { 
        return service.getAuditLogs(actorId, role); 
    }
    
    @GetMapping("/audit/{id}")
    public AuditLog getAuditLogById(@PathVariable Long id, @RequestHeader("X-Actor-Id") Long actorId, @RequestHeader("X-Role") String role) {
        return service.getAuditLogById(id, actorId, role);
    }

    // ========================================================================
    // 8. FEE RECEIPTS
    // ========================================================================
    @GetMapping("/receipts") 
    public List<FeeReceipt> getAllRec(@RequestHeader("X-Actor-Id") Long actorId, @RequestHeader("X-Role") String role) { 
        return service.getReceipts(actorId, role); 
    }

    @GetMapping("/receipts/{id}")
    public FeeReceipt getRecById(@PathVariable Long id, @RequestHeader("X-Actor-Id") Long actorId, @RequestHeader("X-Role") String role) {
        return service.getReceiptById(id, actorId, role);
    }
}