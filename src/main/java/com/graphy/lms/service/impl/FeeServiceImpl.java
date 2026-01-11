package com.graphy.lms.service.impl;

import com.graphy.lms.entity.*;
import com.graphy.lms.repository.*;
import com.graphy.lms.service.FeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class FeeServiceImpl implements FeeService {
    
    private final FeeTypeRepository feeTypeRepository;
    private final FeeStructureRepository feeStructureRepository;
    private final StudentFeeAllocationRepository studentFeeAllocationRepository;
    private final FeeInstallmentPlanRepository feeInstallmentPlanRepository;
    private final StudentFeePaymentRepository studentFeePaymentRepository;
    private final FeeDiscountRepository feeDiscountRepository;
    private final FeeRefundRepository feeRefundRepository;
    private final FeeReceiptRepository feeReceiptRepository;
    private final AuditLogRepository auditLogRepository;
    private final LateFeeRuleRepository lateFeeRuleRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // ========================================
    // AUDIT HELPER METHOD
    // ========================================
    private void createAuditLog(String module, String entityType, Long entityId, String action, 
                                Object oldValue, Object newValue, Long performedBy) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setModule(module);
            auditLog.setEntityType(entityType);
            auditLog.setEntityId(entityId);
            auditLog.setAction(action);
            
            if (oldValue != null) {
                auditLog.setOldValue(objectMapper.writeValueAsString(oldValue));
            }
            if (newValue != null) {
                auditLog.setNewValue(objectMapper.writeValueAsString(newValue));
            }
            
            auditLog.setPerformedBy(performedBy);
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            // Log the error but don't fail the transaction
            System.err.println("Failed to create audit log: " + e.getMessage());
        }
    }
    
    // ========================================
    // FEE TYPE OPERATIONS
    // ========================================
    @Override
    public FeeType createFeeType(FeeType feeType, Long performedBy) {
        FeeType saved = feeTypeRepository.save(feeType);
        createAuditLog("FEE_MANAGEMENT", "FeeType", saved.getId(), "CREATE", null, saved, performedBy);
        return saved;
    }
    
    @Override
    public List<FeeType> getAllFeeTypes() {
        return feeTypeRepository.findAll();
    }
    
    @Override
    public List<FeeType> getActiveFeeTypes() {
        return feeTypeRepository.findByIsActiveTrue();
    }
    
    @Override
    public FeeType getFeeTypeById(Long id) {
        return feeTypeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("FeeType not found with id: " + id));
    }
    
    @Override
    public FeeType updateFeeType(Long id, FeeType feeType, Long performedBy) {
        FeeType existing = getFeeTypeById(id);
        
        // Update only non-null fields
        if (feeType.getName() != null) existing.setName(feeType.getName());
        if (feeType.getDescription() != null) existing.setDescription(feeType.getDescription());
        if (feeType.getIsActive() != null) existing.setIsActive(feeType.getIsActive());
        
        FeeType updated = feeTypeRepository.save(existing);
        createAuditLog("FEE_MANAGEMENT", "FeeType", id, "UPDATE", existing, updated, performedBy);
        return updated;
    }
    
    @Override
    public void deleteFeeType(Long id, Long performedBy) {
        FeeType existing = getFeeTypeById(id);
        createAuditLog("FEE_MANAGEMENT", "FeeType", id, "DELETE", existing, null, performedBy);
        feeTypeRepository.deleteById(id);
    }
    
    // ========================================
    // FEE STRUCTURE OPERATIONS
    // ========================================
    @Override
    public FeeStructure createFeeStructure(FeeStructure feeStructure, Long performedBy) {
        FeeStructure saved = feeStructureRepository.save(feeStructure);
        createAuditLog("FEE_MANAGEMENT", "FeeStructure", saved.getId(), "CREATE", null, saved, performedBy);
        return saved;
    }
    
    @Override
    public List<FeeStructure> getAllFeeStructures() {
        return feeStructureRepository.findAll();
    }
    
    @Override
    public FeeStructure getFeeStructureById(Long id) {
        return feeStructureRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("FeeStructure not found with id: " + id));
    }
    
    @Override
    public List<FeeStructure> getFeeStructuresByCourseAndYear(Long courseId, String academicYear) {
        return feeStructureRepository.findByCourseIdAndAcademicYear(courseId, academicYear);
    }
    
    @Override
    public FeeStructure updateFeeStructure(Long id, FeeStructure feeStructure, Long performedBy) {
        FeeStructure existing = getFeeStructureById(id);
        
        if (feeStructure.getFeeTypeId() != null) existing.setFeeTypeId(feeStructure.getFeeTypeId());
        if (feeStructure.getAcademicYear() != null) existing.setAcademicYear(feeStructure.getAcademicYear());
        if (feeStructure.getCourseId() != null) existing.setCourseId(feeStructure.getCourseId());
        if (feeStructure.getBatchId() != null) existing.setBatchId(feeStructure.getBatchId());
        if (feeStructure.getStudentCategory() != null) existing.setStudentCategory(feeStructure.getStudentCategory());
        if (feeStructure.getTotalAmount() != null) existing.setTotalAmount(feeStructure.getTotalAmount());
        if (feeStructure.getCurrency() != null) existing.setCurrency(feeStructure.getCurrency());
        if (feeStructure.getPaymentSchedule() != null) existing.setPaymentSchedule(feeStructure.getPaymentSchedule());
        if (feeStructure.getIsActive() != null) existing.setIsActive(feeStructure.getIsActive());
        
        FeeStructure updated = feeStructureRepository.save(existing);
        createAuditLog("FEE_MANAGEMENT", "FeeStructure", id, "UPDATE", existing, updated, performedBy);
        return updated;
    }
    
    @Override
    public void deleteFeeStructure(Long id, Long performedBy) {
        FeeStructure existing = getFeeStructureById(id);
        createAuditLog("FEE_MANAGEMENT", "FeeStructure", id, "DELETE", existing, null, performedBy);
        feeStructureRepository.deleteById(id);
    }
    
    // ========================================
    // STUDENT FEE ALLOCATION OPERATIONS
    // ========================================
    @Override
    public StudentFeeAllocation createStudentFeeAllocation(StudentFeeAllocation allocation, Long performedBy) {
        StudentFeeAllocation saved = studentFeeAllocationRepository.save(allocation);
        createAuditLog("FEE_MANAGEMENT", "StudentFeeAllocation", saved.getId(), "CREATE", null, saved, performedBy);
        return saved;
    }
    
    @Override
    public List<StudentFeeAllocation> getAllStudentFeeAllocations() {
        return studentFeeAllocationRepository.findAll();
    }
    
    @Override
    public StudentFeeAllocation getStudentFeeAllocationById(Long id) {
        return studentFeeAllocationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("StudentFeeAllocation not found with id: " + id));
    }
    
    @Override
    public List<StudentFeeAllocation> getStudentFeeAllocationsByUserId(Long userId) {
        return studentFeeAllocationRepository.findByUserId(userId);
    }
    
    @Override
    public StudentFeeAllocation updateStudentFeeAllocation(Long id, StudentFeeAllocation allocation, Long performedBy) {
        StudentFeeAllocation existing = getStudentFeeAllocationById(id);
        
        if (allocation.getUserId() != null) existing.setUserId(allocation.getUserId());
        if (allocation.getFeeStructureId() != null) existing.setFeeStructureId(allocation.getFeeStructureId());
        if (allocation.getOriginalAmount() != null) existing.setOriginalAmount(allocation.getOriginalAmount());
        if (allocation.getDiscountAmount() != null) existing.setDiscountAmount(allocation.getDiscountAmount());
        if (allocation.getFinalAmount() != null) existing.setFinalAmount(allocation.getFinalAmount());
        if (allocation.getInitialPayment() != null) existing.setInitialPayment(allocation.getInitialPayment());
        if (allocation.getRemainingAmount() != null) existing.setRemainingAmount(allocation.getRemainingAmount());
        if (allocation.getNumberOfInstallments() != null) existing.setNumberOfInstallments(allocation.getNumberOfInstallments());
        if (allocation.getDueDate() != null) existing.setDueDate(allocation.getDueDate());
        if (allocation.getStatus() != null) existing.setStatus(allocation.getStatus());
        
        StudentFeeAllocation updated = studentFeeAllocationRepository.save(existing);
        createAuditLog("FEE_MANAGEMENT", "StudentFeeAllocation", id, "UPDATE", existing, updated, performedBy);
        return updated;
    }
    
    @Override
    public void deleteStudentFeeAllocation(Long id, Long performedBy) {
        StudentFeeAllocation existing = getStudentFeeAllocationById(id);
        createAuditLog("FEE_MANAGEMENT", "StudentFeeAllocation", id, "DELETE", existing, null, performedBy);
        studentFeeAllocationRepository.deleteById(id);
    }
    
    // ========================================
    // FEE INSTALLMENT PLAN OPERATIONS
    // ========================================
    @Override
    public FeeInstallmentPlan createFeeInstallmentPlan(FeeInstallmentPlan installmentPlan, Long performedBy) {
        FeeInstallmentPlan saved = feeInstallmentPlanRepository.save(installmentPlan);
        createAuditLog("FEE_MANAGEMENT", "FeeInstallmentPlan", saved.getId(), "CREATE", null, saved, performedBy);
        return saved;
    }
    
    @Override
    public List<FeeInstallmentPlan> getAllFeeInstallmentPlans() {
        return feeInstallmentPlanRepository.findAll();
    }
    
    @Override
    public FeeInstallmentPlan getFeeInstallmentPlanById(Long id) {
        return feeInstallmentPlanRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("FeeInstallmentPlan not found with id: " + id));
    }
    
    @Override
    public List<FeeInstallmentPlan> getInstallmentPlansByAllocationId(Long allocationId) {
        return feeInstallmentPlanRepository.findByStudentFeeAllocationIdOrderByInstallmentNumberAsc(allocationId);
    }
    
    @Override
    public FeeInstallmentPlan updateFeeInstallmentPlan(Long id, FeeInstallmentPlan installmentPlan, Long performedBy) {
        FeeInstallmentPlan existing = getFeeInstallmentPlanById(id);
        
        if (installmentPlan.getStudentFeeAllocationId() != null) existing.setStudentFeeAllocationId(installmentPlan.getStudentFeeAllocationId());
        if (installmentPlan.getInstallmentNumber() != null) existing.setInstallmentNumber(installmentPlan.getInstallmentNumber());
        if (installmentPlan.getDueDate() != null) existing.setDueDate(installmentPlan.getDueDate());
        if (installmentPlan.getDueAmount() != null) existing.setDueAmount(installmentPlan.getDueAmount());
        if (installmentPlan.getPaidAmount() != null) existing.setPaidAmount(installmentPlan.getPaidAmount());
        if (installmentPlan.getStatus() != null) existing.setStatus(installmentPlan.getStatus());
        if (installmentPlan.getLateFee() != null) existing.setLateFee(installmentPlan.getLateFee());
        
        FeeInstallmentPlan updated = feeInstallmentPlanRepository.save(existing);
        createAuditLog("FEE_MANAGEMENT", "FeeInstallmentPlan", id, "UPDATE", existing, updated, performedBy);
        return updated;
    }
    
    @Override
    public void deleteFeeInstallmentPlan(Long id, Long performedBy) {
        FeeInstallmentPlan existing = getFeeInstallmentPlanById(id);
        createAuditLog("FEE_MANAGEMENT", "FeeInstallmentPlan", id, "DELETE", existing, null, performedBy);
        feeInstallmentPlanRepository.deleteById(id);
    }
 // Continue from Part 1...

    // ========================================
    // STUDENT FEE PAYMENT OPERATIONS
    // ========================================
    @Override
    public StudentFeePayment createStudentFeePayment(StudentFeePayment payment, Long performedBy) {
        // Calculate total paid
        payment.setTotalPaid(payment.getPaidAmount().add(
            payment.getLateFeePaid() != null ? payment.getLateFeePaid() : BigDecimal.ZERO
        ));
        
        StudentFeePayment saved = studentFeePaymentRepository.save(payment);
        
        // Update allocation status
        updateAllocationStatus(saved.getStudentFeeAllocationId());
        
        // Update installment if linked
        if (saved.getInstallmentPlanId() != null) {
            updateInstallmentAfterPayment(saved.getInstallmentPlanId(), saved.getPaidAmount());
        }
        
        // Auto-generate receipt
        generateReceipt(saved.getId());
        
        createAuditLog("FEE_MANAGEMENT", "StudentFeePayment", saved.getId(), "CREATE", null, saved, performedBy);
        return saved;
    }
    
    @Override
    public List<StudentFeePayment> getAllStudentFeePayments() {
        return studentFeePaymentRepository.findAll();
    }
    
    @Override
    public StudentFeePayment getStudentFeePaymentById(Long id) {
        return studentFeePaymentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("StudentFeePayment not found with id: " + id));
    }
    
    @Override
    public List<StudentFeePayment> getPaymentsByAllocationId(Long allocationId) {
        return studentFeePaymentRepository.findByStudentFeeAllocationId(allocationId);
    }
    
    @Override
    public StudentFeePayment updateStudentFeePayment(Long id, StudentFeePayment payment, Long performedBy) {
        StudentFeePayment existing = getStudentFeePaymentById(id);
        
        if (payment.getStudentFeeAllocationId() != null) existing.setStudentFeeAllocationId(payment.getStudentFeeAllocationId());
        if (payment.getInstallmentPlanId() != null) existing.setInstallmentPlanId(payment.getInstallmentPlanId());
        if (payment.getPaidAmount() != null) existing.setPaidAmount(payment.getPaidAmount());
        if (payment.getLateFeePaid() != null) existing.setLateFeePaid(payment.getLateFeePaid());
        if (payment.getPaymentDate() != null) existing.setPaymentDate(payment.getPaymentDate());
        if (payment.getPaymentMode() != null) existing.setPaymentMode(payment.getPaymentMode());
        if (payment.getTransactionReference() != null) existing.setTransactionReference(payment.getTransactionReference());
        if (payment.getPaymentGateway() != null) existing.setPaymentGateway(payment.getPaymentGateway());
        if (payment.getPaymentStatus() != null) existing.setPaymentStatus(payment.getPaymentStatus());
        if (payment.getCollectedBy() != null) existing.setCollectedBy(payment.getCollectedBy());
        if (payment.getRemarks() != null) existing.setRemarks(payment.getRemarks());
        
        // Recalculate total
        existing.setTotalPaid(existing.getPaidAmount().add(
            existing.getLateFeePaid() != null ? existing.getLateFeePaid() : BigDecimal.ZERO
        ));
        
        StudentFeePayment updated = studentFeePaymentRepository.save(existing);
        createAuditLog("FEE_MANAGEMENT", "StudentFeePayment", id, "UPDATE", existing, updated, performedBy);
        return updated;
    }
    
    @Override
    public void deleteStudentFeePayment(Long id, Long performedBy) {
        StudentFeePayment existing = getStudentFeePaymentById(id);
        createAuditLog("FEE_MANAGEMENT", "StudentFeePayment", id, "DELETE", existing, null, performedBy);
        studentFeePaymentRepository.deleteById(id);
    }
    
    // ========================================
    // SPECIAL ALLOCATION METHODS
    // ========================================
    @Override
    public StudentFeeAllocation allocateFeeToStudent(Long userId, Long feeStructureId, LocalDate dueDate,
                                                     BigDecimal initialPayment, Integer numberOfInstallments, Long performedBy) {
        FeeStructure feeStructure = getFeeStructureById(feeStructureId);
        
        // Check for existing discounts
        Optional<FeeDiscount> discountOpt = feeDiscountRepository.findApprovedDiscount(userId, feeStructureId);
        
        BigDecimal originalAmount = feeStructure.getTotalAmount();
        BigDecimal discountAmount = BigDecimal.ZERO;
        
        if (discountOpt.isPresent()) {
            discountAmount = discountOpt.get().getCalculatedAmount();
        }
        
        BigDecimal finalAmount = originalAmount.subtract(discountAmount);
        BigDecimal remainingAmount = finalAmount.subtract(initialPayment);
        
        // Create allocation
        StudentFeeAllocation allocation = new StudentFeeAllocation();
        allocation.setUserId(userId);
        allocation.setFeeStructureId(feeStructureId);
        allocation.setOriginalAmount(originalAmount);
        allocation.setDiscountAmount(discountAmount);
        allocation.setFinalAmount(finalAmount);
        allocation.setInitialPayment(initialPayment);
        allocation.setRemainingAmount(remainingAmount);
        allocation.setNumberOfInstallments(numberOfInstallments);
        allocation.setDueDate(dueDate);
        allocation.setStatus("PENDING");
        
        StudentFeeAllocation saved = studentFeeAllocationRepository.save(allocation);
        
        // Create installment plan
        createInstallmentPlanForAllocation(saved.getId(), remainingAmount, numberOfInstallments, dueDate);
        
        createAuditLog("FEE_MANAGEMENT", "StudentFeeAllocation", saved.getId(), "ALLOCATE", null, saved, performedBy);
        
        return saved;
    }
    
    private void createInstallmentPlanForAllocation(Long allocationId, BigDecimal remainingAmount, 
                                                    Integer numberOfInstallments, LocalDate startDate) {
        BigDecimal installmentAmount = remainingAmount.divide(
            BigDecimal.valueOf(numberOfInstallments), 2, RoundingMode.HALF_UP
        );
        
        for (int i = 1; i <= numberOfInstallments; i++) {
            FeeInstallmentPlan installment = new FeeInstallmentPlan();
            installment.setStudentFeeAllocationId(allocationId);
            installment.setInstallmentNumber(i);
            installment.setDueDate(startDate.plusMonths(i));
            installment.setDueAmount(installmentAmount);
            installment.setPaidAmount(BigDecimal.ZERO);
            installment.setStatus("PENDING");
            installment.setLateFee(BigDecimal.ZERO);
            
            feeInstallmentPlanRepository.save(installment);
        }
    }
    
    @Override
    public void applyDiscountToAllocation(Long allocationId, Long discountId, Long performedBy) {
        StudentFeeAllocation allocation = getStudentFeeAllocationById(allocationId);
        FeeDiscount discount = getFeeDiscountById(discountId);
        
        if (!"APPROVED".equals(discount.getStatus())) {
            throw new RuntimeException("Discount is not approved");
        }
        
        BigDecimal discountAmount = discount.getCalculatedAmount();
        allocation.setDiscountAmount(discountAmount);
        allocation.setFinalAmount(allocation.getOriginalAmount().subtract(discountAmount));
        allocation.setRemainingAmount(allocation.getFinalAmount().subtract(allocation.getInitialPayment()));
        
        studentFeeAllocationRepository.save(allocation);
        
        // Recalculate installments
        List<FeeInstallmentPlan> installments = getInstallmentPlansByAllocationId(allocationId);
        recalculateInstallments(installments, allocation.getRemainingAmount());
        
        createAuditLog("FEE_MANAGEMENT", "StudentFeeAllocation", allocationId, "APPLY_DISCOUNT", null, allocation, performedBy);
    }
    
    @Override
    public void adjustInstallmentPlan(Long allocationId, List<BigDecimal> newInstallmentAmounts, Long performedBy) {
        StudentFeeAllocation allocation = getStudentFeeAllocationById(allocationId);
        
        // Validate total equals remaining amount
        BigDecimal total = newInstallmentAmounts.stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (total.compareTo(allocation.getRemainingAmount()) > 0) {
            throw new RuntimeException("Total installment amount exceeds remaining amount");
        }
        
        // Delete existing installments
        List<FeeInstallmentPlan> existing = getInstallmentPlansByAllocationId(allocationId);
        existing.forEach(inst -> feeInstallmentPlanRepository.delete(inst));
        
        // Create new installments
        LocalDate baseDate = LocalDate.now();
        for (int i = 0; i < newInstallmentAmounts.size(); i++) {
            FeeInstallmentPlan installment = new FeeInstallmentPlan();
            installment.setStudentFeeAllocationId(allocationId);
            installment.setInstallmentNumber(i + 1);
            installment.setDueDate(baseDate.plusMonths(i + 1));
            installment.setDueAmount(newInstallmentAmounts.get(i));
            installment.setPaidAmount(BigDecimal.ZERO);
            installment.setStatus("PENDING");
            
            feeInstallmentPlanRepository.save(installment);
        }
        
        allocation.setNumberOfInstallments(newInstallmentAmounts.size());
        studentFeeAllocationRepository.save(allocation);
        
        createAuditLog("FEE_MANAGEMENT", "StudentFeeAllocation", allocationId, "ADJUST_INSTALLMENTS", existing, newInstallmentAmounts, performedBy);
    }
    
    private void recalculateInstallments(List<FeeInstallmentPlan> installments, BigDecimal newTotal) {
        int count = installments.size();
        BigDecimal perInstallment = newTotal.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
        
        for (FeeInstallmentPlan inst : installments) {
            inst.setDueAmount(perInstallment);
            feeInstallmentPlanRepository.save(inst);
        }
    }
    
    // ========================================
    // SPECIAL PAYMENT METHODS
    // ========================================
    @Override
    public StudentFeePayment recordPayment(Long allocationId, BigDecimal amount, String paymentMode,
                                           String transactionRef, Long collectedBy) {
        StudentFeePayment payment = new StudentFeePayment();
        payment.setStudentFeeAllocationId(allocationId);
        payment.setPaidAmount(amount);
        payment.setLateFeePaid(BigDecimal.ZERO);
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentMode(paymentMode);
        payment.setTransactionReference(transactionRef);
        payment.setPaymentStatus("SUCCESS");
        payment.setCollectedBy(collectedBy);
        
        return createStudentFeePayment(payment, collectedBy);
    }
    
    @Override
    public StudentFeePayment recordInstallmentPayment(Long installmentPlanId, BigDecimal amount,
                                                      String paymentMode, String transactionRef, Long collectedBy) {
        FeeInstallmentPlan installment = getFeeInstallmentPlanById(installmentPlanId);
        
        // Calculate late fee if overdue
        BigDecimal lateFee = BigDecimal.ZERO;
        if (LocalDate.now().isAfter(installment.getDueDate())) {
            lateFee = calculateLateFeeForInstallment(installment);
        }
        
        StudentFeePayment payment = new StudentFeePayment();
        payment.setStudentFeeAllocationId(installment.getStudentFeeAllocationId());
        payment.setInstallmentPlanId(installmentPlanId);
        payment.setPaidAmount(amount);
        payment.setLateFeePaid(lateFee);
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentMode(paymentMode);
        payment.setTransactionReference(transactionRef);
        payment.setPaymentStatus("SUCCESS");
        payment.setCollectedBy(collectedBy);
        
        return createStudentFeePayment(payment, collectedBy);
    }
    
    @Override
    public List<StudentFeePayment> getPaymentHistory(Long userId) {
        List<StudentFeeAllocation> allocations = getStudentFeeAllocationsByUserId(userId);
        List<StudentFeePayment> allPayments = new ArrayList<>();
        
        for (StudentFeeAllocation allocation : allocations) {
            allPayments.addAll(getPaymentsByAllocationId(allocation.getId()));
        }
        
        allPayments.sort((p1, p2) -> p2.getPaymentDate().compareTo(p1.getPaymentDate()));
        return allPayments;
    }
    
    private void updateAllocationStatus(Long allocationId) {
        StudentFeeAllocation allocation = getStudentFeeAllocationById(allocationId);
        
        BigDecimal totalPaid = studentFeePaymentRepository.getTotalPaidAmount(allocationId)
            .orElse(BigDecimal.ZERO);
        totalPaid = totalPaid.add(allocation.getInitialPayment());
        
        if (totalPaid.compareTo(allocation.getFinalAmount()) >= 0) {
            allocation.setStatus("PAID");
        } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
            allocation.setStatus("PARTIALLY_PAID");
        } else if (LocalDate.now().isAfter(allocation.getDueDate())) {
            allocation.setStatus("OVERDUE");
        }
        
        studentFeeAllocationRepository.save(allocation);
    }
    
    private void updateInstallmentAfterPayment(Long installmentId, BigDecimal paidAmount) {
        FeeInstallmentPlan installment = getFeeInstallmentPlanById(installmentId);
        
        BigDecimal currentPaid = installment.getPaidAmount().add(paidAmount);
        installment.setPaidAmount(currentPaid);
        
        if (currentPaid.compareTo(installment.getDueAmount()) >= 0) {
            installment.setStatus("PAID");
        } else {
            installment.setStatus("PARTIALLY_PAID");
        }
        
        feeInstallmentPlanRepository.save(installment);
    }
    
    // ========================================
    // LATE FEE CALCULATION
    // ========================================
    @Override
    public void updateOverdueInstallments() {
        List<FeeInstallmentPlan> overdueInstallments = feeInstallmentPlanRepository
            .findOverdueInstallments(LocalDate.now());
        
        for (FeeInstallmentPlan installment : overdueInstallments) {
            installment.setStatus("OVERDUE");
            BigDecimal lateFee = calculateLateFeeForInstallment(installment);
            installment.setLateFee(lateFee);
            feeInstallmentPlanRepository.save(installment);
        }
    }
    
    @Override
    public void calculateLateFees(Long allocationId) {
        List<FeeInstallmentPlan> installments = getInstallmentPlansByAllocationId(allocationId);
        
        for (FeeInstallmentPlan installment : installments) {
            if (LocalDate.now().isAfter(installment.getDueDate()) && 
                !"PAID".equals(installment.getStatus())) {
                BigDecimal lateFee = calculateLateFeeForInstallment(installment);
                installment.setLateFee(lateFee);
                installment.setStatus("OVERDUE");
                feeInstallmentPlanRepository.save(installment);
            }
        }
    }
    
    private BigDecimal calculateLateFeeForInstallment(FeeInstallmentPlan installment) {
        StudentFeeAllocation allocation = getStudentFeeAllocationById(installment.getStudentFeeAllocationId());
        FeeStructure feeStructure = getFeeStructureById(allocation.getFeeStructureId());
        
        Optional<LateFeeRule> ruleOpt = lateFeeRuleRepository.findByFeeTypeIdAndIsActiveTrue(feeStructure.getFeeTypeId());
        if (!ruleOpt.isPresent()) {
            ruleOpt = lateFeeRuleRepository.findDefaultLateFeeRule();
        }
        
        if (!ruleOpt.isPresent()) {
            return BigDecimal.ZERO;
        }
        
        LateFeeRule rule = ruleOpt.get();
        long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(installment.getDueDate(), LocalDate.now());
        
        if (daysOverdue <= rule.getGracePeriodDays()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal lateFee = BigDecimal.ZERO;
        BigDecimal pendingAmount = installment.getDueAmount().subtract(installment.getPaidAmount());
        
        switch (rule.getCalculationType()) {
            case "FIXED":
                lateFee = rule.getAmount();
                break;
            case "PERCENTAGE":
                lateFee = pendingAmount.multiply(rule.getAmount()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                break;
            case "PER_DAY":
                lateFee = rule.getAmount().multiply(BigDecimal.valueOf(daysOverdue - rule.getGracePeriodDays()));
                break;
        }
        
        if (rule.getMaxLateFee() != null && lateFee.compareTo(rule.getMaxLateFee()) > 0) {
            lateFee = rule.getMaxLateFee();
        }
        
        return lateFee;
    }
 // Continue from Part 2...

    // ========================================
    // FEE DISCOUNT OPERATIONS
    // ========================================
    @Override
    public FeeDiscount createFeeDiscount(FeeDiscount discount, Long performedBy) {
        // Calculate actual discount amount
        FeeStructure feeStructure = getFeeStructureById(discount.getFeeStructureId());
        BigDecimal calculatedAmount = calculateDiscountAmount(
            feeStructure.getTotalAmount(),
            discount.getDiscountType(),
            discount.getDiscountValue()
        );
        discount.setCalculatedAmount(calculatedAmount);
        
        FeeDiscount saved = feeDiscountRepository.save(discount);
        createAuditLog("FEE_MANAGEMENT", "FeeDiscount", saved.getId(), "CREATE", null, saved, performedBy);
        return saved;
    }
    
    @Override
    public List<FeeDiscount> getAllFeeDiscounts() {
        return feeDiscountRepository.findAll();
    }
    
    @Override
    public FeeDiscount getFeeDiscountById(Long id) {
        return feeDiscountRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("FeeDiscount not found with id: " + id));
    }
    
    @Override
    public List<FeeDiscount> getDiscountsByUserId(Long userId) {
        return feeDiscountRepository.findByUserId(userId);
    }
    
    @Override
    public FeeDiscount updateFeeDiscount(Long id, FeeDiscount discount, Long performedBy) {
        FeeDiscount existing = getFeeDiscountById(id);
        
        if (discount.getUserId() != null) existing.setUserId(discount.getUserId());
        if (discount.getFeeStructureId() != null) existing.setFeeStructureId(discount.getFeeStructureId());
        if (discount.getDiscountType() != null) existing.setDiscountType(discount.getDiscountType());
        if (discount.getDiscountValue() != null) existing.setDiscountValue(discount.getDiscountValue());
        if (discount.getCalculatedAmount() != null) existing.setCalculatedAmount(discount.getCalculatedAmount());
        if (discount.getReason() != null) existing.setReason(discount.getReason());
        if (discount.getApprovedBy() != null) existing.setApprovedBy(discount.getApprovedBy());
        if (discount.getApprovedDate() != null) existing.setApprovedDate(discount.getApprovedDate());
        if (discount.getStatus() != null) existing.setStatus(discount.getStatus());
        if (discount.getValidFrom() != null) existing.setValidFrom(discount.getValidFrom());
        if (discount.getValidTo() != null) existing.setValidTo(discount.getValidTo());
        
        FeeDiscount updated = feeDiscountRepository.save(existing);
        createAuditLog("FEE_MANAGEMENT", "FeeDiscount", id, "UPDATE", existing, updated, performedBy);
        return updated;
    }
    
    @Override
    public void deleteFeeDiscount(Long id, Long performedBy) {
        FeeDiscount existing = getFeeDiscountById(id);
        createAuditLog("FEE_MANAGEMENT", "FeeDiscount", id, "DELETE", existing, null, performedBy);
        feeDiscountRepository.deleteById(id);
    }
    
    @Override
    public FeeDiscount approveDiscount(Long discountId, Long approvedBy) {
        FeeDiscount discount = getFeeDiscountById(discountId);
        discount.setStatus("APPROVED");
        discount.setApprovedBy(approvedBy);
        discount.setApprovedDate(LocalDate.now());
        
        FeeDiscount updated = feeDiscountRepository.save(discount);
        createAuditLog("FEE_MANAGEMENT", "FeeDiscount", discountId, "APPROVE", discount, updated, approvedBy);
        return updated;
    }
    
    @Override
    public FeeDiscount rejectDiscount(Long discountId, Long approvedBy, String reason) {
        FeeDiscount discount = getFeeDiscountById(discountId);
        discount.setStatus("REJECTED");
        discount.setApprovedBy(approvedBy);
        discount.setApprovedDate(LocalDate.now());
        discount.setReason(reason);
        
        FeeDiscount updated = feeDiscountRepository.save(discount);
        createAuditLog("FEE_MANAGEMENT", "FeeDiscount", discountId, "REJECT", discount, updated, approvedBy);
        return updated;
    }
    
    @Override
    public BigDecimal calculateDiscountAmount(BigDecimal originalAmount, String discountType, BigDecimal discountValue) {
        if ("PERCENTAGE".equals(discountType)) {
            return originalAmount.multiply(discountValue).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            return discountValue;
        }
    }
    
    // ========================================
    // FEE REFUND OPERATIONS
    // ========================================
    @Override
    public FeeRefund createFeeRefund(FeeRefund refund, Long performedBy) {
        refund.setRequestedBy(performedBy);
        FeeRefund saved = feeRefundRepository.save(refund);
        createAuditLog("FEE_MANAGEMENT", "FeeRefund", saved.getId(), "CREATE", null, saved, performedBy);
        return saved;
    }
    
    @Override
    public List<FeeRefund> getAllFeeRefunds() {
        return feeRefundRepository.findAll();
    }
    
    @Override
    public FeeRefund getFeeRefundById(Long id) {
        return feeRefundRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("FeeRefund not found with id: " + id));
    }
    
    @Override
    public List<FeeRefund> getRefundsByUserId(Long userId) {
        return feeRefundRepository.findByUserId(userId);
    }
    
    @Override
    public FeeRefund updateFeeRefund(Long id, FeeRefund refund, Long performedBy) {
        FeeRefund existing = getFeeRefundById(id);
        
        if (refund.getStudentFeePaymentId() != null) existing.setStudentFeePaymentId(refund.getStudentFeePaymentId());
        if (refund.getUserId() != null) existing.setUserId(refund.getUserId());
        if (refund.getRefundAmount() != null) existing.setRefundAmount(refund.getRefundAmount());
        if (refund.getRefundDate() != null) existing.setRefundDate(refund.getRefundDate());
        if (refund.getRefundMode() != null) existing.setRefundMode(refund.getRefundMode());
        if (refund.getReason() != null) existing.setReason(refund.getReason());
        if (refund.getRequestedBy() != null) existing.setRequestedBy(refund.getRequestedBy());
        if (refund.getApprovedBy() != null) existing.setApprovedBy(refund.getApprovedBy());
        if (refund.getApprovalDate() != null) existing.setApprovalDate(refund.getApprovalDate());
        if (refund.getStatus() != null) existing.setStatus(refund.getStatus());
        if (refund.getTransactionReference() != null) existing.setTransactionReference(refund.getTransactionReference());
        
        FeeRefund updated = feeRefundRepository.save(existing);
        createAuditLog("FEE_MANAGEMENT", "FeeRefund", id, "UPDATE", existing, updated, performedBy);
        return updated;
    }
    
    @Override
    public void deleteFeeRefund(Long id, Long performedBy) {
        FeeRefund existing = getFeeRefundById(id);
        createAuditLog("FEE_MANAGEMENT", "FeeRefund", id, "DELETE", existing, null, performedBy);
        feeRefundRepository.deleteById(id);
    }
    
    @Override
    public FeeRefund approveRefund(Long refundId, Long approvedBy) {
        FeeRefund refund = getFeeRefundById(refundId);
        refund.setStatus("APPROVED");
        refund.setApprovedBy(approvedBy);
        refund.setApprovalDate(LocalDate.now());
        
        FeeRefund updated = feeRefundRepository.save(refund);
        createAuditLog("FEE_MANAGEMENT", "FeeRefund", refundId, "APPROVE", refund, updated, approvedBy);
        return updated;
    }
    
    @Override
    public FeeRefund rejectRefund(Long refundId, Long approvedBy, String reason) {
        FeeRefund refund = getFeeRefundById(refundId);
        refund.setStatus("REJECTED");
        refund.setApprovedBy(approvedBy);
        refund.setApprovalDate(LocalDate.now());
        refund.setReason(reason);
        
        FeeRefund updated = feeRefundRepository.save(refund);
        createAuditLog("FEE_MANAGEMENT", "FeeRefund", refundId, "REJECT", refund, updated, approvedBy);
        return updated;
    }
    
    @Override
    public FeeRefund processRefund(Long refundId, String transactionRef, Long performedBy) {
        FeeRefund refund = getFeeRefundById(refundId);
        
        if (!"APPROVED".equals(refund.getStatus())) {
            throw new RuntimeException("Refund must be approved before processing");
        }
        
        refund.setStatus("COMPLETED");
        refund.setTransactionReference(transactionRef);
        refund.setRefundDate(LocalDate.now());
        
        FeeRefund updated = feeRefundRepository.save(refund);
        createAuditLog("FEE_MANAGEMENT", "FeeRefund", refundId, "PROCESS", refund, updated, performedBy);
        return updated;
    }
    
    // ========================================
    // FEE RECEIPT OPERATIONS (AUTO-GENERATED)
    // ========================================
    @Override
    public List<FeeReceipt> getAllFeeReceipts() {
        return feeReceiptRepository.findAll();
    }
    
    @Override
    public FeeReceipt getFeeReceiptById(Long id) {
        return feeReceiptRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("FeeReceipt not found with id: " + id));
    }
    
    @Override
    public FeeReceipt getFeeReceiptByPaymentId(Long paymentId) {
        return feeReceiptRepository.findByPaymentId(paymentId)
            .orElseThrow(() -> new RuntimeException("FeeReceipt not found for payment id: " + paymentId));
    }
    
    @Override
    public List<FeeReceipt> getReceiptsByStudentUserId(Long userId) {
        return feeReceiptRepository.findByStudentUserId(userId);
    }
    
    @Override
    public FeeReceipt generateReceipt(Long paymentId) {
        // Check if receipt already exists
        Optional<FeeReceipt> existingReceipt = feeReceiptRepository.findByPaymentId(paymentId);
        if (existingReceipt.isPresent()) {
            return existingReceipt.get();
        }
        
        StudentFeePayment payment = getStudentFeePaymentById(paymentId);
        
        FeeReceipt receipt = new FeeReceipt();
        receipt.setPaymentId(paymentId);
        receipt.setReceiptNumber(generateReceiptNumber(paymentId));
        receipt.setAmountPaid(payment.getPaidAmount());
        receipt.setPaymentMode(payment.getPaymentMode());
        receipt.setEmailSent(false);
        
        return feeReceiptRepository.save(receipt);
    }
    
    private String generateReceiptNumber(Long paymentId) {
        String prefix = "REC";
        String year = String.valueOf(java.time.Year.now().getValue());
        String paddedId = String.format("%08d", paymentId);
        return prefix + year + paddedId;
    }
    
    @Override
    public void sendReceiptEmail(Long receiptId, String studentEmail) {
        FeeReceipt receipt = getFeeReceiptById(receiptId);
        
        // TODO: Implement email sending logic
        // This would use JavaMailSender to send PDF via email
        
        receipt.setEmailSent(true);
        receipt.setEmailSentAt(LocalDateTime.now());
        feeReceiptRepository.save(receipt);
    }
    
    // ========================================
    // AUDIT LOG OPERATIONS (READ-ONLY)
    // ========================================
    @Override
    public List<AuditLog> getAllAuditLogs() {
        return auditLogRepository.findAll();
    }
    
    @Override
    public AuditLog getAuditLogById(Long id) {
        return auditLogRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("AuditLog not found with id: " + id));
    }
    
    @Override
    public List<AuditLog> getAuditLogsByModule(String module) {
        return auditLogRepository.findByModule(module);
    }
    
    @Override
    public List<AuditLog> getAuditLogsByEntity(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }
    
    // ========================================
    // LATE FEE RULE OPERATIONS
    // ========================================
    @Override
    public LateFeeRule createLateFeeRule(LateFeeRule lateFeeRule, Long performedBy) {
        LateFeeRule saved = lateFeeRuleRepository.save(lateFeeRule);
        createAuditLog("FEE_MANAGEMENT", "LateFeeRule", saved.getId(), "CREATE", null, saved, performedBy);
        return saved;
    }
    
    @Override
    public List<LateFeeRule> getAllLateFeeRules() {
        return lateFeeRuleRepository.findAll();
    }
    
    @Override
    public LateFeeRule getLateFeeRuleById(Long id) {
        return lateFeeRuleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("LateFeeRule not found with id: " + id));
    }
    
    @Override
    public LateFeeRule updateLateFeeRule(Long id, LateFeeRule lateFeeRule, Long performedBy) {
        LateFeeRule existing = getLateFeeRuleById(id);
        
        if (lateFeeRule.getFeeTypeId() != null) existing.setFeeTypeId(lateFeeRule.getFeeTypeId());
        if (lateFeeRule.getCalculationType() != null) existing.setCalculationType(lateFeeRule.getCalculationType());
        if (lateFeeRule.getAmount() != null) existing.setAmount(lateFeeRule.getAmount());
        if (lateFeeRule.getGracePeriodDays() != null) existing.setGracePeriodDays(lateFeeRule.getGracePeriodDays());
        if (lateFeeRule.getMaxLateFee() != null) existing.setMaxLateFee(lateFeeRule.getMaxLateFee());
        if (lateFeeRule.getIsActive() != null) existing.setIsActive(lateFeeRule.getIsActive());
        
        LateFeeRule updated = lateFeeRuleRepository.save(existing);
        createAuditLog("FEE_MANAGEMENT", "LateFeeRule", id, "UPDATE", existing, updated, performedBy);
        return updated;
    }
    
    @Override
    public void deleteLateFeeRule(Long id, Long performedBy) {
        LateFeeRule existing = getLateFeeRuleById(id);
        createAuditLog("FEE_MANAGEMENT", "LateFeeRule", id, "DELETE", existing, null, performedBy);
        lateFeeRuleRepository.deleteById(id);
    }
    
    // ========================================
    // REPORTING & ANALYTICS
    // ========================================
    @Override
    public Map<String, Object> getStudentFeeReport(Long userId) {
        List<StudentFeeAllocation> allocations = getStudentFeeAllocationsByUserId(userId);
        
        Map<String, Object> report = new HashMap<>();
        BigDecimal totalAllocated = BigDecimal.ZERO;
        BigDecimal totalPaid = BigDecimal.ZERO;
        BigDecimal totalPending = BigDecimal.ZERO;
        
        for (StudentFeeAllocation allocation : allocations) {
            totalAllocated = totalAllocated.add(allocation.getFinalAmount());
            BigDecimal paid = studentFeePaymentRepository.getTotalPaidAmount(allocation.getId())
                .orElse(BigDecimal.ZERO);
            totalPaid = totalPaid.add(paid);
        }
        
        totalPending = totalAllocated.subtract(totalPaid);
        
        report.put("userId", userId);
        report.put("totalAllocated", totalAllocated);
        report.put("totalPaid", totalPaid);
        report.put("totalPending", totalPending);
        report.put("allocations", allocations);
        
        return report;
    }
    
    @Override
    public Map<String, Object> getBatchFeeReport(Long batchId) {
        List<StudentFeeAllocation> allocations = studentFeeAllocationRepository.findByBatchId(batchId);
        
        Map<String, Object> report = new HashMap<>();
        BigDecimal totalAllocated = BigDecimal.ZERO;
        BigDecimal totalPaid = BigDecimal.ZERO;
        int studentCount = (int) allocations.stream().map(StudentFeeAllocation::getUserId).distinct().count();
        
        for (StudentFeeAllocation allocation : allocations) {
            totalAllocated = totalAllocated.add(allocation.getFinalAmount());
            BigDecimal paid = studentFeePaymentRepository.getTotalPaidAmount(allocation.getId())
                .orElse(BigDecimal.ZERO);
            totalPaid = totalPaid.add(paid);
        }
        
        report.put("batchId", batchId);
        report.put("studentCount", studentCount);
        report.put("totalAllocated", totalAllocated);
        report.put("totalCollected", totalPaid);
        report.put("totalPending", totalAllocated.subtract(totalPaid));
        
        return report;
    }
    
    @Override
    public Map<String, Object> getCourseFeeReport(Long courseId) {
        List<StudentFeeAllocation> allocations = studentFeeAllocationRepository.findByCourseId(courseId);
        
        Map<String, Object> report = new HashMap<>();
        BigDecimal totalAllocated = BigDecimal.ZERO;
        BigDecimal totalPaid = BigDecimal.ZERO;
        
        for (StudentFeeAllocation allocation : allocations) {
            totalAllocated = totalAllocated.add(allocation.getFinalAmount());
            BigDecimal paid = studentFeePaymentRepository.getTotalPaidAmount(allocation.getId())
                .orElse(BigDecimal.ZERO);
            totalPaid = totalPaid.add(paid);
        }
        
        report.put("courseId", courseId);
        report.put("totalAllocated", totalAllocated);
        report.put("totalCollected", totalPaid);
        report.put("totalPending", totalAllocated.subtract(totalPaid));
        
        return report;
    }
    
    @Override
    public Map<String, Object> getRevenueReport(LocalDate startDate, LocalDate endDate) {
        List<StudentFeePayment> payments = studentFeePaymentRepository.findByPaymentDateBetween(startDate, endDate);
        
        BigDecimal totalRevenue = payments.stream()
            .filter(p -> "SUCCESS".equals(p.getPaymentStatus()))
            .map(StudentFeePayment::getPaidAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, Object> report = new HashMap<>();
        report.put("startDate", startDate);
        report.put("endDate", endDate);
        report.put("totalRevenue", totalRevenue);
        report.put("transactionCount", payments.size());
        report.put("payments", payments);
        
        return report;
    }
    
    @Override
    public Map<String, Object> getMonthlyRevenueReport(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        return getRevenueReport(startDate, endDate);
    }
    
    @Override
    public Map<String, Object> getQuarterlyRevenueReport(int year, int quarter) {
        int startMonth = (quarter - 1) * 3 + 1;
        LocalDate startDate = LocalDate.of(year, startMonth, 1);
        LocalDate endDate = startDate.plusMonths(3).minusDays(1);
        return getRevenueReport(startDate, endDate);
    }
    
    @Override
    public Map<String, Object> getYearlyRevenueReport(int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        return getRevenueReport(startDate, endDate);
    }
    
    @Override
    public Map<String, Object> getPendingFeesReport() {
        List<StudentFeeAllocation> pending = studentFeeAllocationRepository.findByStatus("PENDING");
        
        BigDecimal totalPending = pending.stream()
            .map(StudentFeeAllocation::getRemainingAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, Object> report = new HashMap<>();
        report.put("totalPendingAmount", totalPending);
        report.put("studentCount", pending.size());
        report.put("pendingAllocations", pending);
        
        return report;
    }
    
    @Override
    public Map<String, Object> getOverdueFeesReport() {
        List<StudentFeeAllocation> overdue = studentFeeAllocationRepository.findByStatus("OVERDUE");
        
        BigDecimal totalOverdue = overdue.stream()
            .map(StudentFeeAllocation::getRemainingAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, Object> report = new HashMap<>();
        report.put("totalOverdueAmount", totalOverdue);
        report.put("studentCount", overdue.size());
        report.put("overdueAllocations", overdue);
        
        return report;
    }
    
    @Override
    public List<StudentFeeAllocation> getStudentsWithPendingFees() {
        return studentFeeAllocationRepository.findByStatus("PENDING");
    }
    
    @Override
    public List<StudentFeeAllocation> getStudentsWithOverdueFees() {
        return studentFeeAllocationRepository.findByStatus("OVERDUE");
    }
}

