package com.graphy.lms.service.impl;

import com.graphy.lms.entity.*;
import com.graphy.lms.repository.*;
import com.graphy.lms.service.FeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.Optional;

@Service
@Transactional
public class FeeServiceImpl implements FeeService {
    
    // =============== ALL REPOSITORIES ===============
    @Autowired private FeeTypeRepository feeTypeRepository;
    @Autowired private FeeStructureRepository feeStructureRepository;
    @Autowired private StudentFeeAllocationRepository allocationRepository;
    @Autowired private PaymentInstallmentRepository installmentRepository;
    @Autowired private StudentFeePaymentRepository paymentRepository;
    @Autowired private FeeDiscountRepository discountRepository;
    @Autowired private FeeRefundRepository refundRepository;
    @Autowired private FeeReceiptRepository receiptRepository;
    @Autowired private AuditLogRepository auditLogRepository;
    @Autowired private CurrencyRateRepository currencyRateRepository;
    @Autowired private NotificationLogRepository notificationLogRepository;
    @Autowired private AttendancePenaltyRepository attendancePenaltyRepository;
    @Autowired private CertificateBlockRepository certificateBlockRepository;
    @Autowired private AutoDebitSettingRepository autoDebitSettingRepository;
    @Autowired private FeeReportRepository feeReportRepository;
    @Autowired private PaymentAlternativeRepository paymentAlternativeRepository;
    
    // =============== SECURITY & UTILITY METHODS ===============
    private void checkAdmin(String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new RuntimeException("Access Denied: Admin role required");
        }
    }
    
    private void checkStudent(String role) {
        if (!"STUDENT".equalsIgnoreCase(role)) {
            throw new RuntimeException("Access Denied: Student role required");
        }
    }
    
    private void logAudit(String module, Long entityId, String action, Long actorId) {
        AuditLog log = new AuditLog();
        log.setModule(module);
        log.setEntityId(entityId);
        log.setAction(action);
        log.setPerformedBy(actorId);
        log.setPerformedAt(LocalDateTime.now());
        auditLogRepository.save(log);
    }
    
    // Helper method to check if repository has a method
    private boolean hasMethod(Object repository, String methodName) {
        try {
            repository.getClass().getMethod(methodName, Long.class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
    
    // =============== FEE TYPES IMPLEMENTATION ===============
    @Override
    public FeeType createFeeType(FeeType feeType, Long actorId, String role) {
        checkAdmin(role);
        feeType.setId(null);
        feeType.setCreatedAt(LocalDateTime.now());
        feeType.setUpdatedAt(null);
        FeeType saved = feeTypeRepository.save(feeType);
        logAudit("FEE_TYPE", saved.getId(), "CREATE", actorId);
        return saved;
    }
    
    @Override
    public List<FeeType> getAllFeeTypes(Long actorId, String role) {
        if (!"ADMIN".equalsIgnoreCase(role) && !"FACULTY".equalsIgnoreCase(role)) {
            throw new RuntimeException("Access Denied: GET ALL reserved for Admin/Faculty");
        }
        return feeTypeRepository.findAll();
    }
    
    @Override
    public List<FeeType> getActiveFeeTypes() {
        return feeTypeRepository.findByIsActiveTrue();
    }
    
    @Override
    public FeeType getFeeTypeById(Long id, Long actorId, String role) {
        return feeTypeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("FeeType not found with id: " + id));
    }
    
    @Override
    public FeeType updateFeeType(Long id, FeeType feeType, Long actorId, String role) {
        checkAdmin(role);
        FeeType existing = getFeeTypeById(id, actorId, role);
        
        if (feeType.getName() != null) existing.setName(feeType.getName());
        if (feeType.getDescription() != null) existing.setDescription(feeType.getDescription());
        if (feeType.getIsActive() != null) existing.setIsActive(feeType.getIsActive());
        
        existing.setUpdatedAt(LocalDateTime.now());
        logAudit("FEE_TYPE", id, "UPDATE", actorId);
        return feeTypeRepository.save(existing);
    }
    
    @Override
    public void deleteFeeType(Long id, Long actorId, String role) {
        checkAdmin(role);
        FeeType feeType = getFeeTypeById(id, actorId, role);
        feeType.setIsActive(false);
        feeType.setUpdatedAt(LocalDateTime.now());
        feeTypeRepository.save(feeType);
        logAudit("FEE_TYPE", id, "DELETE", actorId);
    }
    
    // =============== FEE STRUCTURES IMPLEMENTATION ===============
    @Override
    public FeeStructure createFeeStructure(FeeStructure feeStructure, Long actorId, String role) {
        checkAdmin(role);
        feeStructure.setId(null);
        feeStructure.setCreatedAt(LocalDateTime.now());
        feeStructure.setUpdatedAt(null);
        FeeStructure saved = feeStructureRepository.save(feeStructure);
        logAudit("FEE_STRUCTURE", saved.getId(), "CREATE", actorId);
        return saved;
    }
    
    @Override
    public List<FeeStructure> getFeeStructures(Long actorId, String role, Long courseId, String academicYear) {
        if ("ADMIN".equalsIgnoreCase(role)) {
            return feeStructureRepository.findAll();
        }
        
        if ("FACULTY".equalsIgnoreCase(role)) {
            if (courseId == null && academicYear == null) {
                throw new RuntimeException("Faculty must specify courseId or academicYear");
            }
            if (courseId != null && academicYear != null) {
                return feeStructureRepository.findByCourseIdAndAcademicYear(courseId, academicYear);
            } else if (courseId != null) {
                return feeStructureRepository.findByCourseId(courseId);
            } else {
                return feeStructureRepository.findByAcademicYear(academicYear);
            }
        }
        
        throw new RuntimeException("Access Denied for Student/Parent to fee structures");
    }
    
    @Override
    public FeeStructure getFeeStructureById(Long id, Long actorId, String role) {
        return feeStructureRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("FeeStructure not found with id: " + id));
    }
    
    @Override
    public FeeStructure updateFeeStructure(Long id, FeeStructure feeStructure, Long actorId, String role) {
        checkAdmin(role);
        FeeStructure existing = getFeeStructureById(id, actorId, role);
        
        if (feeStructure.getFeeType() != null) existing.setFeeType(feeStructure.getFeeType());
        if (feeStructure.getAcademicYear() != null) existing.setAcademicYear(feeStructure.getAcademicYear());
        if (feeStructure.getCourseId() != null) existing.setCourseId(feeStructure.getCourseId());
        if (feeStructure.getTotalAmount() != null) existing.setTotalAmount(feeStructure.getTotalAmount());
        if (feeStructure.getIsActive() != null) existing.setIsActive(feeStructure.getIsActive());
        
        existing.setUpdatedAt(LocalDateTime.now());
        logAudit("FEE_STRUCTURE", id, "UPDATE", actorId);
        return feeStructureRepository.save(existing);
    }
    
    @Override
    public void deleteFeeStructure(Long id, Long actorId, String role) {
        checkAdmin(role);
        FeeStructure structure = getFeeStructureById(id, actorId, role);
        structure.setIsActive(false);
        structure.setUpdatedAt(LocalDateTime.now());
        feeStructureRepository.save(structure);
        logAudit("FEE_STRUCTURE", id, "DELETE", actorId);
    }
    
    // =============== STUDENT FEE ALLOCATIONS ===============
    @Override
    public StudentFeeAllocation allocateFee(StudentFeeAllocation allocation, Long actorId, String role) {
        checkAdmin(role);
        allocation.setId(null);
        allocation.setAllocationDate(LocalDate.now());
        allocation.setCreatedAt(LocalDateTime.now());
        allocation.setCreatedBy(actorId);
        
        if (allocation.getOriginalAmount() == null) {
            allocation.setOriginalAmount(allocation.getFeeStructure().getTotalAmount());
        }
        if (allocation.getDiscountApplied() == null) {
            allocation.setDiscountApplied(BigDecimal.ZERO);
        }
        if (allocation.getPayableAmount() == null) {
            allocation.setPayableAmount(allocation.getOriginalAmount()
                .subtract(allocation.getDiscountApplied()));
        }
        if (allocation.getRemainingAmount() == null) {
            allocation.setRemainingAmount(allocation.getPayableAmount());
        }
        
        StudentFeeAllocation saved = allocationRepository.save(allocation);
        logAudit("ALLOCATION", saved.getId(), "CREATE", actorId);
        return saved;
    }
    
    @Override
    public List<StudentFeeAllocation> getAllocations(Long actorId, String role, Long studentId) {
        if ("ADMIN".equalsIgnoreCase(role)) {
            return allocationRepository.findAll();
        }
        
        if ("STUDENT".equalsIgnoreCase(role)) {
            return allocationRepository.findByUserId(actorId);
        }
        
        if ("PARENT".equalsIgnoreCase(role)) {
            if (studentId == null) {
                throw new RuntimeException("Parent must specify student userId");
            }
            return allocationRepository.findByUserId(studentId);
        }
        
        throw new RuntimeException("Access Denied");
    }
    
    @Override
    public StudentFeeAllocation getAllocationById(Long id, Long actorId, String role) {
        StudentFeeAllocation allocation = allocationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Allocation not found with id: " + id));
        
        if ("ADMIN".equalsIgnoreCase(role)) return allocation;
        if ("STUDENT".equalsIgnoreCase(role) && allocation.getUserId().equals(actorId)) return allocation;
        if ("PARENT".equalsIgnoreCase(role)) {
            return allocation;
        }
        
        throw new RuntimeException("Access Denied");
    }
    
    @Override
    public StudentFeeAllocation updateAllocation(Long id, StudentFeeAllocation allocation, Long actorId, String role) {
        checkAdmin(role);
        StudentFeeAllocation existing = getAllocationById(id, actorId, role);
        
        if (allocation.getUserId() != null) existing.setUserId(allocation.getUserId());
        if (allocation.getFeeStructure() != null) existing.setFeeStructure(allocation.getFeeStructure());
        if (allocation.getStatus() != null) existing.setStatus(allocation.getStatus());
        if (allocation.getDueDate() != null) existing.setDueDate(allocation.getDueDate());
        if (allocation.getAdvancePaid() != null) existing.setAdvancePaid(allocation.getAdvancePaid());
        if (allocation.getTotalPaid() != null) existing.setTotalPaid(allocation.getTotalPaid());
        if (allocation.getRemainingAmount() != null) existing.setRemainingAmount(allocation.getRemainingAmount());
        
        existing.setUpdatedAt(LocalDateTime.now());
        existing.setUpdatedBy(actorId);
        logAudit("ALLOCATION", id, "UPDATE", actorId);
        return allocationRepository.save(existing);
    }
    
    // =============== PAYMENT ALTERNATIVES IMPLEMENTATION ===============
    @Override
    public PaymentAlternative createPaymentAlternative(PaymentAlternative alternative, Long actorId, String role) {
        checkAdmin(role);
        alternative.setId(null);
        alternative.setCreatedBy(actorId);
        alternative.setCreatedAt(LocalDateTime.now());
        alternative.setUpdatedAt(null);
        PaymentAlternative saved = paymentAlternativeRepository.save(alternative);
        logAudit("PAYMENT_ALTERNATIVE", saved.getId(), "CREATE", actorId);
        return saved;
    }
    
    @Override
    public List<PaymentAlternative> getAllPaymentAlternatives(Long actorId, String role) {
        checkAdmin(role);
        return paymentAlternativeRepository.findAll();
    }
    
    @Override
    public List<PaymentAlternative> getActivePaymentAlternatives() {
        return paymentAlternativeRepository.findByIsActiveTrue();
    }
    
    @Override
    public PaymentAlternative getPaymentAlternativeById(Long id, Long actorId, String role) {
        return paymentAlternativeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("PaymentAlternative not found with id: " + id));
    }
    
    @Override
    public PaymentAlternative updatePaymentAlternative(Long id, PaymentAlternative alternative, Long actorId, String role) {
        checkAdmin(role);
        PaymentAlternative existing = getPaymentAlternativeById(id, actorId, role);
        
        if (alternative.getName() != null) existing.setName(alternative.getName());
        if (alternative.getInstallmentCount() != null) existing.setInstallmentCount(alternative.getInstallmentCount());
        if (alternative.getDescription() != null) existing.setDescription(alternative.getDescription());
        if (alternative.getIsActive() != null) existing.setIsActive(alternative.getIsActive());
        
        existing.setUpdatedAt(LocalDateTime.now());
        logAudit("PAYMENT_ALTERNATIVE", id, "UPDATE", actorId);
        return paymentAlternativeRepository.save(existing);
    }
    
    @Override
    public void deletePaymentAlternative(Long id, Long actorId, String role) {
        checkAdmin(role);
        PaymentAlternative alternative = getPaymentAlternativeById(id, actorId, role);
        alternative.setIsActive(false);
        alternative.setUpdatedAt(LocalDateTime.now());
        paymentAlternativeRepository.save(alternative);
        logAudit("PAYMENT_ALTERNATIVE", id, "DELETE", actorId);
    }
    
    // =============== NEW INSTALLMENT LOGIC ===============
    @Override
    public StudentFeeAllocation setAdvancePayment(Long allocationId, BigDecimal advancePayment, Long actorId, String role) {
        checkAdmin(role);
        
        StudentFeeAllocation allocation = getAllocationById(allocationId, actorId, role);
        
        if (advancePayment.compareTo(allocation.getPayableAmount()) >= 0) {
            throw new RuntimeException("Advance payment must be less than payable amount: " + allocation.getPayableAmount());
        }
        
        allocation.setAdvancePaid(advancePayment);
        allocation.setTotalPaid(advancePayment);
        allocation.setRemainingAmount(allocation.getPayableAmount().subtract(advancePayment));
        allocation.setUpdatedAt(LocalDateTime.now());
        allocation.setUpdatedBy(actorId);
        
        logAudit("ADVANCE_PAYMENT", allocationId, "SET", actorId);
        return allocationRepository.save(allocation);
    }
    
    @Override
    public StudentFeeAllocation selectPaymentPlan(Long allocationId, Long alternativeId, List<BigDecimal> customAmounts, Long actorId, String role) {
        checkStudent(role);
        
        StudentFeeAllocation allocation = getAllocationById(allocationId, actorId, role);
        
        if (!allocation.getUserId().equals(actorId)) {
            throw new RuntimeException("Access Denied: Student can only select plans for their own allocations");
        }
        
        PaymentAlternative alternative = paymentAlternativeRepository.findById(alternativeId)
            .orElseThrow(() -> new RuntimeException("Payment alternative not found"));
        
        if (!alternative.getIsActive()) {
            throw new RuntimeException("Payment alternative is not active");
        }
        
        if (customAmounts.size() != alternative.getInstallmentCount()) {
            throw new RuntimeException("Number of custom amounts must match installment count: " + alternative.getInstallmentCount());
        }
        
        BigDecimal sumCustom = customAmounts.stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal remainingAfterAdvance = allocation.getRemainingAmount();
        
        if (!sumCustom.equals(remainingAfterAdvance)) {
            throw new RuntimeException("Sum of installment amounts must equal remaining amount: " + remainingAfterAdvance);
        }
        
        for (BigDecimal amount : customAmounts) {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Each installment amount must be greater than 0");
            }
        }
        
        allocation.setSelectedAlternativeId(alternativeId);
        allocation.setInstallmentCount(alternative.getInstallmentCount());
        
        createCustomInstallments(allocation, customAmounts);
        
        allocation.setUpdatedAt(LocalDateTime.now());
        allocation.setUpdatedBy(actorId);
        
        logAudit("PAYMENT_PLAN_SELECTION", allocationId, "SELECT", actorId);
        return allocationRepository.save(allocation);
    }
    
    private void createCustomInstallments(StudentFeeAllocation allocation, List<BigDecimal> customAmounts) {
        LocalDate startDate = allocation.getDueDate() != null ? 
            allocation.getDueDate() : LocalDate.now().plusMonths(1);
        
        List<PaymentInstallment> existing = installmentRepository.findByStudentFeeAllocationId(allocation.getId());
        if (!existing.isEmpty()) {
            installmentRepository.deleteAll(existing);
        }
        
        for (int i = 0; i < customAmounts.size(); i++) {
            PaymentInstallment installment = new PaymentInstallment();
            installment.setStudentFeeAllocation(allocation);
            installment.setInstallmentNumber(i + 1);
            installment.setAmount(customAmounts.get(i));
            installment.setDueDate(startDate.plusMonths(i));
            installment.setStatus("PENDING");
            installment.setCreatedAt(LocalDateTime.now());
            installmentRepository.save(installment);
        }
    }
    
    @Override
    public void recalculateInstallments(Long allocationId, List<BigDecimal> newAmounts, Long actorId, String role) {
        checkAdmin(role);
        
        StudentFeeAllocation allocation = getAllocationById(allocationId, actorId, role);
        
        if (allocation.getSelectedAlternativeId() == null) {
            throw new RuntimeException("No payment plan selected for this allocation");
        }
        
        PaymentAlternative alternative = paymentAlternativeRepository
            .findById(allocation.getSelectedAlternativeId())
            .orElseThrow(() -> new RuntimeException("Payment alternative not found"));
        
        if (newAmounts.size() != alternative.getInstallmentCount()) {
            throw new RuntimeException("Number of amounts must match installment count: " + alternative.getInstallmentCount());
        }
        
        BigDecimal sumNew = newAmounts.stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal remainingAfterAdvance = allocation.getRemainingAmount();
        
        if (!sumNew.equals(remainingAfterAdvance)) {
            throw new RuntimeException("Sum must equal remaining amount: " + remainingAfterAdvance);
        }
        
        for (BigDecimal amount : newAmounts) {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Each installment amount must be greater than 0");
            }
        }
        
        updateInstallmentAmounts(allocation, newAmounts);
        
        allocation.setUpdatedAt(LocalDateTime.now());
        allocation.setUpdatedBy(actorId);
        allocationRepository.save(allocation);
        
        logAudit("RECALCULATE_INSTALLMENTS", allocationId, "UPDATE", actorId);
    }
    
    private void updateInstallmentAmounts(StudentFeeAllocation allocation, List<BigDecimal> newAmounts) {
        List<PaymentInstallment> installments = installmentRepository
            .findByStudentFeeAllocationId(allocation.getId());
        
        installments.sort(Comparator.comparing(PaymentInstallment::getInstallmentNumber));
        
        for (int i = 0; i < installments.size() && i < newAmounts.size(); i++) {
            PaymentInstallment installment = installments.get(i);
            installment.setAmount(newAmounts.get(i));
            installment.setUpdatedAt(LocalDateTime.now());
            installmentRepository.save(installment);
        }
    }
    
    // =============== PAYMENT INSTALLMENTS ===============
    @Override
    public PaymentInstallment createPaymentInstallment(PaymentInstallment installment, Long actorId, String role) {
        checkAdmin(role);
        installment.setId(null);
        installment.setCreatedAt(LocalDateTime.now());
        return installmentRepository.save(installment);
    }
    
    @Override
    public List<PaymentInstallment> getInstallmentsByAllocationId(Long allocationId, Long actorId, String role) {
        getAllocationById(allocationId, actorId, role);
        return installmentRepository.findByStudentFeeAllocationId(allocationId);
    }
    
    @Override
    public PaymentInstallment updateInstallment(Long id, PaymentInstallment installment, Long actorId, String role) {
        checkAdmin(role);
        PaymentInstallment existing = installmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Installment not found"));
        
        if (installment.getAmount() != null) existing.setAmount(installment.getAmount());
        if (installment.getDueDate() != null) existing.setDueDate(installment.getDueDate());
        if (installment.getStatus() != null) existing.setStatus(installment.getStatus());
        
        existing.setUpdatedAt(LocalDateTime.now());
        return installmentRepository.save(existing);
    }
    
    // =============== STUDENT FEE PAYMENTS ===============
    @Override
    public StudentFeePayment processPayment(StudentFeePayment payment, Long actorId, String role) {
        if (!"ADMIN".equalsIgnoreCase(role) && !"STUDENT".equalsIgnoreCase(role)) {
            throw new RuntimeException("Only Admin or Student can make payments");
        }
        
        payment.setId(null);
        payment.setPaymentDate(LocalDate.now());
        payment.setCreatedAt(LocalDateTime.now());
        
        if ("STUDENT".equalsIgnoreCase(role)) {
            payment.setCollectedBy(actorId);
        }
        
        StudentFeePayment saved = paymentRepository.save(payment);
        
        updateAllocationAfterPayment(payment.getStudentFeeAllocation(), payment.getPaidAmount());
        
        generateReceipt(saved);
        
        logAudit("PAYMENT", saved.getId(), "CREATE", actorId);
        return saved;
    }
    
    private void updateAllocationAfterPayment(StudentFeeAllocation allocation, BigDecimal paidAmount) {
        BigDecimal newTotalPaid = allocation.getTotalPaid().add(paidAmount);
        BigDecimal newRemaining = allocation.getRemainingAmount().subtract(paidAmount);
        
        allocation.setTotalPaid(newTotalPaid);
        allocation.setRemainingAmount(newRemaining);
        
        if (newRemaining.compareTo(BigDecimal.ZERO) <= 0) {
            allocation.setStatus("PAID");
        } else if (newTotalPaid.compareTo(BigDecimal.ZERO) > 0) {
            allocation.setStatus("PARTIAL");
        }
        
        allocation.setUpdatedAt(LocalDateTime.now());
        allocationRepository.save(allocation);
    }
    
    private void generateReceipt(StudentFeePayment payment) {
        FeeReceipt receipt = new FeeReceipt();
        receipt.setStudentFeePayment(payment);
        receipt.setReceiptNumber("REC-" + System.currentTimeMillis() + "-" + payment.getId());
        receipt.setReceiptDate(LocalDate.now());
        receipt.setGeneratedBy(0L);
        receipt.setCreatedAt(LocalDateTime.now());
        receiptRepository.save(receipt);
        
        payment.setReceiptGenerated(true);
        paymentRepository.save(payment);
    }
    
    @Override
    public List<StudentFeePayment> getPayments(Long actorId, String role) {
        if ("ADMIN".equalsIgnoreCase(role)) {
            return paymentRepository.findAll();
        }
        
        if ("STUDENT".equalsIgnoreCase(role) || "PARENT".equalsIgnoreCase(role)) {
            List<StudentFeeAllocation> allocations = allocationRepository.findByUserId(actorId);
            List<StudentFeePayment> payments = new ArrayList<>();
            for (StudentFeeAllocation alloc : allocations) {
                payments.addAll(paymentRepository.findByStudentFeeAllocationId(alloc.getId()));
            }
            return payments;
        }
        
        throw new RuntimeException("Access Denied");
    }
    
    @Override
    public StudentFeePayment getPaymentById(Long id, Long actorId, String role) {
        StudentFeePayment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        if ("ADMIN".equalsIgnoreCase(role)) return payment;
        
        Long paymentUserId = payment.getStudentFeeAllocation().getUserId();
        if (paymentUserId.equals(actorId)) return payment;
        
        throw new RuntimeException("Access Denied");
    }
    
    // =============== FEE DISCOUNTS ===============
    @Override
    public FeeDiscount applyDiscount(FeeDiscount discount, Long actorId, String role) {
        checkAdmin(role);
        discount.setId(null);
        discount.setCreatedAt(LocalDateTime.now());
        discount.setApprovedBy(actorId);
        discount.setApprovedDate(LocalDate.now());
        return discountRepository.save(discount);
    }
    
    @Override
    public List<FeeDiscount> getDiscounts(Long actorId, String role) {
        if ("ADMIN".equalsIgnoreCase(role)) return discountRepository.findAll();
        return discountRepository.findByUserId(actorId);
    }
    
    @Override
    public FeeDiscount getDiscountById(Long id, Long actorId, String role) {
        return discountRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Discount not found"));
    }
    
    @Override
    public FeeDiscount updateDiscount(Long id, FeeDiscount discount, Long actorId, String role) {
        checkAdmin(role);
        FeeDiscount existing = getDiscountById(id, actorId, role);
        
        if (discount.getDiscountType() != null) existing.setDiscountType(discount.getDiscountType());
        if (discount.getDiscountValue() != null) existing.setDiscountValue(discount.getDiscountValue());
        if (discount.getReason() != null) existing.setReason(discount.getReason());
        
        existing.setUpdatedAt(LocalDateTime.now());
        return discountRepository.save(existing);
    }
    
    @Override
    public void deleteDiscount(Long id, Long actorId, String role) {
        checkAdmin(role);
        discountRepository.deleteById(id);
    }
    
    // =============== FEE REFUNDS ===============
    @Override
    public FeeRefund processRefund(FeeRefund refund, Long actorId, String role) {
        checkAdmin(role);
        refund.setId(null);
        refund.setCreatedAt(LocalDateTime.now());
        return refundRepository.save(refund);
    }
    
    @Override
    public List<FeeRefund> getRefunds(Long actorId, String role) {
        checkAdmin(role);
        return refundRepository.findAll();
    }
    
    @Override
    public FeeRefund getRefundById(Long id, Long actorId, String role) {
        checkAdmin(role);
        return refundRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Refund not found"));
    }
    
    // =============== FEE RECEIPTS ===============
    @Override
    public List<FeeReceipt> getReceipts(Long actorId, String role) {
        if ("ADMIN".equalsIgnoreCase(role)) {
            return receiptRepository.findAll();
        }
        
        try {
            // Check if findByUserId method exists using reflection
            receiptRepository.getClass().getMethod("findByUserId", Long.class);
            return receiptRepository.findByUserId(actorId);
        } catch (NoSuchMethodException e) {
            // Fallback method
            List<StudentFeePayment> payments = getPayments(actorId, role);
            List<FeeReceipt> receipts = new ArrayList<>();
            for (StudentFeePayment payment : payments) {
                Optional<FeeReceipt> receiptOpt = receiptRepository.findByStudentFeePaymentId(payment.getId());
                receiptOpt.ifPresent(receipts::add);
            }
            return receipts;
        }
    }
    
    @Override
    public FeeReceipt getReceiptById(Long id, Long actorId, String role) {
        FeeReceipt receipt = receiptRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Receipt not found"));
        
        Long receiptUserId = receipt.getStudentFeePayment().getStudentFeeAllocation().getUserId();
        if ("ADMIN".equalsIgnoreCase(role) || receiptUserId.equals(actorId)) {
            return receipt;
        }
        throw new RuntimeException("Access Denied");
    }
    
    // =============== AUDIT LOGS ===============
    @Override
    public List<AuditLog> getAuditLogs(Long actorId, String role) {
        checkAdmin(role);
        return auditLogRepository.findAll();
    }
    
    @Override
    public AuditLog getAuditLogById(Long id, Long actorId, String role) {
        checkAdmin(role);
        return auditLogRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Audit log not found"));
    }
    
    // =============== OTHER ENTITIES ===============
    @Override
    public CurrencyRate createCurrencyRate(CurrencyRate rate, Long actorId, String role) {
        checkAdmin(role);
        rate.setId(null);
        rate.setCreatedAt(LocalDateTime.now());
        return currencyRateRepository.save(rate);
    }
    
    @Override
    public List<CurrencyRate> getCurrencyRates(Long actorId, String role) {
        return currencyRateRepository.findAll();
    }
    
    @Override
    public NotificationLog createNotificationLog(NotificationLog log, Long actorId, String role) {
        log.setId(null);
        log.setSentAt(LocalDateTime.now());
        return notificationLogRepository.save(log);
    }
    
    @Override
    public AttendancePenalty createAttendancePenalty(AttendancePenalty penalty, Long actorId, String role) {
        checkAdmin(role);
        penalty.setId(null);
        penalty.setCreatedAt(LocalDateTime.now());
        return attendancePenaltyRepository.save(penalty);
    }
    
    @Override
    public CertificateBlock createCertificateBlock(CertificateBlock block, Long actorId, String role) {
        checkAdmin(role);
        block.setId(null);
        block.setBlockedAt(LocalDateTime.now());
        return certificateBlockRepository.save(block);
    }
    
    @Override
    public AutoDebitSetting createAutoDebitSetting(AutoDebitSetting setting, Long actorId, String role) {
        if (!"ADMIN".equalsIgnoreCase(role) && !"STUDENT".equalsIgnoreCase(role)) {
            throw new RuntimeException("Only Admin or Student can set up auto-debit");
        }
        setting.setId(null);
        setting.setCreatedAt(LocalDateTime.now());
        return autoDebitSettingRepository.save(setting);
    }
    
    @Override
    public FeeReport generateFeeReport(FeeReport report, Long actorId, String role) {
        checkAdmin(role);
        report.setId(null);
        report.setCreatedAt(LocalDateTime.now());
        return feeReportRepository.save(report);
    }
}