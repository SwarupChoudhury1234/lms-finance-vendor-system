package com.graphy.lms.service.impl;

import com.graphy.lms.entity.*;
import com.graphy.lms.repository.*;
import com.graphy.lms.service.FeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class FeeServiceImpl implements FeeService {

    @Autowired private FeeTypeRepository feeTypeRepo;
    @Autowired private FeeStructureRepository feeStructureRepo;
    @Autowired private StudentFeeAllocationRepository allocationRepo;
    @Autowired private StudentFeePaymentRepository paymentRepo;
    @Autowired private FeeDiscountRepository discountRepo;
    @Autowired private FeeRefundRepository refundRepo;
    @Autowired private AuditLogRepository auditRepo;

    /**
     * Helper method to create audit logs matching the updated AuditLog entity.
     */
    private void createLog(String module, Long entityId, String action, String newValue, String oldValue) {
        AuditLog log = new AuditLog();
        log.setModule(module);
        log.setEntityId(entityId);
        log.setAction(action);
        log.setNewValue(newValue);
        log.setOldValue(oldValue);
        log.setPerformedBy(1L); // Default System Admin User ID
        auditRepo.save(log);
    }

    // --- 1. FEE TYPE ---
    @Override 
    @Transactional
    public FeeType saveFeeType(FeeType ft) { 
        FeeType saved = feeTypeRepo.save(ft); 
        createLog("FEE_TYPE", saved.getId(), "CREATE", saved.getName(), null);
        return saved;
    }
    
    @Override public FeeType getFeeTypeById(Long id) { return feeTypeRepo.findById(id).orElseThrow(() -> new RuntimeException("FeeType Not Found: " + id)); }
    @Override public List<FeeType> getAllFeeTypes() { return feeTypeRepo.findAll(); }
    
    @Override @Transactional public FeeType updateFeeType(Long id, FeeType details) {
        FeeType existing = getFeeTypeById(id);
        String oldVal = existing.getName();
        existing.setName(details.getName());
        existing.setDescription(details.getDescription());
        existing.setIsActive(details.getIsActive());
        FeeType updated = feeTypeRepo.save(existing);
        createLog("FEE_TYPE", id, "UPDATE", updated.getName(), oldVal);
        return updated;
    }
    
    @Override @Transactional public void deleteFeeType(Long id) { 
        FeeType existing = getFeeTypeById(id);
        feeTypeRepo.delete(existing); 
        createLog("FEE_TYPE", id, "DELETE", null, existing.getName());
    }

    // --- 2. FEE STRUCTURE ---
    @Override 
    @Transactional
    public FeeStructure saveFeeStructure(FeeStructure fs) { 
        FeeStructure saved = feeStructureRepo.save(fs); 
        createLog("FEE_STRUCTURE", saved.getId(), "CREATE", "Year: " + saved.getAcademicYear() + " Amt: " + saved.getTotalAmount(), null);
        return saved;
    }
    
    @Override public FeeStructure getFeeStructureById(Long id) { return feeStructureRepo.findById(id).orElseThrow(() -> new RuntimeException("FeeStructure Not Found: " + id)); }
    @Override public List<FeeStructure> getAllFeeStructures() { return feeStructureRepo.findAll(); }
    
    @Override @Transactional public FeeStructure updateFeeStructure(Long id, FeeStructure details) {
        FeeStructure existing = getFeeStructureById(id);
        String oldVal = "Amt: " + existing.getTotalAmount();
        existing.setAcademicYear(details.getAcademicYear());
        existing.setTotalAmount(details.getTotalAmount());
        existing.setCourseId(details.getCourseId());
        if (details.getFeeType() != null) existing.setFeeType(details.getFeeType());
        FeeStructure updated = feeStructureRepo.save(existing);
        createLog("FEE_STRUCTURE", id, "UPDATE", "Amt: " + updated.getTotalAmount(), oldVal);
        return updated;
    }
    
    @Override @Transactional public void deleteFeeStructure(Long id) { 
        FeeStructure existing = getFeeStructureById(id);
        feeStructureRepo.delete(existing); 
        createLog("FEE_STRUCTURE", id, "DELETE", null, "Year: " + existing.getAcademicYear());
    }

    // --- 3. ALLOCATIONS ---
    @Override 
    @Transactional
    public StudentFeeAllocation saveAllocation(StudentFeeAllocation sfa) { 
        StudentFeeAllocation saved = allocationRepo.save(sfa); 
        createLog("FEE_ALLOCATION", saved.getId(), "CREATE", "Status: " + saved.getStatus(), null);
        return saved;
    }
    
    @Override public StudentFeeAllocation getAllocationById(Long id) { return allocationRepo.findById(id).orElseThrow(() -> new RuntimeException("Allocation Not Found")); }
    @Override public List<StudentFeeAllocation> getAllAllocations() { return allocationRepo.findAll(); }
    
    @Override @Transactional public StudentFeeAllocation updateAllocation(Long id, StudentFeeAllocation details) {
        StudentFeeAllocation existing = getAllocationById(id);
        String oldStatus = existing.getStatus();
        existing.setUserId(details.getUserId());
        existing.setDueDate(details.getDueDate());
        existing.setStatus(details.getStatus()); 
        existing.setAmountPaid(details.getAmountPaid());
        if (details.getFeeStructure() != null) existing.setFeeStructure(details.getFeeStructure());
        StudentFeeAllocation updated = allocationRepo.save(existing);
        createLog("FEE_ALLOCATION", id, "UPDATE", updated.getStatus(), oldStatus);
        return updated;
    }
    
    @Override @Transactional public void deleteAllocation(Long id) { 
        StudentFeeAllocation existing = getAllocationById(id);
        allocationRepo.delete(existing); 
        createLog("FEE_ALLOCATION", id, "DELETE", null, "User: " + existing.getUserId());
    }

    // --- 4. PAYMENTS ---
    @Override 
    @Transactional
    public StudentFeePayment savePayment(StudentFeePayment sfp) { 
        StudentFeePayment saved = paymentRepo.save(sfp); 
        createLog("FEE_PAYMENT", saved.getId(), "CREATE", "Amt: " + saved.getPaidAmount(), null);
        return saved;
    }
    
    @Override public StudentFeePayment getPaymentById(Long id) { return paymentRepo.findById(id).orElseThrow(() -> new RuntimeException("Payment Not Found")); }
    @Override public List<StudentFeePayment> getAllPayments() { return paymentRepo.findAll(); }
    
    @Override @Transactional public StudentFeePayment updatePayment(Long id, StudentFeePayment details) {
        StudentFeePayment existing = getPaymentById(id);
        String oldRef = existing.getTransactionReference();
        existing.setPaidAmount(details.getPaidAmount());
        existing.setPaymentDate(details.getPaymentDate());
        existing.setPaymentMode(details.getPaymentMode());
        existing.setTransactionReference(details.getTransactionReference());
        if (details.getStudentFeeAllocation() != null) existing.setStudentFeeAllocation(details.getStudentFeeAllocation());
        StudentFeePayment updated = paymentRepo.save(existing);
        createLog("FEE_PAYMENT", id, "UPDATE", updated.getTransactionReference(), oldRef);
        return updated;
    }
    
    @Override @Transactional public void deletePayment(Long id) { 
        StudentFeePayment existing = getPaymentById(id);
        paymentRepo.delete(existing); 
        createLog("FEE_PAYMENT", id, "DELETE", null, "Ref: " + existing.getTransactionReference());
    }

    // --- 5. DISCOUNTS ---
    @Override 
    @Transactional
    public FeeDiscount saveDiscount(FeeDiscount fd) { 
        FeeDiscount saved = discountRepo.save(fd); 
        createLog("FEE_DISCOUNT", saved.getId(), "CREATE", "Val: " + saved.getDiscountValue(), null);
        return saved;
    }
    
    @Override public FeeDiscount getDiscountById(Long id) { return discountRepo.findById(id).orElseThrow(() -> new RuntimeException("Discount Not Found")); }
    @Override public List<FeeDiscount> getAllDiscounts() { return discountRepo.findAll(); }
    
    @Override @Transactional public FeeDiscount updateDiscount(Long id, FeeDiscount details) {
        FeeDiscount existing = getDiscountById(id);
        String oldVal = "Val: " + existing.getDiscountValue();
        existing.setUserId(details.getUserId());
        existing.setDiscountType(details.getDiscountType());
        existing.setDiscountValue(details.getDiscountValue());
        existing.setReason(details.getReason());
        existing.setApprovedBy(details.getApprovedBy());
        existing.setApprovedDate(details.getApprovedDate());
        if (details.getFeeStructure() != null) existing.setFeeStructure(details.getFeeStructure());
        FeeDiscount updated = discountRepo.save(existing);
        createLog("FEE_DISCOUNT", id, "UPDATE", "Val: " + updated.getDiscountValue(), oldVal);
        return updated;
    }
    
    @Override @Transactional public void deleteDiscount(Long id) { 
        FeeDiscount existing = getDiscountById(id);
        discountRepo.delete(existing); 
        createLog("FEE_DISCOUNT", id, "DELETE", null, "Reason: " + existing.getReason());
    }

    // --- 6. REFUNDS ---
    @Override 
    @Transactional
    public FeeRefund saveRefund(FeeRefund fr) { 
        FeeRefund saved = refundRepo.save(fr); 
        createLog("FEE_REFUND", saved.getId(), "CREATE", "Amt: " + saved.getRefundAmount(), null);
        return saved;
    }
    
    @Override public FeeRefund getRefundById(Long id) { return refundRepo.findById(id).orElseThrow(() -> new RuntimeException("Refund Not Found")); }
    @Override public List<FeeRefund> getAllRefunds() { return refundRepo.findAll(); }
    
    @Override @Transactional public FeeRefund updateRefund(Long id, FeeRefund details) {
        FeeRefund existing = getRefundById(id);
        String oldAmt = "Amt: " + existing.getRefundAmount();
        existing.setRefundAmount(details.getRefundAmount());
        existing.setRefundDate(details.getRefundDate());
        existing.setReason(details.getReason());
        if (details.getStudentFeePayment() != null) existing.setStudentFeePayment(details.getStudentFeePayment());
        FeeRefund updated = refundRepo.save(existing);
        createLog("FEE_REFUND", id, "UPDATE", "Amt: " + updated.getRefundAmount(), oldAmt);
        return updated;
    }
    
    @Override @Transactional public void deleteRefund(Long id) { 
        FeeRefund existing = getRefundById(id);
        refundRepo.delete(existing); 
        createLog("FEE_REFUND", id, "DELETE", null, "Amt: " + existing.getRefundAmount());
    }

    // --- 7. AUDIT LOGS ---
    @Override public AuditLog saveAuditLog(AuditLog al) { return auditRepo.save(al); }
    @Override public AuditLog getAuditLogById(Long id) { return auditRepo.findById(id).orElseThrow(() -> new RuntimeException("Audit Not Found")); }
    @Override public List<AuditLog> getAllAuditLogs() { return auditRepo.findAll(); }
}