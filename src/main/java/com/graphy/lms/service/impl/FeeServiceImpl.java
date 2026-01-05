package com.graphy.lms.service.impl;

import com.graphy.lms.entity.*;
import com.graphy.lms.repository.*;
import com.graphy.lms.service.FeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    // --- SECURITY & AUDIT UTILITIES ---

    private void checkAdmin(String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new RuntimeException("Access Denied: Admin role required.");
        }
    }

    private void verifyParentChild(Long actorId, Long studentUserId) {
        // Verification based on existing allocation records for the student
        boolean exists = allocRepo.existsByUserId(studentUserId);
        if (!exists) {
            throw new RuntimeException("Access Denied: No verified relationship with Student ID " + studentUserId);
        }
    }

    private void logAction(String module, Long id, String action, Long actorId) {
        AuditLog log = new AuditLog();
        log.setModule(module);
        log.setEntityId(id);
        log.setAction(action);
        log.setPerformedBy(actorId);
        log.setPerformedAt(LocalDateTime.now());
        auditRepo.save(log);
    }

    // ========================================================================
    // 1. FEE TYPES
    // ========================================================================
    @Override
    public List<FeeType> getAllFeeTypes(Long actorId, String role) {
        if (!"ADMIN".equalsIgnoreCase(role) && !"FACULTY".equalsIgnoreCase(role)) {
            throw new RuntimeException("Access Denied: GET ALL reserved for Admin/Faculty.");
        }
        return typeRepo.findAll();
    }

    @Override
    public List<FeeType> getActiveFeeTypes() {
        return typeRepo.findByIsActiveTrue();
    }

    @Override
    public FeeType createFeeType(FeeType ft, Long actorId, String role) {
        checkAdmin(role);
        ft.setId(null);
        FeeType saved = typeRepo.save(ft);
        logAction("FEE_TYPE", saved.getId(), "POST", actorId);
        return saved;
    }

    @Override
    public FeeType getFeeTypeById(Long id, Long actorId, String role) {
        return typeRepo.findById(id).orElseThrow(() -> new RuntimeException("FeeType not found"));
    }

    @Override
    public FeeType updateFeeType(Long id, FeeType ft, Long actorId, String role) {
        checkAdmin(role);
        // Fetch-then-Update
        FeeType ex = typeRepo.findById(id).orElseThrow(() -> new RuntimeException("FeeType ID not found: " + id));
        
        // Map all columns for response visibility
        ex.setName(ft.getName());
        ex.setDescription(ft.getDescription());
        ex.setIsActive(ft.getIsActive());
        ex.setUpdatedAt(LocalDateTime.now());
        
        logAction("FEE_TYPE", id, "PUT", actorId);
        return typeRepo.save(ex);
    }

    @Override
    public void deleteFeeType(Long id, Long actorId, String role) {
        checkAdmin(role);
        typeRepo.deleteById(id);
        logAction("FEE_TYPE", id, "DELETE", actorId);
    }

    // ========================================================================
    // 2. FEE STRUCTURES
    // ========================================================================
    @Override
    public List<FeeStructure> getFeeStructures(Long actorId, String role, Long courseId, String academicYear) {
        if ("ADMIN".equalsIgnoreCase(role)) return structRepo.findAll();

        if ("FACULTY".equalsIgnoreCase(role)) {
            if (courseId == null) throw new RuntimeException("Faculty must specify a courseId.");
            return (academicYear != null) ? structRepo.findByCourseIdAndAcademicYear(courseId, academicYear) 
                                          : structRepo.findByCourseId(courseId);
        }

        if ("STUDENT".equalsIgnoreCase(role) || "PARENT".equalsIgnoreCase(role)) {
            return allocRepo.findByUserId(actorId).stream()
                    .map(StudentFeeAllocation::getFeeStructure)
                    .distinct()
                    .collect(Collectors.toList());
        }
        throw new RuntimeException("Access Denied.");
    }

    @Override
    public FeeStructure createFeeStructure(FeeStructure fs, Long actorId, String role) {
        checkAdmin(role);
        fs.setId(null);
        FeeStructure saved = structRepo.save(fs);
        logAction("FEE_STRUCTURE", saved.getId(), "POST", actorId);
        return saved;
    }

    @Override
    public FeeStructure getFeeStructureById(Long id, Long actorId, String role) {
        return structRepo.findById(id).orElseThrow(() -> new RuntimeException("Structure not found"));
    }

    @Override
    public FeeStructure updateFeeStructure(Long id, FeeStructure fs, Long actorId, String role) {
        checkAdmin(role);
        FeeStructure ex = structRepo.findById(id).orElseThrow(() -> new RuntimeException("Structure ID not found: " + id));
        
        ex.setAcademicYear(fs.getAcademicYear());
        ex.setTotalAmount(fs.getTotalAmount());
        ex.setCourseId(fs.getCourseId());
        ex.setFeeType(fs.getFeeType());
        ex.setUpdatedAt(LocalDateTime.now());
        
        logAction("FEE_STRUCTURE", id, "PUT", actorId);
        return structRepo.save(ex);
    }

    @Override
    public void deleteFeeStructure(Long id, Long actorId, String role) {
        checkAdmin(role);
        structRepo.deleteById(id);
        logAction("FEE_STRUCTURE", id, "DELETE", actorId);
    }

    // ========================================================================
    // 3. STUDENT FEE ALLOCATIONS
    // ========================================================================
    @Override
    public List<StudentFeeAllocation> getAllocations(Long actorId, String role, Long userId) {
        if ("ADMIN".equalsIgnoreCase(role)) return allocRepo.findAll();
        if ("STUDENT".equalsIgnoreCase(role)) return allocRepo.findByUserId(actorId);
        if ("PARENT".equalsIgnoreCase(role)) {
            if (userId == null) throw new RuntimeException("Parent must specify student userId.");
            verifyParentChild(actorId, userId);
            return allocRepo.findByUserId(userId);
        }
        throw new RuntimeException("Access Denied.");
    }

    @Override
    public StudentFeeAllocation getAllocationById(Long id, Long actorId, String role) {
        StudentFeeAllocation sfa = allocRepo.findById(id).orElseThrow(() -> new RuntimeException("Allocation not found"));
        if ("ADMIN".equalsIgnoreCase(role)) return sfa;
        if ("STUDENT".equalsIgnoreCase(role) && sfa.getUserId().equals(actorId)) return sfa;
        if ("PARENT".equalsIgnoreCase(role)) {
            verifyParentChild(actorId, sfa.getUserId());
            return sfa;
        }
        throw new RuntimeException("Access Denied.");
    }

    @Override
    public StudentFeeAllocation allocateFee(StudentFeeAllocation sfa, Long actorId, String role) {
        checkAdmin(role);
        sfa.setId(null);
        StudentFeeAllocation saved = allocRepo.save(sfa);
        logAction("ALLOCATION", saved.getId(), "POST", actorId);
        return saved;
    }

    @Override
    public StudentFeeAllocation updateAllocation(Long id, StudentFeeAllocation sfa, Long actorId, String role) {
        checkAdmin(role);
        StudentFeeAllocation ex = allocRepo.findById(id).orElseThrow(() -> new RuntimeException("Allocation ID not found: " + id));
        
        ex.setUserId(sfa.getUserId());
        ex.setFeeStructure(sfa.getFeeStructure());
        ex.setDueDate(sfa.getDueDate());
        ex.setAllocationDate(sfa.getAllocationDate());
        ex.setStatus(sfa.getStatus());
        ex.setAmountPaid(sfa.getAmountPaid());
        ex.setUpdatedAt(LocalDateTime.now());
        
        logAction("ALLOCATION", id, "PUT", actorId);
        return allocRepo.save(ex);
    }

    @Override
    public void deleteAllocation(Long id, Long actorId, String role) {
        throw new RuntimeException("Deletion prohibited for financial audit integrity.");
    }

    // ========================================================================
    // 4. STUDENT FEE PAYMENTS
    // ========================================================================
    @Override
    @Transactional
    public StudentFeePayment processPayment(StudentFeePayment p, Long actorId, String role) {
        if (!"ADMIN".equalsIgnoreCase(role) && !"STUDENT".equalsIgnoreCase(role)) {
            throw new RuntimeException("Unauthorized role for payment.");
        }
        p.setId(null);
        StudentFeePayment saved = payRepo.save(p);

        FeeReceipt receipt = new FeeReceipt();
        receipt.setStudentFeePayment(saved);
        receipt.setReceiptNumber("REC-" + System.currentTimeMillis());
        receiptRepo.save(receipt);

        logAction("PAYMENT", saved.getId(), "POST", actorId);
        return saved;
    }

    @Override
    public List<StudentFeePayment> getPayments(Long actorId, String role) {
        if ("ADMIN".equalsIgnoreCase(role)) return payRepo.findAll();
        if ("STUDENT".equalsIgnoreCase(role)) return payRepo.findByStudentFeeAllocationUserId(actorId);
        if ("PARENT".equalsIgnoreCase(role)) return payRepo.findByStudentFeeAllocationUserId(actorId);
        throw new RuntimeException("Access Denied.");
    }

    @Override
    public StudentFeePayment getPaymentById(Long id, Long actorId, String role) {
        StudentFeePayment p = payRepo.findById(id).orElseThrow(() -> new RuntimeException("Payment not found"));
        if ("ADMIN".equalsIgnoreCase(role)) return p;
        Long studentId = p.getStudentFeeAllocation().getUserId();
        if ("STUDENT".equalsIgnoreCase(role) && studentId.equals(actorId)) return p;
        if ("PARENT".equalsIgnoreCase(role)) {
            verifyParentChild(actorId, studentId);
            return p;
        }
        throw new RuntimeException("Access Denied.");
    }

    // ========================================================================
    // 5. FEE DISCOUNTS (New Implementation)
    // ========================================================================
    @Override
    public FeeDiscount applyDiscount(FeeDiscount fd, Long actorId, String role) {
        checkAdmin(role);
        fd.setId(null);
        FeeDiscount saved = discRepo.save(fd);
        logAction("DISCOUNT", saved.getId(), "POST", actorId);
        return saved;
    }

    @Override
    public List<FeeDiscount> getDiscounts(Long actorId, String role) {
        if ("ADMIN".equalsIgnoreCase(role)) return discRepo.findAll();
        if ("STUDENT".equalsIgnoreCase(role)) return discRepo.findByUserId(actorId);
        if ("PARENT".equalsIgnoreCase(role)) return discRepo.findByUserId(actorId);
        throw new RuntimeException("Access Denied.");
    }

    @Override
    public FeeDiscount getDiscountById(Long id, Long actorId, String role) {
        FeeDiscount fd = discRepo.findById(id).orElseThrow(() -> new RuntimeException("Discount not found"));
        if ("ADMIN".equalsIgnoreCase(role)) return fd;
        if (("STUDENT".equalsIgnoreCase(role) || "PARENT".equalsIgnoreCase(role)) && fd.getUserId().equals(actorId)) {
            if ("PARENT".equalsIgnoreCase(role)) verifyParentChild(actorId, fd.getUserId());
            return fd;
        }
        throw new RuntimeException("Access Denied.");
    }

    @Override
    public FeeDiscount updateDiscount(Long id, FeeDiscount fd, Long actorId, String role) {
        checkAdmin(role);
        FeeDiscount ex = discRepo.findById(id).orElseThrow(() -> new RuntimeException("Discount ID not found: " + id));
        
        ex.setUserId(fd.getUserId());
        ex.setFeeStructure(fd.getFeeStructure());
        ex.setDiscountType(fd.getDiscountType());
        ex.setDiscountValue(fd.getDiscountValue());
        ex.setReason(fd.getReason());
        ex.setApprovedBy(fd.getApprovedBy());
        ex.setApprovedDate(fd.getApprovedDate());
        ex.setUpdatedAt(LocalDateTime.now());
        
        logAction("DISCOUNT", id, "PUT", actorId);
        return discRepo.save(ex);
    }

    @Override
    public void deleteDiscount(Long id, Long actorId, String role) {
        checkAdmin(role);
        discRepo.deleteById(id);
        logAction("DISCOUNT", id, "DELETE", actorId);
    }

    // ========================================================================
    // 6. FEE REFUNDS
    // ========================================================================
    @Override
    public List<FeeRefund> getRefunds(Long actorId, String role) {
        if ("ADMIN".equalsIgnoreCase(role)) return refundRepo.findAll();
        if ("STUDENT".equalsIgnoreCase(role)) return refundRepo.findByStudentFeePaymentStudentFeeAllocationUserId(actorId);
        throw new RuntimeException("Access Denied: Parents cannot view refunds.");
    }

    @Override
    public FeeRefund getRefundById(Long id, Long actorId, String role) {
        FeeRefund fr = refundRepo.findById(id).orElseThrow(() -> new RuntimeException("Refund not found"));
        if ("ADMIN".equalsIgnoreCase(role)) return fr;
        Long studentId = fr.getStudentFeePayment().getStudentFeeAllocation().getUserId();
        if ("STUDENT".equalsIgnoreCase(role) && studentId.equals(actorId)) return fr;
        throw new RuntimeException("Access Denied: Restricted access for refunds.");
    }

    @Override
    public FeeRefund processRefund(FeeRefund fr, Long actorId, String role) {
        checkAdmin(role);
        fr.setId(null);
        FeeRefund saved = refundRepo.save(fr);
        logAction("REFUND", saved.getId(), "POST", actorId);
        return saved;
    }

    // ========================================================================
    // 8. FEE RECEIPTS
    // ========================================================================
    @Override
    public List<FeeReceipt> getReceipts(Long actorId, String role) {
        if ("ADMIN".equalsIgnoreCase(role)) return receiptRepo.findAll();
        return receiptRepo.findByStudentFeePaymentStudentFeeAllocationUserId(actorId);
    }

    @Override
    public FeeReceipt getReceiptById(Long id, Long actorId, String role) {
        FeeReceipt fr = receiptRepo.findById(id).orElseThrow(() -> new RuntimeException("Receipt not found"));
        if ("ADMIN".equalsIgnoreCase(role)) return fr;
        Long studentId = fr.getStudentFeePayment().getStudentFeeAllocation().getUserId();
        if ("STUDENT".equalsIgnoreCase(role) && studentId.equals(actorId)) return fr;
        if ("PARENT".equalsIgnoreCase(role)) {
            verifyParentChild(actorId, studentId);
            return fr;
        }
        throw new RuntimeException("Access Denied.");
    }
    
    // --- Audit Logs ---
    @Override public List<AuditLog> getAuditLogs(Long actorId, String role) { checkAdmin(role); return auditRepo.findAll(); }
    @Override public AuditLog getAuditLogById(Long id, Long actorId, String role) { checkAdmin(role); return auditRepo.findById(id).orElseThrow(() -> new RuntimeException("Log not found")); }
}