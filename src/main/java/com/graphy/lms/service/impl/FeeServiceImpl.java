package com.graphy.lms.service.impl;

import com.graphy.lms.entity.*;
import com.graphy.lms.service.*;
import com.graphy.lms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class FeeServiceImpl implements FeeService {
	private static final Logger logger = LoggerFactory.getLogger(FeeServiceImpl.class);

    // ============================================
    // REPOSITORY INJECTIONS
    // ============================================
    
    @Autowired private FeeTypeRepository feeTypeRepository;
    @Autowired private FeeStructureRepository feeStructureRepository;
    @Autowired private FeeDiscountRepository feeDiscountRepository;
    @Autowired private StudentFeeAllocationRepository studentFeeAllocationRepository;
    @Autowired private PaymentAlternativeRepository paymentAlternativeRepository;
    @Autowired private StudentInstallmentPlanRepository studentInstallmentPlanRepository;
    @Autowired private StudentFeePaymentRepository studentFeePaymentRepository;
    @Autowired private LateFeeConfigRepository lateFeeConfigRepository;
    @Autowired private LateFeePenaltyRepository lateFeePenaltyRepository;
    @Autowired private AttendancePenaltyRepository attendancePenaltyRepository;
    @Autowired private ExamFeeLinkageRepository examFeeLinkageRepository;
    @Autowired private FeeRefundRepository feeRefundRepository;
    @Autowired private FeeReceiptRepository feeReceiptRepository;
    @Autowired private PaymentNotificationRepository paymentNotificationRepository;
    @Autowired private AutoDebitConfigRepository autoDebitConfigRepository;
    @Autowired private CurrencyRateRepository currencyRateRepository;
    @Autowired private AuditLogRepository auditLogRepository;
    @Autowired private CertificateBlockListRepository certificateBlockListRepository;

    // ============================================
    // 1. FEE TYPES CRUD
    // ============================================
    
    @Override
    public FeeType createFeeType(FeeType feeType) {
        logger.debug("Creating new fee type: {}", feeType.getName());
        
        FeeType saved = feeTypeRepository.save(feeType);
        createAuditLog("FEE_MANAGEMENT", "FeeType", saved.getId(), 
                      AuditLog.Action.CREATE, null, feeType.toString(), null);
        
        logger.info("Fee type created successfully with ID: {}", saved.getId());
        return saved;
    }

    

    @Override
    public FeeType getFeeTypeById(Long id) {
        return feeTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FeeType not found with id: " + id));
    }

    @Override
    public List<FeeType> getAllFeeTypes() {
        return feeTypeRepository.findAll();
    }

    @Override
    public List<FeeType> getActiveFeeTypes() {
        return feeTypeRepository.findByIsActive(true);
    }

    @Override
    public FeeType updateFeeType(Long id, FeeType feeType) {
        FeeType existing = getFeeTypeById(id);
        String oldValue = existing.toString();
        
        // Update only provided fields
        if (feeType.getName() != null) existing.setName(feeType.getName());
        if (feeType.getDescription() != null) existing.setDescription(feeType.getDescription());
        if (feeType.getIsActive() != null) existing.setIsActive(feeType.getIsActive());
        
        FeeType updated = feeTypeRepository.save(existing);
        createAuditLog("FEE_MANAGEMENT", "FeeType", id, 
                      AuditLog.Action.UPDATE, oldValue, updated.toString(), null);
        return updated;
    }

    @Override
    public void deleteFeeType(Long id) {
        FeeType existing = getFeeTypeById(id);
        feeTypeRepository.deleteById(id);
        createAuditLog("FEE_MANAGEMENT", "FeeType", id, 
                      AuditLog.Action.DELETE, existing.toString(), null, null);
    }

    // ============================================
    // 2. FEE STRUCTURES CRUD
    // ============================================
    
    @Override
    public FeeStructure createFeeStructure(FeeStructure feeStructure) {
        FeeStructure saved = feeStructureRepository.save(feeStructure);
        createAuditLog("FEE_MANAGEMENT", "FeeStructure", saved.getId(), 
                      AuditLog.Action.CREATE, null, feeStructure.toString(), null);
        return saved;
    }

    @Override
    public FeeStructure getFeeStructureById(Long id) {
        return feeStructureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FeeStructure not found with id: " + id));
    }

    @Override
    public List<FeeStructure> getAllFeeStructures() {
        return feeStructureRepository.findAll();
    }

    @Override
    public List<FeeStructure> getFeeStructuresByCourse(Long courseId) {
        return feeStructureRepository.findByCourseId(courseId);
    }

    @Override
    public List<FeeStructure> getFeeStructuresByAcademicYear(String academicYear) {
        return feeStructureRepository.findByAcademicYear(academicYear);
    }

    @Override
    public List<FeeStructure> getFeeStructuresByBatch(Long batchId) {
        return feeStructureRepository.findByBatchId(batchId);
    }

    @Override
    public FeeStructure updateFeeStructure(Long id, FeeStructure feeStructure) {
        FeeStructure existing = getFeeStructureById(id);
        String oldValue = existing.toString();
        
        if (feeStructure.getFeeTypeId() != null) existing.setFeeTypeId(feeStructure.getFeeTypeId());
        if (feeStructure.getAcademicYear() != null) existing.setAcademicYear(feeStructure.getAcademicYear());
        if (feeStructure.getCourseId() != null) existing.setCourseId(feeStructure.getCourseId());
        if (feeStructure.getBatchId() != null) existing.setBatchId(feeStructure.getBatchId());
        if (feeStructure.getTotalAmount() != null) existing.setTotalAmount(feeStructure.getTotalAmount());
        if (feeStructure.getCurrency() != null) existing.setCurrency(feeStructure.getCurrency());
        if (feeStructure.getIsActive() != null) existing.setIsActive(feeStructure.getIsActive());
        
        FeeStructure updated = feeStructureRepository.save(existing);
        createAuditLog("FEE_MANAGEMENT", "FeeStructure", id, 
                      AuditLog.Action.UPDATE, oldValue, updated.toString(), null);
        return updated;
    }

    @Override
    public void deleteFeeStructure(Long id) {
        FeeStructure existing = getFeeStructureById(id);
        feeStructureRepository.deleteById(id);
        createAuditLog("FEE_MANAGEMENT", "FeeStructure", id, 
                      AuditLog.Action.DELETE, existing.toString(), null, null);
    }

    // ============================================
    // 3. FEE DISCOUNTS CRUD
    // ============================================
    
    @Override
    public FeeDiscount createFeeDiscount(FeeDiscount feeDiscount) {
        FeeDiscount saved = feeDiscountRepository.save(feeDiscount);
        createAuditLog("FEE_MANAGEMENT", "FeeDiscount", saved.getId(), 
                      AuditLog.Action.CREATE, null, feeDiscount.toString(), null);
        return saved;
    }

    @Override
    public FeeDiscount getFeeDiscountById(Long id) {
        return feeDiscountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FeeDiscount not found with id: " + id));
    }

    @Override
    public List<FeeDiscount> getAllFeeDiscounts() {
        return feeDiscountRepository.findAll();
    }

    @Override
    public List<FeeDiscount> getFeeDiscountsByUserId(Long userId) {
        return feeDiscountRepository.findByUserId(userId);
    }

    @Override
    public FeeDiscount updateFeeDiscount(Long id, FeeDiscount feeDiscount) {
        FeeDiscount existing = getFeeDiscountById(id);
        String oldValue = existing.toString();
        
        if (feeDiscount.getUserId() != null) existing.setUserId(feeDiscount.getUserId());
        if (feeDiscount.getFeeStructureId() != null) existing.setFeeStructureId(feeDiscount.getFeeStructureId());
        if (feeDiscount.getDiscountName() != null) existing.setDiscountName(feeDiscount.getDiscountName());
        if (feeDiscount.getDiscountType() != null) existing.setDiscountType(feeDiscount.getDiscountType());
        if (feeDiscount.getDiscountValue() != null) existing.setDiscountValue(feeDiscount.getDiscountValue());
        if (feeDiscount.getReason() != null) existing.setReason(feeDiscount.getReason());
        if (feeDiscount.getApprovedBy() != null) existing.setApprovedBy(feeDiscount.getApprovedBy());
        if (feeDiscount.getApprovedDate() != null) existing.setApprovedDate(feeDiscount.getApprovedDate());
        if (feeDiscount.getIsActive() != null) existing.setIsActive(feeDiscount.getIsActive());
        
        FeeDiscount updated = feeDiscountRepository.save(existing);
        createAuditLog("FEE_MANAGEMENT", "FeeDiscount", id, 
                      AuditLog.Action.UPDATE, oldValue, updated.toString(), null);
        return updated;
    }

    @Override
    public void deleteFeeDiscount(Long id) {
        FeeDiscount existing = getFeeDiscountById(id);
        feeDiscountRepository.deleteById(id);
        createAuditLog("FEE_MANAGEMENT", "FeeDiscount", id, 
                      AuditLog.Action.DELETE, existing.toString(), null, null);
    }

    // ============================================
    // 4. STUDENT FEE ALLOCATIONS CRUD + BUSINESS LOGIC
    // ============================================
    
    @Override
    public StudentFeeAllocation createStudentFeeAllocation(StudentFeeAllocation allocation) {
        // Auto-calculate payable amount if discounts exist
        if (allocation.getFeeStructureId() != null && allocation.getUserId() != null) {
            BigDecimal payableAmount = calculatePayableAmount(allocation.getUserId(), allocation.getFeeStructureId());
            allocation.setPayableAmount(payableAmount);
            
            // Calculate remaining amount after advance payment
            BigDecimal advancePayment = allocation.getAdvancePayment() != null ? 
                                       allocation.getAdvancePayment() : BigDecimal.ZERO;
            allocation.setRemainingAmount(payableAmount.subtract(advancePayment));
        }
        
        StudentFeeAllocation saved = studentFeeAllocationRepository.save(allocation);
        createAuditLog("FEE_MANAGEMENT", "StudentFeeAllocation", saved.getId(), 
                      AuditLog.Action.CREATE, null, allocation.toString(), null);
        return saved;
    }

    @Override
    public StudentFeeAllocation getFeeAllocationById(Long id) {
        return studentFeeAllocationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("StudentFeeAllocation not found with id: " + id));
    }

    @Override
    public List<StudentFeeAllocation> getAllFeeAllocations() {
        return studentFeeAllocationRepository.findAll();
    }

    @Override
    public List<StudentFeeAllocation> getFeeAllocationsByUserId(Long userId) {
        return studentFeeAllocationRepository.findByUserId(userId);
    }

    @Override
    public StudentFeeAllocation updateFeeAllocation(Long id, StudentFeeAllocation allocation) {
        StudentFeeAllocation existing = getFeeAllocationById(id);
        String oldValue = existing.toString();
        
        if (allocation.getUserId() != null) existing.setUserId(allocation.getUserId());
        if (allocation.getFeeStructureId() != null) existing.setFeeStructureId(allocation.getFeeStructureId());
        if (allocation.getOriginalAmount() != null) existing.setOriginalAmount(allocation.getOriginalAmount());
        if (allocation.getTotalDiscount() != null) existing.setTotalDiscount(allocation.getTotalDiscount());
        if (allocation.getPayableAmount() != null) existing.setPayableAmount(allocation.getPayableAmount());
        if (allocation.getAdvancePayment() != null) existing.setAdvancePayment(allocation.getAdvancePayment());
        if (allocation.getRemainingAmount() != null) existing.setRemainingAmount(allocation.getRemainingAmount());
        if (allocation.getCurrency() != null) existing.setCurrency(allocation.getCurrency());
        if (allocation.getAllocationDate() != null) existing.setAllocationDate(allocation.getAllocationDate());
        if (allocation.getStatus() != null) existing.setStatus(allocation.getStatus());
        
        StudentFeeAllocation updated = studentFeeAllocationRepository.save(existing);
        createAuditLog("FEE_MANAGEMENT", "StudentFeeAllocation", id, 
                      AuditLog.Action.UPDATE, oldValue, updated.toString(), null);
        return updated;
    }

    @Override
    public void deleteFeeAllocation(Long id) {
        StudentFeeAllocation existing = getFeeAllocationById(id);
        studentFeeAllocationRepository.deleteById(id);
        createAuditLog("FEE_MANAGEMENT", "StudentFeeAllocation", id, 
                      AuditLog.Action.DELETE, existing.toString(), null, null);
    }

    @Override
    public BigDecimal calculatePayableAmount(Long userId, Long feeStructureId) {
        // Get original fee structure amount
        FeeStructure feeStructure = getFeeStructureById(feeStructureId);
        BigDecimal originalAmount = feeStructure.getTotalAmount();
        
        // Get all active discounts for this user and fee structure
        List<FeeDiscount> discounts = feeDiscountRepository.findByUserIdAndFeeStructureId(userId, feeStructureId)
                .stream()
                .filter(d -> d.getIsActive() != null && d.getIsActive())
                .collect(Collectors.toList());
        
        BigDecimal totalDiscount = BigDecimal.ZERO;
        
        for (FeeDiscount discount : discounts) {
            if (discount.getDiscountType() == FeeDiscount.DiscountType.PERCENTAGE) {
                // Percentage discount
                BigDecimal discountAmount = originalAmount.multiply(discount.getDiscountValue())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                totalDiscount = totalDiscount.add(discountAmount);
            } else {
                // Flat discount
                totalDiscount = totalDiscount.add(discount.getDiscountValue());
            }
        }
        
        BigDecimal payableAmount = originalAmount.subtract(totalDiscount);
        
        // Ensure payable amount is not negative
        return payableAmount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : payableAmount;
    }
 // ============================================
    // 5. PAYMENT ALTERNATIVES CRUD
    // ============================================
    
    @Override
    public PaymentAlternative createPaymentAlternative(PaymentAlternative alternative) {
        PaymentAlternative saved = paymentAlternativeRepository.save(alternative);
        createAuditLog("FEE_MANAGEMENT", "PaymentAlternative", saved.getId(), 
                      AuditLog.Action.CREATE, null, alternative.toString(), null);
        return saved;
    }

    @Override
    public PaymentAlternative getPaymentAlternativeById(Long id) {
        return paymentAlternativeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PaymentAlternative not found with id: " + id));
    }

    @Override
    public List<PaymentAlternative> getAllPaymentAlternatives() {
        return paymentAlternativeRepository.findAll();
    }

    @Override
    public List<PaymentAlternative> getActivePaymentAlternatives() {
        return paymentAlternativeRepository.findByIsActive(true);
    }

    @Override
    public PaymentAlternative updatePaymentAlternative(Long id, PaymentAlternative alternative) {
        PaymentAlternative existing = getPaymentAlternativeById(id);
        String oldValue = existing.toString();
        
        if (alternative.getAlternativeName() != null) existing.setAlternativeName(alternative.getAlternativeName());
        if (alternative.getNumberOfInstallments() != null) existing.setNumberOfInstallments(alternative.getNumberOfInstallments());
        if (alternative.getDescription() != null) existing.setDescription(alternative.getDescription());
        if (alternative.getIsActive() != null) existing.setIsActive(alternative.getIsActive());
        if (alternative.getCreatedBy() != null) existing.setCreatedBy(alternative.getCreatedBy());
        
        PaymentAlternative updated = paymentAlternativeRepository.save(existing);
        createAuditLog("FEE_MANAGEMENT", "PaymentAlternative", id, 
                      AuditLog.Action.UPDATE, oldValue, updated.toString(), null);
        return updated;
    }

    @Override
    public void deletePaymentAlternative(Long id) {
        PaymentAlternative existing = getPaymentAlternativeById(id);
        paymentAlternativeRepository.deleteById(id);
        createAuditLog("FEE_MANAGEMENT", "PaymentAlternative", id, 
                      AuditLog.Action.DELETE, existing.toString(), null, null);
    }

    // ============================================
    // 6. STUDENT INSTALLMENT PLANS CRUD + BUSINESS LOGIC
    // ============================================
    
    @Override
    public StudentInstallmentPlan createInstallmentPlan(StudentInstallmentPlan plan) {
        StudentInstallmentPlan saved = studentInstallmentPlanRepository.save(plan);
        createAuditLog("FEE_MANAGEMENT", "StudentInstallmentPlan", saved.getId(), 
                      AuditLog.Action.CREATE, null, plan.toString(), null);
        return saved;
    }

    @Override
    public StudentInstallmentPlan getInstallmentPlanById(Long id) {
        return studentInstallmentPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("StudentInstallmentPlan not found with id: " + id));
    }

    @Override
    public List<StudentInstallmentPlan> getAllInstallmentPlans() {
        return studentInstallmentPlanRepository.findAll();
    }

    @Override
    public List<StudentInstallmentPlan> getInstallmentPlansByAllocationId(Long allocationId) {
        return studentInstallmentPlanRepository.findByStudentFeeAllocationId(allocationId);
    }

    @Override
    public StudentInstallmentPlan updateInstallmentPlan(Long id, StudentInstallmentPlan plan) {
        StudentInstallmentPlan existing = getInstallmentPlanById(id);
        String oldValue = existing.toString();
        
        if (plan.getStudentFeeAllocationId() != null) existing.setStudentFeeAllocationId(plan.getStudentFeeAllocationId());
        if (plan.getPaymentAlternativeId() != null) existing.setPaymentAlternativeId(plan.getPaymentAlternativeId());
        if (plan.getInstallmentNumber() != null) existing.setInstallmentNumber(plan.getInstallmentNumber());
        if (plan.getInstallmentAmount() != null) existing.setInstallmentAmount(plan.getInstallmentAmount());
        if (plan.getDueDate() != null) existing.setDueDate(plan.getDueDate());
        if (plan.getPaidAmount() != null) existing.setPaidAmount(plan.getPaidAmount());
        if (plan.getStatus() != null) existing.setStatus(plan.getStatus());
        
        StudentInstallmentPlan updated = studentInstallmentPlanRepository.save(existing);
        createAuditLog("FEE_MANAGEMENT", "StudentInstallmentPlan", id, 
                      AuditLog.Action.UPDATE, oldValue, updated.toString(), null);
        return updated;
    }

    @Override
    public void deleteInstallmentPlan(Long id) {
        StudentInstallmentPlan existing = getInstallmentPlanById(id);
        studentInstallmentPlanRepository.deleteById(id);
        createAuditLog("FEE_MANAGEMENT", "StudentInstallmentPlan", id, 
                      AuditLog.Action.DELETE, existing.toString(), null, null);
    }

    @Override
    public List<StudentInstallmentPlan> createInstallmentsForStudent(Long allocationId, Long alternativeId, 
                                                                      List<Map<String, Object>> installmentDetails) {
        StudentFeeAllocation allocation = getFeeAllocationById(allocationId);
        PaymentAlternative alternative = getPaymentAlternativeById(alternativeId);
        
        // Validate: Number of installments must match alternative
        if (installmentDetails.size() != alternative.getNumberOfInstallments()) {
            throw new RuntimeException("Number of installments must be " + alternative.getNumberOfInstallments());
        }
        
        // Validate: Sum of installment amounts must equal remaining amount
        BigDecimal totalInstallmentAmount = installmentDetails.stream()
                .map(detail -> new BigDecimal(detail.get("amount").toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalInstallmentAmount.compareTo(allocation.getRemainingAmount()) != 0) {
            throw new RuntimeException("Sum of installment amounts must equal remaining amount: " + 
                                      allocation.getRemainingAmount());
        }
        
        List<StudentInstallmentPlan> installments = new ArrayList<>();
        
        for (int i = 0; i < installmentDetails.size(); i++) {
            Map<String, Object> detail = installmentDetails.get(i);
            
            StudentInstallmentPlan installment = new StudentInstallmentPlan();
            installment.setStudentFeeAllocationId(allocationId);
            installment.setPaymentAlternativeId(alternativeId);
            installment.setInstallmentNumber(i + 1);
            installment.setInstallmentAmount(new BigDecimal(detail.get("amount").toString()));
            installment.setDueDate(LocalDate.parse(detail.get("dueDate").toString()));
            installment.setStatus(StudentInstallmentPlan.InstallmentStatus.PENDING);
            
            installments.add(studentInstallmentPlanRepository.save(installment));
        }
        
        return installments;
    }

    @Override
    @Transactional
    public List<StudentInstallmentPlan> resetInstallments(Long allocationId, Long alternativeId, 
                                                        List<Map<String, Object>> newInstallmentDetails) {
        
        // 1. Fetch the Alternative
        PaymentAlternative alternative = getPaymentAlternativeById(alternativeId);

        // 2. Fetch Existing Installments
        List<StudentInstallmentPlan> existingInstallments = getInstallmentPlansByAllocationId(allocationId);
        
        // 3. SEPARATE Paid vs. Pending
        List<StudentInstallmentPlan> paidInstallments = new ArrayList<>();
        BigDecimal totalPaidInstallments = BigDecimal.ZERO;

        for (StudentInstallmentPlan plan : existingInstallments) {
            if (plan.getStatus() == StudentInstallmentPlan.InstallmentStatus.PAID) {
                paidInstallments.add(plan);
                totalPaidInstallments = totalPaidInstallments.add(plan.getInstallmentAmount());
            } else {
                // Delete PENDING installments to make room for the new plan
                studentInstallmentPlanRepository.delete(plan);
            }
        }

        // 4. VALIDATE: (Paid Installments + New Plan) should equal (Total Payable - Advance Payment)
        StudentFeeAllocation allocation = getFeeAllocationById(allocationId);
        
        BigDecimal totalPayable = allocation.getPayableAmount();
        BigDecimal advancePayment = allocation.getAdvancePayment() != null ? 
                                   allocation.getAdvancePayment() : BigDecimal.ZERO;
        
        // The Target Amount involved in installments is (Total - Advance)
        BigDecimal targetInstallmentTotal = totalPayable.subtract(advancePayment);
        
        BigDecimal newPlanTotal = BigDecimal.ZERO;
        for (Map<String, Object> detail : newInstallmentDetails) {
            newPlanTotal = newPlanTotal.add(new BigDecimal(detail.get("dueAmount").toString()));
        }

        BigDecimal finalCalculatedTotal = totalPaidInstallments.add(newPlanTotal);
        
        // Allow a small difference of 1.00 for floating point errors
        if (finalCalculatedTotal.subtract(targetInstallmentTotal).abs().compareTo(BigDecimal.ONE) > 0) {
             throw new RuntimeException("Mismatch! Target Installment Total is " + targetInstallmentTotal + 
                                       " (Total " + totalPayable + " - Advance " + advancePayment + ")" +
                                       " but (Paid Inst " + totalPaidInstallments + " + New Plan " + newPlanTotal + ") = " + finalCalculatedTotal);
        }

        // 5. CREATE NEW INSTALLMENTS
        List<StudentInstallmentPlan> newPlans = new ArrayList<>();
        
        // Add back the paid ones
        newPlans.addAll(paidInstallments);

        for (Map<String, Object> detail : newInstallmentDetails) {
            StudentInstallmentPlan plan = new StudentInstallmentPlan();
            plan.setStudentFeeAllocationId(allocationId);
            plan.setPaymentAlternativeId(alternativeId);
            plan.setInstallmentNumber((Integer) detail.get("installmentNumber"));
            plan.setDueDate(LocalDate.parse(detail.get("dueDate").toString())); 
            plan.setInstallmentAmount(new BigDecimal(detail.get("dueAmount").toString()));
            plan.setStatus(StudentInstallmentPlan.InstallmentStatus.PENDING);
            
            newPlans.add(studentInstallmentPlanRepository.save(plan));
        }

        return newPlans;
    }	

    @Override
    public List<StudentInstallmentPlan> getOverdueInstallments() {
        return studentInstallmentPlanRepository.findOverdueInstallments(LocalDate.now());
    }
 // ============================================
    // 7. STUDENT FEE PAYMENTS CRUD + BUSINESS LOGIC
    // ============================================
    
    @Override
    public StudentFeePayment createPayment(StudentFeePayment payment) {
        StudentFeePayment saved = studentFeePaymentRepository.save(payment);
        createAuditLog("FEE_MANAGEMENT", "StudentFeePayment", saved.getId(), 
                      AuditLog.Action.CREATE, null, payment.toString(), null);
        
        // Update installment status if linked
        if (saved.getStudentInstallmentPlanId() != null) {
            updateInstallmentStatus(saved.getStudentInstallmentPlanId(), saved.getPaidAmount());
        }
        
        // Auto-generate receipt if payment is successful
        if (saved.getPaymentStatus() == StudentFeePayment.PaymentStatus.SUCCESS) {
            generateReceipt(saved.getId());
        }
        
        return saved;
    }

    @Override
    public StudentFeePayment getPaymentById(Long id) {
        return studentFeePaymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("StudentFeePayment not found with id: " + id));
    }

    @Override
    public List<StudentFeePayment> getAllPayments() {
        return studentFeePaymentRepository.findAll();
    }

    @Override
    public List<StudentFeePayment> getPaymentsByAllocationId(Long allocationId) {
        return studentFeePaymentRepository.findByStudentFeeAllocationId(allocationId);
    }

    @Override
    public StudentFeePayment updatePayment(Long id, StudentFeePayment payment) {
        StudentFeePayment existing = getPaymentById(id);
        String oldValue = existing.toString();
        
        if (payment.getStudentFeeAllocationId() != null) existing.setStudentFeeAllocationId(payment.getStudentFeeAllocationId());
        if (payment.getStudentInstallmentPlanId() != null) existing.setStudentInstallmentPlanId(payment.getStudentInstallmentPlanId());
        if (payment.getPaidAmount() != null) existing.setPaidAmount(payment.getPaidAmount());
        if (payment.getPaymentDate() != null) existing.setPaymentDate(payment.getPaymentDate());
        if (payment.getPaymentMode() != null) existing.setPaymentMode(payment.getPaymentMode());
        if (payment.getPaymentStatus() != null) existing.setPaymentStatus(payment.getPaymentStatus());
        if (payment.getTransactionReference() != null) existing.setTransactionReference(payment.getTransactionReference());
        if (payment.getGatewayResponse() != null) existing.setGatewayResponse(payment.getGatewayResponse());
        if (payment.getScreenshotUrl() != null) existing.setScreenshotUrl(payment.getScreenshotUrl());
        if (payment.getCurrency() != null) existing.setCurrency(payment.getCurrency());
        if (payment.getRecordedBy() != null) existing.setRecordedBy(payment.getRecordedBy());
        
        StudentFeePayment updated = studentFeePaymentRepository.save(existing);
        createAuditLog("FEE_MANAGEMENT", "StudentFeePayment", id, 
                      AuditLog.Action.UPDATE, oldValue, updated.toString(), null);
        return updated;
    }

    @Override
    public void deletePayment(Long id) {
        StudentFeePayment existing = getPaymentById(id);
        studentFeePaymentRepository.deleteById(id);
        createAuditLog("FEE_MANAGEMENT", "StudentFeePayment", id, 
                      AuditLog.Action.DELETE, existing.toString(), null, null);
    }

    @Override
    public StudentFeePayment processOnlinePayment(Long allocationId, Long installmentPlanId, 
                                                  BigDecimal amount, String paymentMode, 
                                                  String transactionRef, String gatewayResponse) {
        logger.info("Processing online payment - Allocation ID: {}, Amount: {}", allocationId, amount);
        
        try {
            StudentFeePayment payment = new StudentFeePayment();
            payment.setStudentFeeAllocationId(allocationId);
            payment.setStudentInstallmentPlanId(installmentPlanId);
            payment.setPaidAmount(amount);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setPaymentMode(StudentFeePayment.PaymentMode.valueOf(paymentMode));
            payment.setPaymentStatus(StudentFeePayment.PaymentStatus.SUCCESS);
            payment.setTransactionReference(transactionRef);
            payment.setGatewayResponse(gatewayResponse);
            
            StudentFeePayment saved = createPayment(payment);

            // ====================================================================
            // 🔴 FIX ADDED HERE: Update the Parent Allocation Balance
            // ====================================================================
            StudentFeeAllocation allocation = getFeeAllocationById(allocationId);
            
            // 1. Calculate New Remaining Amount (Old Remaining - Current Payment)
            BigDecimal newRemaining = allocation.getRemainingAmount().subtract(amount);
            allocation.setRemainingAmount(newRemaining);
            
            // 2. Check if fully paid
            if (newRemaining.compareTo(BigDecimal.ZERO) <= 0) {
                allocation.setStatus(StudentFeeAllocation.AllocationStatus.COMPLETED);
                unblockCertificate(allocation.getUserId()); // Bonus: Auto-unblock if paid
            }
            
            // 3. SAVE THE ALLOCATION (This updates the report!)
            studentFeeAllocationRepository.save(allocation);
            // ====================================================================
            
            logger.info("Online payment processed successfully - Payment ID: {}", saved.getId());
            return saved;
            
        } catch (Exception e) {
            logger.error("Failed to process online payment for allocation {}: {}", allocationId, e.getMessage());
            throw e;
        }
    }

    @Override
    public StudentFeePayment recordManualPayment(Long allocationId, Long installmentPlanId, 
                                                 BigDecimal amount, String paymentMode, 
                                                 String transactionRef, Long recordedBy) {
        StudentFeePayment payment = new StudentFeePayment();
        payment.setStudentFeeAllocationId(allocationId);
        payment.setStudentInstallmentPlanId(installmentPlanId);
        payment.setPaidAmount(amount);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMode(StudentFeePayment.PaymentMode.valueOf(paymentMode));
        payment.setPaymentStatus(StudentFeePayment.PaymentStatus.SUCCESS);
        payment.setTransactionReference(transactionRef);
        payment.setRecordedBy(recordedBy);
        
        StudentFeePayment saved = createPayment(payment);
        
        // Update allocation remaining amount
        StudentFeeAllocation allocation = getFeeAllocationById(allocationId);
        BigDecimal newRemaining = allocation.getRemainingAmount().subtract(amount);
        allocation.setRemainingAmount(newRemaining);
        
        // Update status if fully paid
        if (newRemaining.compareTo(BigDecimal.ZERO) <= 0) {
            allocation.setStatus(StudentFeeAllocation.AllocationStatus.COMPLETED);
            unblockCertificate(allocation.getUserId());
        }
        studentFeeAllocationRepository.save(allocation);
        
        return saved;
    }

    @Override
    public void updateInstallmentStatus(Long installmentPlanId, BigDecimal paidAmount) {
        StudentInstallmentPlan installment = getInstallmentPlanById(installmentPlanId);
        
        // Update paid amount
        BigDecimal currentPaid = installment.getPaidAmount() != null ? installment.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal newPaid = currentPaid.add(paidAmount);
        installment.setPaidAmount(newPaid);
        
        // Update status
        if (newPaid.compareTo(installment.getInstallmentAmount()) >= 0) {
            installment.setStatus(StudentInstallmentPlan.InstallmentStatus.PAID);
        } else if (newPaid.compareTo(BigDecimal.ZERO) > 0) {
            installment.setStatus(StudentInstallmentPlan.InstallmentStatus.PARTIALLY_PAID);
        }
        
        studentInstallmentPlanRepository.save(installment);
    }

    // ============================================
    // 8. LATE FEE CONFIG CRUD
    // ============================================
    
    @Override
    public LateFeeConfig createLateFeeConfig(LateFeeConfig config) {
        LateFeeConfig saved = lateFeeConfigRepository.save(config);
        createAuditLog("FEE_MANAGEMENT", "LateFeeConfig", saved.getId(), 
                      AuditLog.Action.CREATE, null, config.toString(), null);
        return saved;
    }

    @Override
    public LateFeeConfig getLateFeeConfigById(Long id) {
        return lateFeeConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("LateFeeConfig not found with id: " + id));
    }

    @Override
    public List<LateFeeConfig> getAllLateFeeConfigs() {
        return lateFeeConfigRepository.findAll();
    }

    @Override
    public List<LateFeeConfig> getActiveLateFeeConfigs() {
        return lateFeeConfigRepository.findByIsActive(true);
    }

    @Override
    public LateFeeConfig updateLateFeeConfig(Long id, LateFeeConfig config) {
        LateFeeConfig existing = getLateFeeConfigById(id);
        String oldValue = existing.toString();
        
        if (config.getPaymentSchedule() != null) existing.setPaymentSchedule(config.getPaymentSchedule());
        if (config.getPeriodCount() != null) existing.setPeriodCount(config.getPeriodCount());
        if (config.getPenaltyAmount() != null) existing.setPenaltyAmount(config.getPenaltyAmount());
        if (config.getIsActive() != null) existing.setIsActive(config.getIsActive());
        if (config.getEffectiveFrom() != null) existing.setEffectiveFrom(config.getEffectiveFrom());
        if (config.getEffectiveTo() != null) existing.setEffectiveTo(config.getEffectiveTo());
        if (config.getCreatedBy() != null) existing.setCreatedBy(config.getCreatedBy());
        
        LateFeeConfig updated = lateFeeConfigRepository.save(existing);
        createAuditLog("FEE_MANAGEMENT", "LateFeeConfig", id, 
                      AuditLog.Action.UPDATE, oldValue, updated.toString(), null);
        return updated;
    }

    @Override
    public void deleteLateFeeConfig(Long id) {
        LateFeeConfig existing = getLateFeeConfigById(id);
        lateFeeConfigRepository.deleteById(id);
        createAuditLog("FEE_MANAGEMENT", "LateFeeConfig", id, 
                      AuditLog.Action.DELETE, existing.toString(), null, null);
    }

    // ============================================
    // 9. LATE FEE PENALTIES CRUD + AUTO CALCULATION
    // ============================================
    
    @Override
    public LateFeePenalty createLateFeePenalty(LateFeePenalty penalty) {
        LateFeePenalty saved = lateFeePenaltyRepository.save(penalty);
        createAuditLog("FEE_MANAGEMENT", "LateFeePenalty", saved.getId(), 
                      AuditLog.Action.CREATE, null, penalty.toString(), null);
        return saved;
    }

    @Override
    public LateFeePenalty getLateFeePenaltyById(Long id) {
        return lateFeePenaltyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("LateFeePenalty not found with id: " + id));
    }

    @Override
    public List<LateFeePenalty> getAllLateFeePenalties() {
        return lateFeePenaltyRepository.findAll();
    }

    @Override
    public List<LateFeePenalty> getLateFeePenaltiesByInstallmentId(Long installmentId) {
        return lateFeePenaltyRepository.findByStudentInstallmentPlanId(installmentId);
    }

    @Override
    public LateFeePenalty updateLateFeePenalty(Long id, LateFeePenalty penalty) {
        LateFeePenalty existing = getLateFeePenaltyById(id);
        String oldValue = existing.toString();
        
        if (penalty.getStudentInstallmentPlanId() != null) existing.setStudentInstallmentPlanId(penalty.getStudentInstallmentPlanId());
        if (penalty.getPenaltyAmount() != null) existing.setPenaltyAmount(penalty.getPenaltyAmount());
        if (penalty.getPenaltyDate() != null) existing.setPenaltyDate(penalty.getPenaltyDate());
        if (penalty.getReason() != null) existing.setReason(penalty.getReason());
        if (penalty.getIsWaived() != null) existing.setIsWaived(penalty.getIsWaived());
        if (penalty.getWaivedBy() != null) existing.setWaivedBy(penalty.getWaivedBy());
        if (penalty.getWaivedDate() != null) existing.setWaivedDate(penalty.getWaivedDate());
        
        LateFeePenalty updated = lateFeePenaltyRepository.save(existing);
        createAuditLog("FEE_MANAGEMENT", "LateFeePenalty", id, 
                      AuditLog.Action.UPDATE, oldValue, updated.toString(), null);
        return updated;
    }

    @Override
    public void deleteLateFeePenalty(Long id) {
        LateFeePenalty existing = getLateFeePenaltyById(id);
        lateFeePenaltyRepository.deleteById(id);
        createAuditLog("FEE_MANAGEMENT", "LateFeePenalty", id, 
                      AuditLog.Action.DELETE, existing.toString(), null, null);
    }

    @Override
    public void applyLateFees() {
        LocalDate today = LocalDate.now();
        List<StudentInstallmentPlan> overdueInstallments = getOverdueInstallments();
        
        // Get active late fee configs
        List<LateFeeConfig> configs = lateFeeConfigRepository.findActiveConfigsForDate(today);
        
        for (StudentInstallmentPlan installment : overdueInstallments) {
            // Skip if already fully paid
            if (installment.getStatus() == StudentInstallmentPlan.InstallmentStatus.PAID) {
                continue;
            }
            
            LocalDate dueDate = installment.getDueDate();
            long daysPastDue = ChronoUnit.DAYS.between(dueDate, today);
            
            for (LateFeeConfig config : configs) {
                int periodDays = 0;
                
                // Calculate period in days based on schedule type
                switch (config.getPaymentSchedule()) {
                    case MONTHLY:
                        periodDays = 30 * config.getPeriodCount();
                        break;
                    case QUARTERLY:
                        periodDays = 90 * config.getPeriodCount();
                        break;
                    case YEARLY:
                        periodDays = 365 * config.getPeriodCount();
                        break;
                }
                
                // Apply penalty if overdue period matches
                if (daysPastDue >= periodDays && daysPastDue < periodDays + 30) {
                    // Check if penalty already applied for this period
                    List<LateFeePenalty> existingPenalties = 
                        lateFeePenaltyRepository.findByStudentInstallmentPlanId(installment.getId());
                    
                    boolean alreadyApplied = existingPenalties.stream()
                        .anyMatch(p -> p.getPenaltyDate().equals(today));
                    
                    if (!alreadyApplied) {
                        LateFeePenalty penalty = new LateFeePenalty();
                        penalty.setStudentInstallmentPlanId(installment.getId());
                        penalty.setPenaltyAmount(config.getPenaltyAmount());
                        penalty.setPenaltyDate(today);
                        penalty.setReason("Overdue by " + daysPastDue + " days");
                        
                        createLateFeePenalty(penalty);
                        
                        // Update installment status to OVERDUE
                        installment.setStatus(StudentInstallmentPlan.InstallmentStatus.OVERDUE);
                        studentInstallmentPlanRepository.save(installment);
                        
                        // Get student allocation and send warning
                        StudentFeeAllocation allocation = getFeeAllocationById(installment.getStudentFeeAllocationId());
                        sendOverdueWarningNotification(allocation.getUserId(), installment.getId(), "student@example.com");
                    }
                }
            }
        }
    }

    @Override
    public LateFeePenalty waiveLateFee(Long penaltyId, Long waivedBy) {
        LateFeePenalty penalty = getLateFeePenaltyById(penaltyId);
        penalty.setIsWaived(true);
        penalty.setWaivedBy(waivedBy);
        penalty.setWaivedDate(LocalDate.now());
        
        return lateFeePenaltyRepository.save(penalty);
    }

    // ============================================
    // 10. ATTENDANCE PENALTIES CRUD + AUTO APPLICATION
    // ============================================
    
    @Override
    public AttendancePenalty createAttendancePenalty(AttendancePenalty penalty) {
        AttendancePenalty saved = attendancePenaltyRepository.save(penalty);
        createAuditLog("FEE_MANAGEMENT", "AttendancePenalty", saved.getId(), 
                      AuditLog.Action.CREATE, null, penalty.toString(), null);
        
        // Update student allocation remaining amount
        StudentFeeAllocation allocation = getFeeAllocationById(penalty.getStudentFeeAllocationId());
        BigDecimal newRemaining = allocation.getRemainingAmount().add(penalty.getPenaltyAmount());
        allocation.setRemainingAmount(newRemaining);
        studentFeeAllocationRepository.save(allocation);
        
        return saved;
    }

    @Override
    public AttendancePenalty getAttendancePenaltyById(Long id) {
        return attendancePenaltyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AttendancePenalty not found with id: " + id));
    }

    @Override
    public List<AttendancePenalty> getAllAttendancePenalties() {
        return attendancePenaltyRepository.findAll();
    }

    @Override
    public List<AttendancePenalty> getAttendancePenaltiesByUserId(Long userId) {
        return attendancePenaltyRepository.findByUserId(userId);
    }

    @Override
    public AttendancePenalty updateAttendancePenalty(Long id, AttendancePenalty penalty) {
        AttendancePenalty existing = getAttendancePenaltyById(id);
        String oldValue = existing.toString();
        
        if (penalty.getUserId() != null) existing.setUserId(penalty.getUserId());
        if (penalty.getStudentFeeAllocationId() != null) existing.setStudentFeeAllocationId(penalty.getStudentFeeAllocationId());
        if (penalty.getAbsenceDate() != null) existing.setAbsenceDate(penalty.getAbsenceDate());
        if (penalty.getPenaltyAmount() != null) existing.setPenaltyAmount(penalty.getPenaltyAmount());
        if (penalty.getReason() != null) existing.setReason(penalty.getReason());
        if (penalty.getAppliedBy() != null) existing.setAppliedBy(penalty.getAppliedBy());
        if (penalty.getIsActive() != null) existing.setIsActive(penalty.getIsActive());
        
        AttendancePenalty updated = attendancePenaltyRepository.save(existing);
        createAuditLog("FEE_MANAGEMENT", "AttendancePenalty", id, 
                      AuditLog.Action.UPDATE, oldValue, updated.toString(), null);
        return updated;
    }

    @Override
    public void deleteAttendancePenalty(Long id) {
        AttendancePenalty existing = getAttendancePenaltyById(id);
        attendancePenaltyRepository.deleteById(id);
        createAuditLog("FEE_MANAGEMENT", "AttendancePenalty", id, 
                      AuditLog.Action.DELETE, existing.toString(), null, null);
    }

    @Override
    public AttendancePenalty applyAttendancePenalty(Long userId, Long allocationId, 
                                                    LocalDate absenceDate, BigDecimal penaltyAmount, 
                                                    String reason, Long appliedBy) {
        AttendancePenalty penalty = new AttendancePenalty();
        penalty.setUserId(userId);
        penalty.setStudentFeeAllocationId(allocationId);
        penalty.setAbsenceDate(absenceDate);
        penalty.setPenaltyAmount(penaltyAmount);
        penalty.setReason(reason);
        penalty.setAppliedBy(appliedBy);
        penalty.setIsActive(true);
        
        return createAttendancePenalty(penalty);
    }
 // ============================================
    // 11. EXAM FEE LINKAGE CRUD + AUTO LINKING
    // ============================================
    
    @Override
    public ExamFeeLinkage createExamFeeLinkage(ExamFeeLinkage linkage) {
        ExamFeeLinkage saved = examFeeLinkageRepository.save(linkage);
        createAuditLog("FEE_MANAGEMENT", "ExamFeeLinkage", saved.getId(), 
                      AuditLog.Action.CREATE, null, linkage.toString(), null);
        
        // Update student allocation remaining amount
        StudentFeeAllocation allocation = getFeeAllocationById(linkage.getStudentFeeAllocationId());
        BigDecimal newRemaining = allocation.getRemainingAmount().add(linkage.getExamFeeAmount());
        allocation.setRemainingAmount(newRemaining);
        studentFeeAllocationRepository.save(allocation);
        
        return saved;
    }

    @Override
    public ExamFeeLinkage getExamFeeLinkageById(Long id) {
        return examFeeLinkageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ExamFeeLinkage not found with id: " + id));
    }

    @Override
    public List<ExamFeeLinkage> getAllExamFeeLinkages() {
        return examFeeLinkageRepository.findAll();
    }

    @Override
    public List<ExamFeeLinkage> getExamFeeLinkagesByUserId(Long userId) {
        return examFeeLinkageRepository.findByUserId(userId);
    }

    @Override
    public ExamFeeLinkage updateExamFeeLinkage(Long id, ExamFeeLinkage linkage) {
        ExamFeeLinkage existing = getExamFeeLinkageById(id);
        String oldValue = existing.toString();
        
        if (linkage.getExamId() != null) existing.setExamId(linkage.getExamId());
        if (linkage.getUserId() != null) existing.setUserId(linkage.getUserId());
        if (linkage.getStudentFeeAllocationId() != null) existing.setStudentFeeAllocationId(linkage.getStudentFeeAllocationId());
        if (linkage.getExamFeeAmount() != null) existing.setExamFeeAmount(linkage.getExamFeeAmount());
        if (linkage.getCurrency() != null) existing.setCurrency(linkage.getCurrency());
        if (linkage.getAppliedDate() != null) existing.setAppliedDate(linkage.getAppliedDate());
        
        ExamFeeLinkage updated = examFeeLinkageRepository.save(existing);
        createAuditLog("FEE_MANAGEMENT", "ExamFeeLinkage", id, 
                      AuditLog.Action.UPDATE, oldValue, updated.toString(), null);
        return updated;
    }

    @Override
    public void deleteExamFeeLinkage(Long id) {
        ExamFeeLinkage existing = getExamFeeLinkageById(id);
        examFeeLinkageRepository.deleteById(id);
        createAuditLog("FEE_MANAGEMENT", "ExamFeeLinkage", id, 
                      AuditLog.Action.DELETE, existing.toString(), null, null);
    }

    @Override
    public ExamFeeLinkage linkExamFeeToStudent(Long examId, Long userId, Long allocationId, 
                                               BigDecimal examFeeAmount) {
        ExamFeeLinkage linkage = new ExamFeeLinkage();
        linkage.setExamId(examId);
        linkage.setUserId(userId);
        linkage.setStudentFeeAllocationId(allocationId);
        linkage.setExamFeeAmount(examFeeAmount);
        linkage.setAppliedDate(LocalDate.now());
        
        return createExamFeeLinkage(linkage);
    }

    // ============================================
    // 12. FEE REFUNDS CRUD + WORKFLOW
    // ============================================
    
    @Override
    public FeeRefund createRefundRequest(FeeRefund refund) {
        // 1. Fetch the Allocation details
        StudentFeeAllocation allocation = getFeeAllocationById(refund.getStudentFeeAllocationId());

        // 2. Calculate Total Refundable Amount (Sum of actual payments made)
        // Note: This excludes 'Advance Payment' because that is stored in Allocation, 
        // whereas this list comes from the Payment transaction table.
        List<StudentFeePayment> payments = studentFeePaymentRepository
                .findByStudentFeeAllocationId(allocation.getId());

        BigDecimal totalPaidViaInstallments = BigDecimal.ZERO;
        
        for (StudentFeePayment payment : payments) {
            // Only count successful payments towards the refundable limit
            if (payment.getPaymentStatus() == StudentFeePayment.PaymentStatus.SUCCESS) {
                totalPaidViaInstallments = totalPaidViaInstallments.add(payment.getPaidAmount());
            }
        }

        // 3. VALIDATION: Check if requested amount exceeds what they actually paid
        if (refund.getRefundAmount().compareTo(totalPaidViaInstallments) > 0) {
            throw new RuntimeException("Invalid Refund Request! You requested " + refund.getRefundAmount() + 
                                       " but the total refundable amount (excluding advance) is only " + 
                                       totalPaidViaInstallments);
        }

        // 4. If valid, proceed to save
        refund.setRefundStatus(FeeRefund.RefundStatus.PENDING);
        refund.setRequestedDate(LocalDate.now());
        
        FeeRefund saved = feeRefundRepository.save(refund);
        createAuditLog("FEE_MANAGEMENT", "FeeRefund", saved.getId(), 
                       AuditLog.Action.CREATE, null, refund.toString(), null);
        return saved;
    }

    @Override
    public FeeRefund getRefundById(Long id) {
        return feeRefundRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FeeRefund not found with id: " + id));
    }

    @Override
    public List<FeeRefund> getAllRefunds() {
        return feeRefundRepository.findAll();
    }

    @Override
    public List<FeeRefund> getRefundsByUserId(Long userId) {
        return feeRefundRepository.findByUserId(userId);
    }

    @Override
    public List<FeeRefund> getRefundsByStatus(FeeRefund.RefundStatus status) {
        return feeRefundRepository.findByRefundStatus(status);
    }

    @Override
    public FeeRefund updateRefund(Long id, FeeRefund refund) {
        FeeRefund existing = getRefundById(id);
        String oldValue = existing.toString();
        
        if (refund.getStudentFeePaymentId() != null) existing.setStudentFeePaymentId(refund.getStudentFeePaymentId());
        if (refund.getStudentFeeAllocationId() != null) existing.setStudentFeeAllocationId(refund.getStudentFeeAllocationId());
        if (refund.getUserId() != null) existing.setUserId(refund.getUserId());
        if (refund.getRefundAmount() != null) existing.setRefundAmount(refund.getRefundAmount());
        if (refund.getRefundReason() != null) existing.setRefundReason(refund.getRefundReason());
        if (refund.getRefundStatus() != null) existing.setRefundStatus(refund.getRefundStatus());
        if (refund.getRequestedDate() != null) existing.setRequestedDate(refund.getRequestedDate());
        if (refund.getApprovedBy() != null) existing.setApprovedBy(refund.getApprovedBy());
        if (refund.getApprovedDate() != null) existing.setApprovedDate(refund.getApprovedDate());
        if (refund.getProcessedDate() != null) existing.setProcessedDate(refund.getProcessedDate());
        if (refund.getRefundMode() != null) existing.setRefundMode(refund.getRefundMode());
        if (refund.getTransactionReference() != null) existing.setTransactionReference(refund.getTransactionReference());
        
        FeeRefund updated = feeRefundRepository.save(existing);
        createAuditLog("FEE_MANAGEMENT", "FeeRefund", id, 
                      AuditLog.Action.UPDATE, oldValue, updated.toString(), null);
        return updated;
    }

    @Override
    public void deleteRefund(Long id) {
        FeeRefund existing = getRefundById(id);
        feeRefundRepository.deleteById(id);
        createAuditLog("FEE_MANAGEMENT", "FeeRefund", id, 
                      AuditLog.Action.DELETE, existing.toString(), null, null);
    }

    @Override
    public FeeRefund approveRefund(Long refundId, Long approvedBy) {
        FeeRefund refund = getRefundById(refundId);
        refund.setRefundStatus(FeeRefund.RefundStatus.APPROVED);
        refund.setApprovedBy(approvedBy);
        refund.setApprovedDate(LocalDate.now());
        
        return feeRefundRepository.save(refund);
    }

    @Override
    public FeeRefund processRefund(Long refundId, String refundMode, String transactionRef) {
        FeeRefund refund = getRefundById(refundId);
        
        if (refund.getRefundStatus() != FeeRefund.RefundStatus.APPROVED) {
            throw new RuntimeException("Refund must be approved before processing");
        }
        
        refund.setRefundStatus(FeeRefund.RefundStatus.PROCESSED);
        refund.setProcessedDate(LocalDate.now());
        refund.setRefundMode(refundMode);
        refund.setTransactionReference(transactionRef);
        
        // Update allocation status
        StudentFeeAllocation allocation = getFeeAllocationById(refund.getStudentFeeAllocationId());
        allocation.setStatus(StudentFeeAllocation.AllocationStatus.REFUNDED);
        studentFeeAllocationRepository.save(allocation);
        
        // Update payment status
        StudentFeePayment payment = getPaymentById(refund.getStudentFeePaymentId());
        payment.setPaymentStatus(StudentFeePayment.PaymentStatus.REFUNDED);
        studentFeePaymentRepository.save(payment);
        
        return feeRefundRepository.save(refund);
    }

    @Override
    public FeeRefund rejectRefund(Long refundId, Long rejectedBy, String reason) {
        FeeRefund refund = getRefundById(refundId);
        refund.setRefundStatus(FeeRefund.RefundStatus.REJECTED);
        refund.setApprovedBy(rejectedBy);
        refund.setApprovedDate(LocalDate.now());
        refund.setRefundReason(refund.getRefundReason() + " | Rejection Reason: " + reason);
        
        return feeRefundRepository.save(refund);
    }

    // ============================================
    // 13. FEE RECEIPTS (AUTO-GENERATED - READ ONLY)
    // ============================================
    
    @Override
    public FeeReceipt getReceiptById(Long id) {
        return feeReceiptRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FeeReceipt not found with id: " + id));
    }

    @Override
    public List<FeeReceipt> getAllReceipts() {
        return feeReceiptRepository.findAll();
    }

    @Override
    public List<FeeReceipt> getReceiptsByUserId(Long userId) {
        return feeReceiptRepository.findByUserId(userId);
    }

    @Override
    public FeeReceipt getReceiptByPaymentId(Long paymentId) {
        return feeReceiptRepository.findByPaymentId(paymentId)
                .orElse(null);
    }

    @Override
    public FeeReceipt generateReceipt(Long paymentId) {
        // Check if receipt already exists
        FeeReceipt existingReceipt = getReceiptByPaymentId(paymentId);
        if (existingReceipt != null) {
            return existingReceipt;
        }
        
        StudentFeePayment payment = getPaymentById(paymentId);
        StudentFeeAllocation allocation = getFeeAllocationById(payment.getStudentFeeAllocationId());
        
        // Generate unique receipt number
        String receiptNumber = "REC-" + LocalDate.now().getYear() + "-" + 
                              String.format("%06d", paymentId);
        
        FeeReceipt receipt = new FeeReceipt();
        receipt.setPaymentId(paymentId);
        receipt.setReceiptNumber(receiptNumber);
        receipt.setUserId(allocation.getUserId());
        receipt.setReceiptPdfUrl("/receipts/" + receiptNumber + ".pdf");
        
        FeeReceipt saved = feeReceiptRepository.save(receipt);
        
        // Send receipt via email
        sendPaymentSuccessNotification(allocation.getUserId(), paymentId, "student@example.com");
        
        return saved;
    }

    // ============================================
    // 14. PAYMENT NOTIFICATIONS CRUD
    // ============================================
    
    @Override
    public PaymentNotification createNotification(PaymentNotification notification) {
        PaymentNotification saved = paymentNotificationRepository.save(notification);
        createAuditLog("FEE_MANAGEMENT", "PaymentNotification", saved.getId(), 
                      AuditLog.Action.CREATE, null, notification.toString(), null);
        return saved;
    }

    @Override
    public PaymentNotification getNotificationById(Long id) {
        return paymentNotificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PaymentNotification not found with id: " + id));
    }

    @Override
    public List<PaymentNotification> getAllNotifications() {
        return paymentNotificationRepository.findAll();
    }

    @Override
    public List<PaymentNotification> getNotificationsByUserId(Long userId) {
        return paymentNotificationRepository.findByUserId(userId);
    }

    @Override
    public PaymentNotification updateNotification(Long id, PaymentNotification notification) {
        PaymentNotification existing = getNotificationById(id);
        String oldValue = existing.toString();
        
        if (notification.getUserId() != null) existing.setUserId(notification.getUserId());
        if (notification.getNotificationType() != null) existing.setNotificationType(notification.getNotificationType());
        if (notification.getMessage() != null) existing.setMessage(notification.getMessage());
        if (notification.getEmail() != null) existing.setEmail(notification.getEmail());
        if (notification.getPhone() != null) existing.setPhone(notification.getPhone());
        if (notification.getSentAt() != null) existing.setSentAt(notification.getSentAt());
        if (notification.getDeliveryStatus() != null) existing.setDeliveryStatus(notification.getDeliveryStatus());
        
        PaymentNotification updated = paymentNotificationRepository.save(existing);
        createAuditLog("FEE_MANAGEMENT", "PaymentNotification", id, 
                      AuditLog.Action.UPDATE, oldValue, updated.toString(), null);
        return updated;
    }

    @Override
    public void deleteNotification(Long id) {
        PaymentNotification existing = getNotificationById(id);
        paymentNotificationRepository.deleteById(id);
        createAuditLog("FEE_MANAGEMENT", "PaymentNotification", id, 
                      AuditLog.Action.DELETE, existing.toString(), null, null);
    }

    @Override
    public void sendPaymentSuccessNotification(Long userId, Long paymentId, String email) {
        StudentFeePayment payment = getPaymentById(paymentId);
        FeeReceipt receipt = getReceiptByPaymentId(paymentId);
        
        String message = "Payment of " + payment.getPaidAmount() + " " + payment.getCurrency() + 
                        " received successfully. Receipt Number: " + 
                        (receipt != null ? receipt.getReceiptNumber() : "N/A");
        
        PaymentNotification notification = new PaymentNotification();
        notification.setUserId(userId);
        notification.setNotificationType(PaymentNotification.NotificationType.PAYMENT_SUCCESS);
        notification.setMessage(message);
        notification.setEmail(email);
        notification.setSentAt(LocalDateTime.now());
        notification.setDeliveryStatus(PaymentNotification.DeliveryStatus.SENT);
        
        createNotification(notification);
        
        // TODO: Actual email sending logic would go here
        System.out.println("Email sent to: " + email + " - " + message);
    }

    @Override
    public void sendPaymentFailedNotification(Long userId, String email, String reason) {
        String message = "Payment failed. Reason: " + reason + ". Please retry.";
        
        PaymentNotification notification = new PaymentNotification();
        notification.setUserId(userId);
        notification.setNotificationType(PaymentNotification.NotificationType.PAYMENT_FAILED);
        notification.setMessage(message);
        notification.setEmail(email);
        notification.setSentAt(LocalDateTime.now());
        notification.setDeliveryStatus(PaymentNotification.DeliveryStatus.SENT);
        
        createNotification(notification);
        
        System.out.println("Email sent to: " + email + " - " + message);
    }

    @Override
    public void sendDueReminderNotification(Long userId, Long installmentPlanId, String email) {
        StudentInstallmentPlan installment = getInstallmentPlanById(installmentPlanId);
        
        String message = "Reminder: Your installment of " + installment.getInstallmentAmount() + 
                        " is due on " + installment.getDueDate();
        
        PaymentNotification notification = new PaymentNotification();
        notification.setUserId(userId);
        notification.setNotificationType(PaymentNotification.NotificationType.DUE_REMINDER);
        notification.setMessage(message);
        notification.setEmail(email);
        notification.setSentAt(LocalDateTime.now());
        notification.setDeliveryStatus(PaymentNotification.DeliveryStatus.SENT);
        
        createNotification(notification);
        
        System.out.println("Email sent to: " + email + " - " + message);
    }

    @Override
    public void sendOverdueWarningNotification(Long userId, Long installmentPlanId, String email) {
        StudentInstallmentPlan installment = getInstallmentPlanById(installmentPlanId);
        
        // Calculate total penalties
        BigDecimal totalPenalty = lateFeePenaltyRepository.getTotalPenaltyByInstallmentPlanId(installmentPlanId);
        if (totalPenalty == null) totalPenalty = BigDecimal.ZERO;
        
        String message = "OVERDUE PAYMENT WARNING: Your installment of " + installment.getInstallmentAmount() + 
                        " was due on " + installment.getDueDate() + 
                        ". Late fee of " + totalPenalty + " has been applied. Please pay immediately.";
        
        PaymentNotification notification = new PaymentNotification();
        notification.setUserId(userId);
        notification.setNotificationType(PaymentNotification.NotificationType.OVERDUE_WARNING);
        notification.setMessage(message);
        notification.setEmail(email);
        notification.setSentAt(LocalDateTime.now());
        notification.setDeliveryStatus(PaymentNotification.DeliveryStatus.SENT);
        
        createNotification(notification);
        
        System.out.println("Email sent to: " + email + " - " + message);
    }
 // ============================================
    // 15. AUTO DEBIT CONFIG CRUD
    // ============================================
    
    @Override
    public AutoDebitConfig createAutoDebitConfig(AutoDebitConfig config) {
        AutoDebitConfig saved = autoDebitConfigRepository.save(config);
        createAuditLog("FEE_MANAGEMENT", "AutoDebitConfig", saved.getId(), 
                      AuditLog.Action.CREATE, null, config.toString(), null);
        return saved;
    }

    @Override
    public AutoDebitConfig getAutoDebitConfigById(Long id) {
        return autoDebitConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AutoDebitConfig not found with id: " + id));
    }

    @Override
    public List<AutoDebitConfig> getAllAutoDebitConfigs() {
        return autoDebitConfigRepository.findAll();
    }

    @Override
    public List<AutoDebitConfig> getAutoDebitConfigsByUserId(Long userId) {
        return autoDebitConfigRepository.findByUserId(userId);
    }

    @Override
    public AutoDebitConfig updateAutoDebitConfig(Long id, AutoDebitConfig config) {
        AutoDebitConfig existing = getAutoDebitConfigById(id);
        String oldValue = existing.toString();
        
        if (config.getUserId() != null) existing.setUserId(config.getUserId());
        if (config.getStudentFeeAllocationId() != null) existing.setStudentFeeAllocationId(config.getStudentFeeAllocationId());
        if (config.getBankAccountNumber() != null) existing.setBankAccountNumber(config.getBankAccountNumber());
        if (config.getCardToken() != null) existing.setCardToken(config.getCardToken());
        if (config.getPaymentGateway() != null) existing.setPaymentGateway(config.getPaymentGateway());
        if (config.getAutoDebitDay() != null) existing.setAutoDebitDay(config.getAutoDebitDay());
        if (config.getIsActive() != null) existing.setIsActive(config.getIsActive());
        if (config.getConsentGiven() != null) existing.setConsentGiven(config.getConsentGiven());
        if (config.getConsentDate() != null) existing.setConsentDate(config.getConsentDate());
        
        AutoDebitConfig updated = autoDebitConfigRepository.save(existing);
        createAuditLog("FEE_MANAGEMENT", "AutoDebitConfig", id, 
                      AuditLog.Action.UPDATE, oldValue, updated.toString(), null);
        return updated;
    }

    @Override
    public void deleteAutoDebitConfig(Long id) {
        AutoDebitConfig existing = getAutoDebitConfigById(id);
        autoDebitConfigRepository.deleteById(id);
        createAuditLog("FEE_MANAGEMENT", "AutoDebitConfig", id, 
                      AuditLog.Action.DELETE, existing.toString(), null, null);
    }

    @Override
    public void processAutoDebit() {
        LocalDate today = LocalDate.now();
        int currentDay = today.getDayOfMonth();
        
        // Get all active auto-debit configs with consent
        List<AutoDebitConfig> activeConfigs = autoDebitConfigRepository
                .findByIsActiveAndConsentGiven(true, true);
        
        for (AutoDebitConfig config : activeConfigs) {
            // Check if today is the auto-debit day
            if (config.getAutoDebitDay() != null && config.getAutoDebitDay() == currentDay) {
                
                // Get pending installments for this student
                List<StudentInstallmentPlan> installments = studentInstallmentPlanRepository
                        .findByStudentFeeAllocationId(config.getStudentFeeAllocationId())
                        .stream()
                        .filter(i -> i.getStatus() == StudentInstallmentPlan.InstallmentStatus.PENDING ||
                                    i.getStatus() == StudentInstallmentPlan.InstallmentStatus.PARTIALLY_PAID)
                        .sorted(Comparator.comparing(StudentInstallmentPlan::getDueDate))
                        .collect(Collectors.toList());
                
                if (!installments.isEmpty()) {
                    StudentInstallmentPlan nextInstallment = installments.get(0);
                    BigDecimal amountToDebit = nextInstallment.getInstallmentAmount()
                            .subtract(nextInstallment.getPaidAmount() != null ? 
                                     nextInstallment.getPaidAmount() : BigDecimal.ZERO);
                    
                    try {
                        // TODO: Integrate with actual payment gateway
                        // For now, simulate successful auto-debit
                        String transactionRef = "AUTO-" + System.currentTimeMillis();
                        
                        processOnlinePayment(
                            config.getStudentFeeAllocationId(),
                            nextInstallment.getId(),
                            amountToDebit,
                            "AUTO_DEBIT",
                            transactionRef,
                            "Auto-debit successful via " + config.getPaymentGateway()
                        );
                        
                        System.out.println("Auto-debit successful for user: " + config.getUserId() + 
                                         ", Amount: " + amountToDebit);
                        
                    } catch (Exception e) {
                        // Log auto-debit failure
                        StudentFeeAllocation allocation = getFeeAllocationById(config.getStudentFeeAllocationId());
                        sendPaymentFailedNotification(allocation.getUserId(), 
                                                     "student@example.com", 
                                                     "Auto-debit failed: " + e.getMessage());
                        
                        System.err.println("Auto-debit failed for user: " + config.getUserId() + 
                                         ", Error: " + e.getMessage());
                    }
                }
            }
        }
    }

    // ============================================
    // 16. CURRENCY RATES CRUD
    // ============================================
    
    @Override
    public CurrencyRate createCurrencyRate(CurrencyRate rate) {
        CurrencyRate saved = currencyRateRepository.save(rate);
        createAuditLog("FEE_MANAGEMENT", "CurrencyRate", saved.getId(), 
                      AuditLog.Action.CREATE, null, rate.toString(), null);
        return saved;
    }

    @Override
    public CurrencyRate getCurrencyRateById(Long id) {
        return currencyRateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CurrencyRate not found with id: " + id));
    }

    @Override
    public List<CurrencyRate> getAllCurrencyRates() {
        return currencyRateRepository.findAll();
    }

    @Override
    public CurrencyRate updateCurrencyRate(Long id, CurrencyRate rate) {
        CurrencyRate existing = getCurrencyRateById(id);
        String oldValue = existing.toString();
        
        if (rate.getFromCurrency() != null) existing.setFromCurrency(rate.getFromCurrency());
        if (rate.getToCurrency() != null) existing.setToCurrency(rate.getToCurrency());
        if (rate.getExchangeRate() != null) existing.setExchangeRate(rate.getExchangeRate());
        if (rate.getEffectiveDate() != null) existing.setEffectiveDate(rate.getEffectiveDate());
        
        CurrencyRate updated = currencyRateRepository.save(existing);
        createAuditLog("FEE_MANAGEMENT", "CurrencyRate", id, 
                      AuditLog.Action.UPDATE, oldValue, updated.toString(), null);
        return updated;
    }

    @Override
    public void deleteCurrencyRate(Long id) {
        CurrencyRate existing = getCurrencyRateById(id);
        currencyRateRepository.deleteById(id);
        createAuditLog("FEE_MANAGEMENT", "CurrencyRate", id, 
                      AuditLog.Action.DELETE, existing.toString(), null, null);
    }

    @Override
    public BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency, LocalDate date) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }
        
        CurrencyRate rate = currencyRateRepository.findLatestRate(fromCurrency, toCurrency, date)
                .orElseThrow(() -> new RuntimeException("Currency rate not found for " + 
                                                        fromCurrency + " to " + toCurrency));
        
        return amount.multiply(rate.getExchangeRate()).setScale(2, RoundingMode.HALF_UP);
    }

    // ============================================
    // 17. AUDIT LOGS (AUTO-GENERATED - READ ONLY)
    // ============================================
    
    @Override
    public AuditLog getAuditLogById(Long id) {
        return auditLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AuditLog not found with id: " + id));
    }

    @Override
    public List<AuditLog> getAllAuditLogs() {
        return auditLogRepository.findAll();
    }

    @Override
    public List<AuditLog> getAuditLogsByModule(String module) {
        return auditLogRepository.findByModule(module);
    }

    @Override
    public List<AuditLog> getAuditLogsByEntity(String entityName, Long entityId) {
        return auditLogRepository.findByEntityNameAndEntityId(entityName, entityId);
    }

    // Helper method to create audit logs
    private void createAuditLog(String module, String entityName, Long entityId, 
                               AuditLog.Action action, String oldValue, String newValue, Long performedBy) {
        AuditLog log = new AuditLog();
        log.setModule(module);
        log.setEntityName(entityName);
        log.setEntityId(entityId);
        log.setAction(action);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        log.setPerformedBy(performedBy);
        log.setIpAddress("127.0.0.1"); // TODO: Get actual IP from request
        
        auditLogRepository.save(log);
    }

    // ============================================
    // 18. CERTIFICATE BLOCK LIST CRUD
    // ============================================
    
    @Override
    public CertificateBlockList createCertificateBlock(CertificateBlockList block) {
        CertificateBlockList saved = certificateBlockListRepository.save(block);
        createAuditLog("FEE_MANAGEMENT", "CertificateBlockList", saved.getId(), 
                      AuditLog.Action.CREATE, null, block.toString(), null);
        return saved;
    }

    @Override
    public CertificateBlockList getCertificateBlockById(Long id) {
        return certificateBlockListRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CertificateBlockList not found with id: " + id));
    }

    @Override
    public List<CertificateBlockList> getAllCertificateBlocks() {
        return certificateBlockListRepository.findAll();
    }

    @Override
    public CertificateBlockList updateCertificateBlock(Long id, CertificateBlockList block) {
        CertificateBlockList existing = getCertificateBlockById(id);
        String oldValue = existing.toString();
        
        if (block.getUserId() != null) existing.setUserId(block.getUserId());
        if (block.getBlockedReason() != null) existing.setBlockedReason(block.getBlockedReason());
        if (block.getPendingAmount() != null) existing.setPendingAmount(block.getPendingAmount());
        if (block.getBlockedBy() != null) existing.setBlockedBy(block.getBlockedBy());
        if (block.getBlockedDate() != null) existing.setBlockedDate(block.getBlockedDate());
        if (block.getIsActive() != null) existing.setIsActive(block.getIsActive());
        
        CertificateBlockList updated = certificateBlockListRepository.save(existing);
        createAuditLog("FEE_MANAGEMENT", "CertificateBlockList", id, 
                      AuditLog.Action.UPDATE, oldValue, updated.toString(), null);
        return updated;
    }

    @Override
    public void deleteCertificateBlock(Long id) {
        CertificateBlockList existing = getCertificateBlockById(id);
        certificateBlockListRepository.deleteById(id);
        createAuditLog("FEE_MANAGEMENT", "CertificateBlockList", id, 
                      AuditLog.Action.DELETE, existing.toString(), null, null);
    }

    @Override
    public boolean canIssueCertificate(Long userId) {
        return !certificateBlockListRepository.existsByUserIdAndIsActive(userId, true);
    }

    @Override
    public CertificateBlockList blockCertificate(Long userId, String reason, Long blockedBy) {
        // 1. Check if already blocked (Existing logic)
        if (!canIssueCertificate(userId)) {
            throw new RuntimeException("Certificate already blocked for user: " + userId);
        }
        
        // 2. NEW LOGIC: Auto-Calculate Total Pending Amount from DB
        BigDecimal totalPending = BigDecimal.ZERO;
        
        // This helper method exists in your code (Line ~320)
        List<StudentFeeAllocation> allocations = getFeeAllocationsByUserId(userId);
        
        for (StudentFeeAllocation allocation : allocations) {
            // We sum up the 'RemainingAmount' from all allocations
            if (allocation.getRemainingAmount() != null) {
                totalPending = totalPending.add(allocation.getRemainingAmount());
            }
        }
        
        // 3. Create the Block Record using the calculated amount
        CertificateBlockList block = new CertificateBlockList();
        block.setUserId(userId);
        block.setPendingAmount(totalPending); // <--- Auto-filled here
        block.setBlockedReason(reason);
        block.setBlockedBy(blockedBy);
        block.setBlockedDate(LocalDate.now());
        block.setIsActive(true);
        
        return createCertificateBlock(block);
    }
    @Override
    public void unblockCertificate(Long userId) {
        // Find the active block for this user
        certificateBlockListRepository.findByUserIdAndIsActive(userId, true)
                .ifPresent(block -> {
                    // Mark it as inactive (unblocked)
                    block.setIsActive(false);
                    certificateBlockListRepository.save(block);
                    
                    // Audit log for unblocking
                    createAuditLog("FEE_MANAGEMENT", "CertificateBlockList", block.getId(), 
                                  AuditLog.Action.UPDATE, "ACTIVE", "INACTIVE (Unblocked)", null);
                });
    }

    // ============================================
    // REPORTS & ANALYTICS
    // ============================================
    
    @Override
    public Map<String, Object> getStudentFeeReport(Long userId) {
        Map<String, Object> report = new HashMap<>();
        
        List<StudentFeeAllocation> allocations = getFeeAllocationsByUserId(userId);
        
        BigDecimal totalFee = allocations.stream()
                .map(StudentFeeAllocation::getOriginalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalDiscount = allocations.stream()
                .map(StudentFeeAllocation::getTotalDiscount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalPayable = allocations.stream()
                .map(StudentFeeAllocation::getPayableAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalPaid = BigDecimal.ZERO;
        BigDecimal totalPending = BigDecimal.ZERO;
        
        for (StudentFeeAllocation allocation : allocations) {
            BigDecimal paid = studentFeePaymentRepository.getTotalPaidByAllocationId(allocation.getId());
            totalPaid = totalPaid.add(paid != null ? paid : BigDecimal.ZERO);
            totalPending = totalPending.add(allocation.getRemainingAmount());
        }
        
        // Get late fees
        BigDecimal totalLateFee = BigDecimal.ZERO;
        for (StudentFeeAllocation allocation : allocations) {
            List<StudentInstallmentPlan> installments = getInstallmentPlansByAllocationId(allocation.getId());
            for (StudentInstallmentPlan installment : installments) {
                BigDecimal penalty = lateFeePenaltyRepository.getTotalPenaltyByInstallmentPlanId(installment.getId());
                totalLateFee = totalLateFee.add(penalty != null ? penalty : BigDecimal.ZERO);
            }
        }
        
        // Get attendance penalties
        BigDecimal totalAttendancePenalty = BigDecimal.ZERO;
        for (StudentFeeAllocation allocation : allocations) {
            BigDecimal penalty = attendancePenaltyRepository.getTotalPenaltyByAllocationId(allocation.getId());
            totalAttendancePenalty = totalAttendancePenalty.add(penalty != null ? penalty : BigDecimal.ZERO);
        }
        
        report.put("userId", userId);
        report.put("totalFee", totalFee);
        report.put("totalDiscount", totalDiscount);
        report.put("totalPayable", totalPayable);
        report.put("totalPaid", totalPaid);
        report.put("totalPending", totalPending);
        report.put("totalLateFee", totalLateFee);
        report.put("totalAttendancePenalty", totalAttendancePenalty);
        report.put("paymentStatus", totalPending.compareTo(BigDecimal.ZERO) == 0 ? "PAID" : 
                                   totalPaid.compareTo(BigDecimal.ZERO) > 0 ? "PARTIAL" : "DUE");
        report.put("allocations", allocations);
        
        return report;
    }

    @Override
    public Map<String, Object> getBatchFeeReport(Long batchId) {
        Map<String, Object> report = new HashMap<>();
        
        List<FeeStructure> structures = getFeeStructuresByBatch(batchId);
        
        BigDecimal totalExpected = BigDecimal.ZERO;
        BigDecimal totalCollected = BigDecimal.ZERO;
        BigDecimal totalPending = BigDecimal.ZERO;
        int totalStudents = 0;
        
        for (FeeStructure structure : structures) {
            List<StudentFeeAllocation> allocations = studentFeeAllocationRepository.findAll().stream()
                    .filter(a -> a.getFeeStructureId().equals(structure.getId()))
                    .collect(Collectors.toList());
            
            totalStudents += allocations.size();
            
            for (StudentFeeAllocation allocation : allocations) {
                totalExpected = totalExpected.add(allocation.getPayableAmount());
                BigDecimal paid = studentFeePaymentRepository.getTotalPaidByAllocationId(allocation.getId());
                totalCollected = totalCollected.add(paid != null ? paid : BigDecimal.ZERO);
                totalPending = totalPending.add(allocation.getRemainingAmount());
            }
        }
        
        report.put("batchId", batchId);
        report.put("totalStudents", totalStudents);
        report.put("totalExpected", totalExpected);
        report.put("totalCollected", totalCollected);
        report.put("totalPending", totalPending);
        report.put("collectionPercentage", totalExpected.compareTo(BigDecimal.ZERO) > 0 ? 
                   totalCollected.multiply(BigDecimal.valueOf(100)).divide(totalExpected, 2, RoundingMode.HALF_UP) : 
                   BigDecimal.ZERO);
        
        return report;
    }

    @Override
    public Map<String, Object> getCourseFeeReport(Long courseId) {
        Map<String, Object> report = new HashMap<>();
        
        List<FeeStructure> structures = getFeeStructuresByCourse(courseId);
        
        BigDecimal totalExpected = BigDecimal.ZERO;
        BigDecimal totalCollected = BigDecimal.ZERO;
        BigDecimal totalPending = BigDecimal.ZERO;
        int totalStudents = 0;
        
        for (FeeStructure structure : structures) {
            List<StudentFeeAllocation> allocations = studentFeeAllocationRepository.findAll().stream()
                    .filter(a -> a.getFeeStructureId().equals(structure.getId()))
                    .collect(Collectors.toList());
            
            totalStudents += allocations.size();
            
            for (StudentFeeAllocation allocation : allocations) {
                totalExpected = totalExpected.add(allocation.getPayableAmount());
                BigDecimal paid = studentFeePaymentRepository.getTotalPaidByAllocationId(allocation.getId());
                totalCollected = totalCollected.add(paid != null ? paid : BigDecimal.ZERO);
                totalPending = totalPending.add(allocation.getRemainingAmount());
            }
        }
        
        report.put("courseId", courseId);
        report.put("totalStudents", totalStudents);
        report.put("totalExpected", totalExpected);
        report.put("totalCollected", totalCollected);
        report.put("totalPending", totalPending);
        report.put("collectionPercentage", totalExpected.compareTo(BigDecimal.ZERO) > 0 ? 
                   totalCollected.multiply(BigDecimal.valueOf(100)).divide(totalExpected, 2, RoundingMode.HALF_UP) : 
                   BigDecimal.ZERO);
        
        return report;
    }

    @Override
    public Map<String, Object> getMonthlyRevenueReport(int year, int month) {
        LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);
        
        List<StudentFeePayment> payments = studentFeePaymentRepository
                .findSuccessfulPaymentsBetween(startDate, endDate);
        
        BigDecimal totalCollected = payments.stream()
                .map(StudentFeePayment::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, Object> report = new HashMap<>();
        report.put("year", year);
        report.put("month", month);
        report.put("totalCollected", totalCollected);
        report.put("totalTransactions", payments.size());
        report.put("payments", payments);
        
        return report;
    }

    @Override
    public Map<String, Object> getQuarterlyRevenueReport(int year, int quarter) {
        int startMonth = (quarter - 1) * 3 + 1;
        LocalDateTime startDate = LocalDateTime.of(year, startMonth, 1, 0, 0);
        LocalDateTime endDate = startDate.plusMonths(3).minusSeconds(1);
        
        List<StudentFeePayment> payments = studentFeePaymentRepository
                .findSuccessfulPaymentsBetween(startDate, endDate);
        
        BigDecimal totalCollected = payments.stream()
                .map(StudentFeePayment::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, Object> report = new HashMap<>();
        report.put("year", year);
        report.put("quarter", quarter);
        report.put("totalCollected", totalCollected);
        report.put("totalTransactions", payments.size());
        
        return report;
    }

    @Override
    public Map<String, Object> getYearlyRevenueReport(int year) {
        LocalDateTime startDate = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(year, 12, 31, 23, 59, 59);
        
        List<StudentFeePayment> payments = studentFeePaymentRepository
                .findSuccessfulPaymentsBetween(startDate, endDate);
        
        BigDecimal totalCollected = payments.stream()
                .map(StudentFeePayment::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, Object> report = new HashMap<>();
        report.put("year", year);
        report.put("totalCollected", totalCollected);
        report.put("totalTransactions", payments.size());
        
        return report;
    }

    @Override
    public Map<String, Object> getOverallFinancialSummary() {
        List<StudentFeeAllocation> allAllocations = getAllFeeAllocations();
        
        BigDecimal totalExpected = allAllocations.stream()
                .map(StudentFeeAllocation::getPayableAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalCollected = BigDecimal.ZERO;
        BigDecimal totalPending = BigDecimal.ZERO;
        
        for (StudentFeeAllocation allocation : allAllocations) {
            BigDecimal paid = studentFeePaymentRepository.getTotalPaidByAllocationId(allocation.getId());
            totalCollected = totalCollected.add(paid != null ? paid : BigDecimal.ZERO);
            totalPending = totalPending.add(allocation.getRemainingAmount());
        }
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalExpected", totalExpected);
        summary.put("totalCollected", totalCollected);
        summary.put("totalPending", totalPending);
        summary.put("collectionPercentage", totalExpected.compareTo(BigDecimal.ZERO) > 0 ? 
                   totalCollected.multiply(BigDecimal.valueOf(100)).divide(totalExpected, 2, RoundingMode.HALF_UP) : 
                   BigDecimal.ZERO);
        summary.put("totalStudents", allAllocations.size());
        
        return summary;
    }

    @Override
    public List<StudentFeePayment> getPaymentHistory(Long userId) {
        List<StudentFeeAllocation> allocations = getFeeAllocationsByUserId(userId);
        List<StudentFeePayment> allPayments = new ArrayList<>();
        
        for (StudentFeeAllocation allocation : allocations) {
            allPayments.addAll(getPaymentsByAllocationId(allocation.getId()));
        }
        
        // Sort by payment date descending
        allPayments.sort(Comparator.comparing(StudentFeePayment::getPaymentDate).reversed());
        
        return allPayments;
    }
}