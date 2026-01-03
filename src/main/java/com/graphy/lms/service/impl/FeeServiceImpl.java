package com.graphy.lms.service.impl;

import com.graphy.lms.entity.*;
import com.graphy.lms.repository.*;
import com.graphy.lms.service.FeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeeServiceImpl implements FeeService {

    @Autowired private FeeTypeRepository typeRepo;
    @Autowired private FeeStructureRepository structRepo;
    @Autowired private StudentFeeAllocationRepository allocRepo;
    @Autowired private StudentFeePaymentRepository payRepo;
    @Autowired private FeeDiscountRepository discRepo;
    @Autowired private FeeRefundRepository refundRepo;
    @Autowired private AuditLogRepository auditRepo;
    @Autowired private FeeReceiptRepository receiptRepo;

    private void validateCourseFromAcademic(Long courseId) {
        if(courseId == null || courseId <= 0) throw new RuntimeException("Academic Validation Failed: Invalid Course ID");
    }

    private void logAction(String module, Long id, String action, Long actorId) {
        AuditLog log = new AuditLog();
        log.setModule(module);
        log.setEntityId(id);
        log.setAction(action);
        log.setPerformedBy(actorId);
        auditRepo.save(log);
    }

    // --- FEE TYPES ---
    @Override 
    public FeeType createFeeType(FeeType ft, Long actorId) {
        ft.setId(null); // FIX: Prevents "Detached Entity" error by forcing a new record
        ft.setCreatedAt(LocalDateTime.now());
        FeeType saved = typeRepo.save(ft);
        logAction("FEE_TYPE", saved.getId(), "POST", actorId);
        return saved;
    }
    
    @Override 
    public FeeType updateFeeType(Long id, FeeType ft, Long actorId) {
        FeeType ex = typeRepo.findById(id).orElseThrow(() -> new RuntimeException("Update Failed: FeeType ID " + id + " not found"));
        // Fetch-then-Update: Syncing all columns
        if(ft.getName() != null) ex.setName(ft.getName());
        if(ft.getDescription() != null) ex.setDescription(ft.getDescription());
        if(ft.getIsActive() != null) ex.setIsActive(ft.getIsActive());
        
        ex.setUpdatedAt(LocalDateTime.now()); // Mentor rule: Update timestamp on PUT
        logAction("FEE_TYPE", id, "PUT", actorId);
        return typeRepo.save(ex);
    }
    @Override public List<FeeType> getAllFeeTypes() { return typeRepo.findAll(); }
    @Override public FeeType getFeeTypeById(Long id) { return typeRepo.findById(id).orElse(null); }
    @Override public void deleteFeeType(Long id, Long actorId) { typeRepo.deleteById(id); logAction("FEE_TYPE", id, "DELETE", actorId); }

    // --- FEE STRUCTURES ---
    @Override 
    public FeeStructure createFeeStructure(FeeStructure fs, Long actorId) {
        validateCourseFromAcademic(fs.getCourseId());
        fs.setId(null); // FIX
        fs.setCreatedAt(LocalDateTime.now());
        FeeStructure saved = structRepo.save(fs);
        logAction("FEE_STRUCTURE", saved.getId(), "POST", actorId);
        return saved;
    }
    
    @Override 
    public FeeStructure updateFeeStructure(Long id, FeeStructure fs, Long actorId) {
        FeeStructure ex = structRepo.findById(id).orElseThrow(() -> new RuntimeException("ID Not Found"));
        if(fs.getAcademicYear() != null) ex.setAcademicYear(fs.getAcademicYear());
        if(fs.getTotalAmount() != null) ex.setTotalAmount(fs.getTotalAmount());
        if(fs.getCourseId() != null) { validateCourseFromAcademic(fs.getCourseId()); ex.setCourseId(fs.getCourseId()); }
        if(fs.getFeeType() != null) ex.setFeeType(fs.getFeeType());
        
        ex.setUpdatedAt(LocalDateTime.now());
        logAction("FEE_STRUCTURE", id, "PUT", actorId);
        return structRepo.save(ex);
    }
    @Override public List<FeeStructure> getAllFeeStructures() { return structRepo.findAll(); }
    @Override public FeeStructure getFeeStructureById(Long id) { return structRepo.findById(id).orElse(null); }
    @Override public void deleteFeeStructure(Long id, Long actorId) { structRepo.deleteById(id); logAction("FEE_STRUCTURE", id, "DELETE", actorId); }

    // --- ALLOCATIONS ---
    @Override 
    public StudentFeeAllocation allocateFee(StudentFeeAllocation sfa, Long actorId) {
        sfa.setId(null); // FIX
        sfa.setCreatedAt(LocalDateTime.now());
        StudentFeeAllocation saved = allocRepo.save(sfa);
        logAction("ALLOCATION", saved.getId(), "POST", actorId);
        return saved;
    }
    
    @Override 
    public StudentFeeAllocation updateAllocation(Long id, StudentFeeAllocation sfa, Long actorId) {
        StudentFeeAllocation ex = allocRepo.findById(id).orElseThrow(() -> new RuntimeException("ID Not Found"));
        if(sfa.getDueDate() != null) ex.setDueDate(sfa.getDueDate());
        if(sfa.getUserId() != null) ex.setUserId(sfa.getUserId());
        if(sfa.getFeeStructure() != null) ex.setFeeStructure(sfa.getFeeStructure());
        
        ex.setUpdatedAt(LocalDateTime.now());
        logAction("ALLOCATION", id, "PUT", actorId);
        return allocRepo.save(ex);
    }
    @Override public List<StudentFeeAllocation> getAllAllocations() { return allocRepo.findAll(); }
    @Override public StudentFeeAllocation getAllocationById(Long id) { return allocRepo.findById(id).orElse(null); }
    @Override public void deleteAllocation(Long id, Long actorId) { allocRepo.deleteById(id); logAction("ALLOCATION", id, "DELETE", actorId); }

    // --- PAYMENTS & AUTO RECEIPT ---
    @Override 
    @Transactional 
    public StudentFeePayment processPayment(StudentFeePayment sfp, Long actorId) {
        sfp.setId(null); // FIX
        sfp.setCreatedAt(LocalDateTime.now());
        StudentFeePayment saved = payRepo.save(sfp);
        
        FeeReceipt receipt = new FeeReceipt();
        receipt.setStudentFeePayment(saved);
        receipt.setReceiptNumber("REC-" + System.currentTimeMillis());
        receiptRepo.save(receipt);
        
        logAction("PAYMENT", saved.getId(), "POST", actorId);
        return saved;
    }
    
    @Override 
    public StudentFeePayment updatePayment(Long id, StudentFeePayment sfp, Long actorId) {
        StudentFeePayment ex = payRepo.findById(id).orElseThrow(() -> new RuntimeException("ID Not Found"));
        if(sfp.getPaidAmount() != null) ex.setPaidAmount(sfp.getPaidAmount());
        if(sfp.getPaymentMode() != null) ex.setPaymentMode(sfp.getPaymentMode());
        if(sfp.getTransactionReference() != null) ex.setTransactionReference(sfp.getTransactionReference());
        
        ex.setUpdatedAt(LocalDateTime.now());
        logAction("PAYMENT", id, "PUT", actorId);
        return payRepo.save(ex);
    }
    @Override public List<StudentFeePayment> getAllPayments() { return payRepo.findAll(); }
    @Override public StudentFeePayment getPaymentById(Long id) { return payRepo.findById(id).orElse(null); }
    @Override public void deletePayment(Long id, Long actorId) { payRepo.deleteById(id); logAction("PAYMENT", id, "DELETE", actorId); }

    // --- DISCOUNTS ---
    @Override 
    public FeeDiscount applyDiscount(FeeDiscount fd, Long actorId) {
        fd.setId(null); // FIX
        fd.setCreatedAt(LocalDateTime.now());
        FeeDiscount saved = discRepo.save(fd);
        logAction("DISCOUNT", saved.getId(), "POST", actorId);
        return saved;
    }
    
    @Override 
    public FeeDiscount updateDiscount(Long id, FeeDiscount fd, Long actorId) {
        FeeDiscount ex = discRepo.findById(id).orElseThrow(() -> new RuntimeException("ID Not Found"));
        if(fd.getDiscountValue() != null) ex.setDiscountValue(fd.getDiscountValue());
        if(fd.getReason() != null) ex.setReason(fd.getReason());
        
        ex.setUpdatedAt(LocalDateTime.now());
        logAction("DISCOUNT", id, "PUT", actorId);
        return discRepo.save(ex);
    }
    @Override public List<FeeDiscount> getAllDiscounts() { return discRepo.findAll(); }
    @Override public FeeDiscount getDiscountById(Long id) { return discRepo.findById(id).orElse(null); }
    @Override public void deleteDiscount(Long id, Long actorId) { discRepo.deleteById(id); logAction("DISCOUNT", id, "DELETE", actorId); }

    // --- REFUNDS ---
    @Override 
    public FeeRefund processRefund(FeeRefund fr, Long actorId) {
        fr.setId(null); // FIX
        fr.setCreatedAt(LocalDateTime.now());
        FeeRefund saved = refundRepo.save(fr);
        logAction("REFUND", saved.getId(), "POST", actorId);
        return saved;
    }
    
    @Override 
    public FeeRefund updateRefund(Long id, FeeRefund fr, Long actorId) {
        FeeRefund ex = refundRepo.findById(id).orElseThrow(() -> new RuntimeException("ID Not Found"));
        if(fr.getRefundAmount() != null) ex.setRefundAmount(fr.getRefundAmount());
        if(fr.getReason() != null) ex.setReason(fr.getReason());
        
        ex.setUpdatedAt(LocalDateTime.now());
        logAction("REFUND", id, "PUT", actorId);
        return refundRepo.save(ex);
    }
    @Override public List<FeeRefund> getAllRefunds() { return refundRepo.findAll(); }
    @Override public FeeRefund getRefundById(Long id) { return refundRepo.findById(id).orElse(null); }
    @Override public void deleteRefund(Long id, Long actorId) { refundRepo.deleteById(id); logAction("REFUND", id, "DELETE", actorId); }

    // --- READ ONLY ---
    @Override public List<AuditLog> getAllAuditLogs() { return auditRepo.findAll(); }
    @Override public AuditLog getAuditLogById(Long id) { return auditRepo.findById(id).orElse(null); }
    @Override public List<FeeReceipt> getAllReceipts() { return receiptRepo.findAll(); }
    @Override public FeeReceipt getReceiptById(Long id) { return receiptRepo.findById(id).orElse(null); }
}