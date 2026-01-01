package com.graphy.lms.controller;

import com.graphy.lms.entity.*;
import com.graphy.lms.service.FeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/fees")
public class FeeController {

    @Autowired 
    private FeeService feeService;

    // --- 1. FEE TYPE ---
    @PostMapping("/types") 
    public FeeType addType(@RequestBody FeeType ft) { return feeService.saveFeeType(ft); }
    @GetMapping("/types/{id}") 
    public FeeType getType(@PathVariable Long id) { return feeService.getFeeTypeById(id); }
    @GetMapping("/types") 
    public List<FeeType> getAllTypes() { return feeService.getAllFeeTypes(); }
    @PutMapping("/types/{id}") 
    public FeeType updateType(@PathVariable Long id, @RequestBody FeeType ft) { return feeService.updateFeeType(id, ft); }
    @DeleteMapping("/types/{id}") 
    public String deleteType(@PathVariable Long id) { feeService.deleteFeeType(id); return "Deleted Type " + id; }

    // --- 2. FEE STRUCTURE ---
    @PostMapping("/structures") 
    public FeeStructure addStructure(@RequestBody FeeStructure fs) { return feeService.saveFeeStructure(fs); }
    @GetMapping("/structures/{id}") 
    public FeeStructure getStructure(@PathVariable Long id) { return feeService.getFeeStructureById(id); }
    @GetMapping("/structures") 
    public List<FeeStructure> getAllStructures() { return feeService.getAllFeeStructures(); }
    @PutMapping("/structures/{id}") 
    public FeeStructure updateStructure(@PathVariable Long id, @RequestBody FeeStructure fs) { return feeService.updateFeeStructure(id, fs); }
    @DeleteMapping("/structures/{id}") 
    public String deleteStructure(@PathVariable Long id) { feeService.deleteFeeStructure(id); return "Deleted Structure " + id; }

    // --- 3. ALLOCATIONS ---
    @PostMapping("/allocations") 
    public StudentFeeAllocation addAlloc(@RequestBody StudentFeeAllocation sfa) { return feeService.saveAllocation(sfa); }
    @GetMapping("/allocations/{id}") 
    public StudentFeeAllocation getAlloc(@PathVariable Long id) { return feeService.getAllocationById(id); }
    @GetMapping("/allocations") 
    public List<StudentFeeAllocation> getAllAlloc() { return feeService.getAllAllocations(); }
    @PutMapping("/allocations/{id}") 
    public StudentFeeAllocation updateAlloc(@PathVariable Long id, @RequestBody StudentFeeAllocation sfa) { return feeService.updateAllocation(id, sfa); }
    @DeleteMapping("/allocations/{id}") 
    public String deleteAlloc(@PathVariable Long id) { feeService.deleteAllocation(id); return "Deleted Allocation " + id; }

    // --- 4. PAYMENTS ---
    @PostMapping("/payments") 
    public StudentFeePayment addPay(@RequestBody StudentFeePayment sfp) { return feeService.savePayment(sfp); }
    @GetMapping("/payments/{id}") 
    public StudentFeePayment getPay(@PathVariable Long id) { return feeService.getPaymentById(id); }
    @GetMapping("/payments") 
    public List<StudentFeePayment> getAllPay() { return feeService.getAllPayments(); }
    @PutMapping("/payments/{id}") 
    public StudentFeePayment updatePay(@PathVariable Long id, @RequestBody StudentFeePayment sfp) { return feeService.updatePayment(id, sfp); }
    @DeleteMapping("/payments/{id}") 
    public String deletePay(@PathVariable Long id) { feeService.deletePayment(id); return "Deleted Payment " + id; }

    // --- 5. DISCOUNTS ---
    @PostMapping("/discounts") 
    public FeeDiscount addDisc(@RequestBody FeeDiscount fd) { return feeService.saveDiscount(fd); }
    @GetMapping("/discounts/{id}") 
    public FeeDiscount getDisc(@PathVariable Long id) { return feeService.getDiscountById(id); }
    @GetMapping("/discounts") 
    public List<FeeDiscount> getAllDisc() { return feeService.getAllDiscounts(); }
    @PutMapping("/discounts/{id}") 
    public FeeDiscount updateDisc(@PathVariable Long id, @RequestBody FeeDiscount fd) { return feeService.updateDiscount(id, fd); }
    @DeleteMapping("/discounts/{id}") 
    public String deleteDisc(@PathVariable Long id) { feeService.deleteDiscount(id); return "Deleted Discount " + id; }

    // --- 6. REFUNDS ---
    @PostMapping("/refunds") 
    public FeeRefund addRef(@RequestBody FeeRefund fr) { return feeService.saveRefund(fr); }
    @GetMapping("/refunds/{id}") 
    public FeeRefund getRef(@PathVariable Long id) { return feeService.getRefundById(id); }
    @GetMapping("/refunds") 
    public List<FeeRefund> getAllRef() { return feeService.getAllRefunds(); }
    @PutMapping("/refunds/{id}") 
    public FeeRefund updateRef(@PathVariable Long id, @RequestBody FeeRefund fr) { return feeService.updateRefund(id, fr); }
    @DeleteMapping("/refunds/{id}") 
    public String deleteRef(@PathVariable Long id) { feeService.deleteRefund(id); return "Deleted Refund " + id; }

    // --- 7. AUDIT LOGS ---
    @GetMapping("/audit-logs") 
    public List<AuditLog> getLogs() { return feeService.getAllAuditLogs(); }

    // ADD THIS NEW METHOD TO FIX THE 404 ERROR
    @GetMapping("/audit-logs/{id}")
    public AuditLog getLogById(@PathVariable Long id) { 
        return feeService.getAuditLogById(id); 
    }
}