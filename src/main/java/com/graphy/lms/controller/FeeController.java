package com.graphy.lms.controller;

import com.graphy.lms.entity.*;
import com.graphy.lms.service.FeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/finance")
public class FeeController {

    @Autowired private FeeService service;

    // Helper to extract actor from token (Mocked for now)
    private Long getActorId() { return 1001L; }

    // FEE TYPES
    @PostMapping("/types") public FeeType postType(@RequestBody FeeType t) { return service.createFeeType(t, getActorId()); }
    @GetMapping("/types") public List<FeeType> getAllTypes() { return service.getAllFeeTypes(); }
    @GetMapping("/types/{id}") public FeeType getType(@PathVariable Long id) { return service.getFeeTypeById(id); }
    @PutMapping("/types/{id}") public FeeType putType(@PathVariable Long id, @RequestBody FeeType t) { return service.updateFeeType(id, t, getActorId()); }
    @DeleteMapping("/types/{id}") public void delType(@PathVariable Long id) { service.deleteFeeType(id, getActorId()); }

    // FEE STRUCTURES
    @PostMapping("/structures") public FeeStructure postStruct(@RequestBody FeeStructure s) { return service.createFeeStructure(s, getActorId()); }
    @GetMapping("/structures") public List<FeeStructure> getAllStructs() { return service.getAllFeeStructures(); }
    @GetMapping("/structures/{id}") public FeeStructure getStruct(@PathVariable Long id) { return service.getFeeStructureById(id); }
    @PutMapping("/structures/{id}") public FeeStructure putStruct(@PathVariable Long id, @RequestBody FeeStructure s) { return service.updateFeeStructure(id, s, getActorId()); }
    @DeleteMapping("/structures/{id}") public void delStruct(@PathVariable Long id) { service.deleteFeeStructure(id, getActorId()); }

    // ALLOCATIONS
    @PostMapping("/allocations") public StudentFeeAllocation postAlloc(@RequestBody StudentFeeAllocation a) { return service.allocateFee(a, getActorId()); }
    @GetMapping("/allocations") public List<StudentFeeAllocation> getAllAlloc() { return service.getAllAllocations(); }
    @GetMapping("/allocations/{id}") public StudentFeeAllocation getAlloc(@PathVariable Long id) { return service.getAllocationById(id); }
    @PutMapping("/allocations/{id}") public StudentFeeAllocation putAlloc(@PathVariable Long id, @RequestBody StudentFeeAllocation a) { return service.updateAllocation(id, a, getActorId()); }
    @DeleteMapping("/allocations/{id}") public void delAlloc(@PathVariable Long id) { service.deleteAllocation(id, getActorId()); }

    // PAYMENTS
    @PostMapping("/payments") public StudentFeePayment postPay(@RequestBody StudentFeePayment p) { return service.processPayment(p, getActorId()); }
    @GetMapping("/payments") public List<StudentFeePayment> getAllPay() { return service.getAllPayments(); }
    @GetMapping("/payments/{id}") public StudentFeePayment getPay(@PathVariable Long id) { return service.getPaymentById(id); }
    @PutMapping("/payments/{id}") public StudentFeePayment putPay(@PathVariable Long id, @RequestBody StudentFeePayment p) { return service.updatePayment(id, p, getActorId()); }
    @DeleteMapping("/payments/{id}") public void delPay(@PathVariable Long id) { service.deletePayment(id, getActorId()); }

    // DISCOUNTS
    @PostMapping("/discounts") public FeeDiscount postDisc(@RequestBody FeeDiscount d) { return service.applyDiscount(d, getActorId()); }
    @GetMapping("/discounts") public List<FeeDiscount> getAllDisc() { return service.getAllDiscounts(); }
    @GetMapping("/discounts/{id}") public FeeDiscount getDisc(@PathVariable Long id) { return service.getDiscountById(id); }
    @PutMapping("/discounts/{id}") public FeeDiscount putDisc(@PathVariable Long id, @RequestBody FeeDiscount d) { return service.updateDiscount(id, d, getActorId()); }
    @DeleteMapping("/discounts/{id}") public void delDisc(@PathVariable Long id) { service.deleteDiscount(id, getActorId()); }

    // REFUNDS
    @PostMapping("/refunds") public FeeRefund postRef(@RequestBody FeeRefund r) { return service.processRefund(r, getActorId()); }
    @GetMapping("/refunds") public List<FeeRefund> getAllRef() { return service.getAllRefunds(); }
    @GetMapping("/refunds/{id}") public FeeRefund getRef(@PathVariable Long id) { return service.getRefundById(id); }
    @PutMapping("/refunds/{id}") public FeeRefund putRef(@PathVariable Long id, @RequestBody FeeRefund r) { return service.updateRefund(id, r, getActorId()); }
    @DeleteMapping("/refunds/{id}") public void delRef(@PathVariable Long id) { service.deleteRefund(id, getActorId()); }

    // AUDIT (Get only)
    @GetMapping("/audit") public List<AuditLog> getAllAudit() { return service.getAllAuditLogs(); }
    @GetMapping("/audit/{id}") public AuditLog getAudit(@PathVariable Long id) { return service.getAuditLogById(id); }

    // RECEIPTS (Get only)
    @GetMapping("/receipts") public List<FeeReceipt> getAllRec() { return service.getAllReceipts(); }
    @GetMapping("/receipts/{id}") public FeeReceipt getRec(@PathVariable Long id) { return service.getReceiptById(id); }
}