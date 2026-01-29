package com.graphy.lms.service.impl;

import com.graphy.lms.entity.*;
import com.razorpay.RazorpayClient;
import com.razorpay.Order;
import com.razorpay.Utils;
import org.json.JSONObject;
import com.razorpay.RazorpayException;
import com.graphy.lms.service.*;
import com.graphy.lms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

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
    
    @Autowired
    private EmailService emailService;
    @Autowired
    private PDFReceiptService pdfReceiptService;
   
    // REMOVED: private UserRepository userRepository; (To fix "User cannot be resolved" error)

    @Autowired
    private RazorpayClient razorpayClient;

    @org.springframework.beans.factory.annotation.Value("${razorpay.key.secret}")
    private String razorpaySecret;

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
    @Transactional // Ensures Parent and Children are saved together safely
    public FeeStructure createFeeStructure(FeeStructure feeStructure) {
        
        // 🔴 NEW LOGIC: Handle Fee Components (Tuition, Lab, Exam Breakdown)
        if (feeStructure.getComponents() != null && !feeStructure.getComponents().isEmpty()) {
            BigDecimal calculatedTotal = BigDecimal.ZERO;
            
            for (FeeStructureComponent component : feeStructure.getComponents()) {
                // 1. Link the Child (Component) back to the Parent (Structure)
                // This is crucial for the Foreign Key to be saved correctly in DB
                component.setFeeStructure(feeStructure);
                
                // 2. Sum up the amounts
                if (component.getAmount() != null) {
                    calculatedTotal = calculatedTotal.add(component.getAmount());
                }
            }
            
            // 3. Override the "Total Amount" with the calculated sum
            // This ensures the Net Amount is always mathematically correct
            feeStructure.setTotalAmount(calculatedTotal);
        }

        // Existing Save Logic (CascadeType.ALL will automatically save the components now)
        FeeStructure saved = feeStructureRepository.save(feeStructure);
        
        // Audit Log
        // Note: passing "null" for userId as per your snippet, ideally pass dynamic user ID
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
        // 1. VALIDATION: CHECK DUPLICATES
        boolean exists = studentFeeAllocationRepository.findByUserId(allocation.getUserId())
                .stream()
                .anyMatch(a -> a.getFeeStructureId().equals(allocation.getFeeStructureId()));

        if (exists) {
            throw new RuntimeException("DUPLICATE: This fee structure is already assigned to the student.");
        }

        // 2. EXISTING LOGIC (Calculate Amounts)
        if (allocation.getFeeStructureId() != null && allocation.getUserId() != null) {
            FeeStructure structure = getFeeStructureById(allocation.getFeeStructureId());
            
            BigDecimal baseAmount = (allocation.getOriginalAmount() != null) 
                    ? allocation.getOriginalAmount() 
                    : structure.getTotalAmount();
            allocation.setOriginalAmount(baseAmount);

            List<FeeDiscount> discounts = feeDiscountRepository.findByUserIdAndFeeStructureId(
                    allocation.getUserId(), allocation.getFeeStructureId());
            
            BigDecimal totalDiscount = BigDecimal.ZERO;
            for (FeeDiscount discount : discounts) {
                if (discount.getIsActive() != null && discount.getIsActive()) {
                     if (discount.getDiscountType() == FeeDiscount.DiscountType.PERCENTAGE) {
                        BigDecimal dAmount = baseAmount.multiply(discount.getDiscountValue())
                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                        totalDiscount = totalDiscount.add(dAmount);
                    } else {
                        totalDiscount = totalDiscount.add(discount.getDiscountValue());
                    }
                }
            }

            BigDecimal payableAmount = baseAmount.subtract(totalDiscount).max(BigDecimal.ZERO);
            allocation.setTotalDiscount(totalDiscount);
            allocation.setPayableAmount(payableAmount);
            
            BigDecimal plannedAdvance = allocation.getAdvancePayment() != null ? 
                                       allocation.getAdvancePayment() : BigDecimal.ZERO;
            allocation.setRemainingAmount(payableAmount.subtract(plannedAdvance));
            
            allocation.setCurrency(structure.getCurrency());
        }
        
        allocation.setAllocationDate(LocalDate.now());
        allocation.setStatus(StudentFeeAllocation.AllocationStatus.ACTIVE);
        
        // 3. SAVE
        StudentFeeAllocation saved = studentFeeAllocationRepository.save(allocation);

        // 🔴 4. EMAIL TRIGGER "ON CREATION" (DYNAMIC)
        try {
            FeeStructure structure = getFeeStructureById(saved.getFeeStructureId());
            FeeType type = getFeeTypeById(structure.getFeeTypeId());

            if (Boolean.TRUE.equals(structure.getTriggerOnCreation())) {
                
                // ✅ FIX: Get email directly from Request Body (No Hardcoding)
                String targetEmail = allocation.getStudentEmail();
                
                // Fallback validation
                if (targetEmail == null || targetEmail.isEmpty()) {
                    logger.warn("Skipping 'On Creation' email: No studentEmail provided in request.");
                } else {
                    String studentName = "Student " + saved.getUserId(); // Or pass studentName in @Transient too if needed

                    emailService.sendFeeAssignedEmail(
                        targetEmail,    // Using the dynamic input
                        studentName,
                        type.getName(),
                        saved.getPayableAmount(),
                        LocalDate.now().plusDays(30)
                    );
                }
            }
        } catch (Exception e) {
            logger.error("Trigger Error: Failed to send creation email.", e);
        }

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
    @Transactional
    // 1. REMOVED Long alternativeId from parameters
    public List<StudentInstallmentPlan> createInstallmentsForStudent(Long allocationId, List<StudentInstallmentPlan> installmentDetails) {
        
        StudentFeeAllocation allocation = getFeeAllocationById(allocationId);

        // 2. NEW VALIDATION: Check total amount instead of counting items
        BigDecimal totalPlannedAmount = installmentDetails.stream()
                .map(StudentInstallmentPlan::getInstallmentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Ensure the sum of installments equals what the student owes
        // Using compareTo for safe BigDecimal comparison (0 means equal)
        if (totalPlannedAmount.compareTo(allocation.getPayableAmount()) != 0) {
             throw new RuntimeException("Invalid Plan: The sum of installments (" + totalPlannedAmount + 
                     ") does not match the total payable amount (" + allocation.getPayableAmount() + ")");
        }

        List<StudentInstallmentPlan> savedPlans = new ArrayList<>();
        int installmentNumber = 1;

        for (StudentInstallmentPlan detail : installmentDetails) {
            StudentInstallmentPlan plan = new StudentInstallmentPlan();
            plan.setStudentFeeAllocationId(allocationId);
            // 3. NO LONGER SETTING alternativeId
            // plan.setPaymentAlternativeId(null); 
            
            plan.setInstallmentNumber(installmentNumber++);
            plan.setInstallmentAmount(detail.getInstallmentAmount());
            plan.setDueDate(detail.getDueDate()); // Frontend provides the date
            plan.setStatus(StudentInstallmentPlan.InstallmentStatus.PENDING);
            
            savedPlans.add(studentInstallmentPlanRepository.save(plan));
        }
        
        return savedPlans;
    }

    @Override
    @Transactional
    public List<StudentInstallmentPlan> resetInstallments(Long allocationId, Long alternativeId, 
                                                        List<Map<String, Object>> newInstallmentDetails) {
        
        // 1. Template Validation
        if (alternativeId != null) {
            PaymentAlternative alternative = getPaymentAlternativeById(alternativeId);
            if (newInstallmentDetails.size() != alternative.getNumberOfInstallments()) {
                 throw new RuntimeException("Mismatch: Template requires " + alternative.getNumberOfInstallments() + " installments.");
            }
        }

        List<StudentInstallmentPlan> existingInstallments = getInstallmentPlansByAllocationId(allocationId);
        List<StudentInstallmentPlan> keptInstallments = new ArrayList<>();
        BigDecimal totalKeptAmount = BigDecimal.ZERO;

        // 2. HANDLE HISTORY
        for (StudentInstallmentPlan plan : existingInstallments) {
            // Because of the new "Rollover" logic, partial plans become PAID (shrunk). 
            // So checking for PAID covers both Fully Paid and Rolled-Over Partial payments.
            if (plan.getStatus() == StudentInstallmentPlan.InstallmentStatus.PAID) {
                keptInstallments.add(plan);
                totalKeptAmount = totalKeptAmount.add(plan.getInstallmentAmount());
            } else if (plan.getStatus() == StudentInstallmentPlan.InstallmentStatus.PARTIALLY_PAID) {
                // Handle edge case (Last installment partial)
                BigDecimal amountPaidSoFar = plan.getPaidAmount();
                plan.setInstallmentAmount(amountPaidSoFar);
                plan.setStatus(StudentInstallmentPlan.InstallmentStatus.PAID);
                studentInstallmentPlanRepository.save(plan);
                
                keptInstallments.add(plan);
                totalKeptAmount = totalKeptAmount.add(amountPaidSoFar);
            } else {
                // Delete Pending/Rolled-up future installments
                studentInstallmentPlanRepository.delete(plan);
            }
        }

        // 3. CALCULATE TRUE REMAINING DEBT
        // We use the Allocation Total as the Source of Truth
        StudentFeeAllocation allocation = getFeeAllocationById(allocationId);
        BigDecimal totalPayable = allocation.getPayableAmount();
        BigDecimal advancePayment = allocation.getAdvancePayment() != null ? 
                                   allocation.getAdvancePayment() : BigDecimal.ZERO;
        
        // Target = Total Fee - Advance - (What was collected so far)
        BigDecimal targetForNewPlan = totalPayable.subtract(advancePayment).subtract(totalKeptAmount);
        
        // 4. VALIDATE NEW PLAN TOTAL
        BigDecimal newPlanTotal = BigDecimal.ZERO;
        for (Map<String, Object> detail : newInstallmentDetails) {
            newPlanTotal = newPlanTotal.add(new BigDecimal(detail.get("dueAmount").toString()));
        }

        if (newPlanTotal.subtract(targetForNewPlan).abs().compareTo(BigDecimal.ONE) > 0) {
             throw new RuntimeException("Mismatch! You need to schedule exactly " + targetForNewPlan + 
                                       ". Your new plan totals " + newPlanTotal);
        }

        // 5. CREATE NEW INSTALLMENTS
        List<StudentInstallmentPlan> finalPlans = new ArrayList<>();
        finalPlans.addAll(keptInstallments);

        for (Map<String, Object> detail : newInstallmentDetails) {
            StudentInstallmentPlan plan = new StudentInstallmentPlan();
            plan.setStudentFeeAllocationId(allocationId);
            plan.setPaymentAlternativeId(alternativeId);
            
            plan.setInstallmentNumber((Integer) detail.get("installmentNumber"));
            plan.setDueDate(LocalDate.parse(detail.get("dueDate").toString())); 
            plan.setInstallmentAmount(new BigDecimal(detail.get("dueAmount").toString()));
            plan.setStatus(StudentInstallmentPlan.InstallmentStatus.PENDING);
            
            finalPlans.add(studentInstallmentPlanRepository.save(plan));
        }
        
        // Re-sort for clean return
        finalPlans.sort(Comparator.comparing(StudentInstallmentPlan::getDueDate));
        return finalPlans;
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
    @Transactional
    public StudentFeePayment processOnlinePayment(Long allocationId, Long installmentPlanId, 
                                                  BigDecimal amount, String paymentMode, 
                                                  String transactionRef, String gatewayResponse,
                                                  String screenshotUrl, String studentName,
                                                  String studentEmail) {
        
        logger.info("Processing online payment. AllocID: {}, Amount: {}", allocationId, amount);
        
        StudentFeeAllocation allocation = getFeeAllocationById(allocationId);

        // 🔴 FIX: FETCH DYNAMIC CURRENCY
        String currencyCode = (allocation.getCurrency() != null && !allocation.getCurrency().isEmpty()) 
                            ? allocation.getCurrency() 
                            : "INR"; 

        // 1. Save Payment
        StudentFeePayment payment = new StudentFeePayment();
        payment.setStudentFeeAllocationId(allocationId);
        payment.setStudentInstallmentPlanId(installmentPlanId);
        payment.setPaidAmount(amount);
        payment.setCurrency(currencyCode); // <--- 🔴 SAVING TO DB
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMode(StudentFeePayment.PaymentMode.valueOf(paymentMode));
        payment.setPaymentStatus(StudentFeePayment.PaymentStatus.SUCCESS);
        payment.setTransactionReference(transactionRef);
        payment.setGatewayResponse(gatewayResponse);
        payment.setScreenshotUrl(screenshotUrl);
        
        StudentFeePayment savedPayment = studentFeePaymentRepository.save(payment);
        generateReceipt(savedPayment.getId(), studentEmail);

        // 2. Update Allocation Logic (Roadmap)
        if (installmentPlanId != null) {
            BigDecimal newRemaining = allocation.getRemainingAmount().subtract(amount);
            allocation.setRemainingAmount(newRemaining.max(BigDecimal.ZERO));
            
            StudentInstallmentPlan plan = getInstallmentPlanById(installmentPlanId);
            plan.setStatus(StudentInstallmentPlan.InstallmentStatus.PAID);
            studentInstallmentPlanRepository.save(plan);
        } 
        
        if (allocation.getRemainingAmount().compareTo(BigDecimal.ZERO) == 0 && installmentPlanId != null) {
             allocation.setStatus(StudentFeeAllocation.AllocationStatus.ACTIVE); 
        }
        studentFeeAllocationRepository.save(allocation);

        // 3. Generate Receipt & Email
        generateReceipt(savedPayment.getId());

        try {
            String receiptNumber = "REC-" + savedPayment.getId();
            
            // A. Generate PDF (Pass Dynamic Currency)
            byte[] pdfBytes = pdfReceiptService.generateReceiptPDF(
                    receiptNumber,
                    studentName,
                    allocation.getUserId(), 
                    savedPayment.getPaidAmount(),
                    currencyCode, // <--- 🔴 PASSING DYNAMIC CURRENCY
                    savedPayment.getPaymentMode().toString(),
                    savedPayment.getTransactionReference(),
                    savedPayment.getPaymentDate()
            );
            
            // B. Send Email (Pass Dynamic Currency)
            emailService.sendPaymentSuccessEmail(
                    studentEmail,
                    studentName,
                    receiptNumber,
                    savedPayment.getPaidAmount(),
                    currencyCode, // <--- 🔴 PASSING DYNAMIC CURRENCY
                    pdfBytes
            );
            
            logger.info("✅ Receipt Email Sent Successfully to {}", studentEmail);

        } catch (Exception e) {
            logger.error("❌ Failed to send receipt email: {}", e.getMessage());
        }

        return savedPayment;
    }
 // Inside FeeServiceImpl.java

    @Override
    @Transactional
    public StudentFeePayment recordManualPayment(Long allocationId, Long installmentPlanId, 
                                                 BigDecimal amount, String paymentMode, 
                                                 String transactionRef, String screenshotUrl,
                                                 Long recordedBy,
                                                 String studentName, String studentEmail) {
        
        StudentFeeAllocation allocation = getFeeAllocationById(allocationId);

        // 🔴 FIX: FETCH DYNAMIC CURRENCY
        String currencyCode = (allocation.getCurrency() != null && !allocation.getCurrency().isEmpty()) 
                            ? allocation.getCurrency() 
                            : "INR"; 

        // 1. Save Payment
        StudentFeePayment payment = new StudentFeePayment();
        payment.setStudentFeeAllocationId(allocationId);
        payment.setStudentInstallmentPlanId(installmentPlanId);
        payment.setPaidAmount(amount);
        payment.setCurrency(currencyCode); // <--- 🔴 SAVING TO DB
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMode(StudentFeePayment.PaymentMode.valueOf(paymentMode));
        payment.setPaymentStatus(StudentFeePayment.PaymentStatus.SUCCESS);
        payment.setTransactionReference(transactionRef);
        payment.setScreenshotUrl(screenshotUrl);
        payment.setRecordedBy(recordedBy);
        
        StudentFeePayment savedPayment = studentFeePaymentRepository.save(payment);
        
        generateReceipt(savedPayment.getId(), studentEmail);

        // 2. Update Allocation (Roadmap Logic)
        if (installmentPlanId != null) {
            BigDecimal newRemaining = allocation.getRemainingAmount().subtract(amount);
            allocation.setRemainingAmount(newRemaining.max(BigDecimal.ZERO));
            
            StudentInstallmentPlan plan = getInstallmentPlanById(installmentPlanId);
            updateInstallmentStatus(plan.getId(), amount); 
        } 

        if (allocation.getRemainingAmount().compareTo(BigDecimal.ZERO) == 0 && installmentPlanId != null) {
            allocation.setStatus(StudentFeeAllocation.AllocationStatus.ACTIVE); 
        }
        studentFeeAllocationRepository.save(allocation);

        // 3. Generate Receipt & Email
        generateReceipt(savedPayment.getId());

        if (studentEmail != null && studentName != null) {
            try {
                String receiptNumber = "REC-" + savedPayment.getId();
                
                // A. Generate PDF
                byte[] pdfBytes = pdfReceiptService.generateReceiptPDF(
                        receiptNumber,
                        studentName,
                        allocation.getUserId(), 
                        savedPayment.getPaidAmount(),
                        currencyCode, // <--- 🔴 PASSING DYNAMIC CURRENCY
                        savedPayment.getPaymentMode().toString(),
                        savedPayment.getTransactionReference(),
                        savedPayment.getPaymentDate()
                );
                
                // B. Send Email
                emailService.sendPaymentSuccessEmail(
                        studentEmail,
                        studentName,
                        receiptNumber,
                        savedPayment.getPaidAmount(),
                        currencyCode, // <--- 🔴 PASSING DYNAMIC CURRENCY
                        pdfBytes
                );
                logger.info("✅ Manual Payment Receipt Email Sent to {}", studentEmail);

            } catch (Exception e) {
                logger.error("❌ Failed to send manual receipt email: {}", e.getMessage());
            }
        }
        
        return savedPayment;
    }
    @Override
    public void updateInstallmentStatus(Long installmentPlanId, BigDecimal paidAmount) {
        StudentInstallmentPlan current = getInstallmentPlanById(installmentPlanId);

        // 1. Update Paid Amount
        BigDecimal previousPaid = current.getPaidAmount() != null ? current.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal totalPaid = previousPaid.add(paidAmount);
        current.setPaidAmount(totalPaid);

        // 2. Logic: Fully Paid vs Partial (Rollover)
        if (totalPaid.compareTo(current.getInstallmentAmount()) >= 0) {
            // Case A: Fully Paid
            current.setStatus(StudentInstallmentPlan.InstallmentStatus.PAID);
            studentInstallmentPlanRepository.save(current);
        } else {
            // Case B: PARTIAL PAYMENT -> AUTOMATIC ROLLOVER
            
            // i. Find the Next Installment
            List<StudentInstallmentPlan> allPlans = getInstallmentPlansByAllocationId(current.getStudentFeeAllocationId());
            // Sort to ensure we find the immediate next one
            allPlans.sort(Comparator.comparing(StudentInstallmentPlan::getInstallmentNumber));
            
            StudentInstallmentPlan nextPlan = allPlans.stream()
                    .filter(p -> p.getInstallmentNumber() > current.getInstallmentNumber())
                    // FIX: Removed .filter(p -> p.getStatus() != CANCELLED) to prevent error
                    .findFirst()
                    .orElse(null);

            if (nextPlan != null) {
                // ii. Calculate Remaining Balance
                BigDecimal remaining = current.getInstallmentAmount().subtract(totalPaid);
                
                // iii. Move Debt to Next Plan
                BigDecimal newNextAmount = nextPlan.getInstallmentAmount().add(remaining);
                nextPlan.setInstallmentAmount(newNextAmount);
                studentInstallmentPlanRepository.save(nextPlan);
                
                // iv. Close Current Plan (Shrink it to match what was paid)
                current.setInstallmentAmount(totalPaid);
                current.setStatus(StudentInstallmentPlan.InstallmentStatus.PAID);
                studentInstallmentPlanRepository.save(current);
                
                System.out.println("Rollover: " + remaining + " moved from Inst #" + 
                                   current.getInstallmentNumber() + " to Inst #" + nextPlan.getInstallmentNumber());
            } else {
                // No next plan (Last Installment) -> Must stay Partially Paid
                current.setStatus(StudentInstallmentPlan.InstallmentStatus.PARTIALLY_PAID);
                studentInstallmentPlanRepository.save(current);
            }
        }
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
        applyLateFees(null); // Just delegate with null email
    }

    @Override
    public void applyLateFees(String manualEmail) {
        LocalDate today = LocalDate.now();
        List<StudentInstallmentPlan> overdueInstallments = getOverdueInstallments();
        List<LateFeeConfig> configs = lateFeeConfigRepository.findActiveConfigsForDate(today);
        
        for (StudentInstallmentPlan installment : overdueInstallments) {
            if (installment.getStatus() == StudentInstallmentPlan.InstallmentStatus.PAID) continue;
            
            LocalDate dueDate = installment.getDueDate();
            
            for (LateFeeConfig config : configs) {
                // 1. CALCULATE CALENDAR-SMART DATES
                LocalDate penaltyStartDate;
                LocalDate penaltyEndDate; // Defines the "window" for this specific penalty

                // Use Java's native date math logic
                switch (config.getPaymentSchedule()) {
                    case MONTHLY:
                        // e.g. Due Jan 15 + 1 Month = Feb 15 (Smart)
                        penaltyStartDate = dueDate.plusMonths(config.getPeriodCount());
                        // Window ends 1 month after start
                        penaltyEndDate = penaltyStartDate.plusMonths(1); 
                        break;
                        
                    case QUARTERLY:
                        // e.g. Due Jan 15 + 3 Months = Apr 15
                        penaltyStartDate = dueDate.plusMonths((long) config.getPeriodCount() * 3);
                        penaltyEndDate = penaltyStartDate.plusMonths(3);
                        break;
                        
                    case YEARLY:
                        // e.g. Feb 29 2024 + 1 Year = Feb 28 2025 (Smart)
                        penaltyStartDate = dueDate.plusYears(config.getPeriodCount());
                        penaltyEndDate = penaltyStartDate.plusYears(1);
                        break;
                        
                    default:
                        continue;
                }
                
                // 2. CHECK IF TODAY FALLS IN THE PENALTY WINDOW
                // Logic: Today must be ON or AFTER the start date, AND BEFORE the end date.
                boolean isPenaltyApplicable = !today.isBefore(penaltyStartDate) && today.isBefore(penaltyEndDate);
                
                if (isPenaltyApplicable) {
                     List<LateFeePenalty> existing = lateFeePenaltyRepository
                         .findByStudentInstallmentPlanId(installment.getId());
                     
                     // 3. PREVENT DUPLICATES (The "Once Per Window" Check)
                     // We check if we already have a penalty of THIS amount for THIS installment
                     // that was applied roughly in this same timeframe.
                     // A simple robust check: Have we applied *any* penalty for this specific rule?
                     // (Since 'existing' stores raw penalties, we check if one matches the amount/reason to avoid duplicates)
                     
                     boolean alreadyApplied = existing.stream()
                         .anyMatch(p -> p.getPenaltyAmount().compareTo(config.getPenaltyAmount()) == 0 
                                     && p.getPenaltyDate().isAfter(penaltyStartDate.minusDays(1)) 
                                     && p.getPenaltyDate().isBefore(penaltyEndDate));
                     
                     if (!alreadyApplied) { 
                        LateFeePenalty penalty = new LateFeePenalty();
                        penalty.setStudentInstallmentPlanId(installment.getId());
                        penalty.setPenaltyAmount(config.getPenaltyAmount());
                        penalty.setPenaltyDate(today);
                        
                        // Calculate exact days late for the reason
                        long actualDaysLate = ChronoUnit.DAYS.between(dueDate, today);
                        penalty.setReason("Overdue by " + actualDaysLate + " days (Calendar Rule)");
                        
                        createLateFeePenalty(penalty);
                        
                        // Update status
                        installment.setStatus(StudentInstallmentPlan.InstallmentStatus.OVERDUE);
                        studentInstallmentPlanRepository.save(installment);

                        // Send Email
                        StudentFeeAllocation allocation = getFeeAllocationById(installment.getStudentFeeAllocationId());
                        String targetEmail = (manualEmail != null && !manualEmail.isEmpty()) 
                                            ? manualEmail 
                                            : "student@example.com"; 
                        
                        sendOverdueWarningNotification(allocation.getUserId(), installment.getId(), targetEmail);
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
 // ... inside FeeServiceImpl class ...

    @Override
    @Transactional
    // 1. Update Signature to match Interface
    public List<ExamFeeLinkage> linkExamFeeInBulk(Long examId, BigDecimal amount, FeeService.BulkLinkType type, Long typeId) {
        List<ExamFeeLinkage> results = new ArrayList<>();
        List<StudentFeeAllocation> targetAllocations = new ArrayList<>();

        // 2. FIX LOGIC: Use Enum comparison (==)
        if (type == FeeService.BulkLinkType.BATCH) {
            
            List<FeeStructure> structures = getFeeStructuresByBatch(typeId);
            for (FeeStructure fs : structures) {
                targetAllocations.addAll(
                    studentFeeAllocationRepository.findAll().stream()
                        .filter(a -> a.getFeeStructureId().equals(fs.getId()))
                        .collect(Collectors.toList())
                );
            }
            
        } else if (type == FeeService.BulkLinkType.COURSE) {
            
            List<FeeStructure> structures = getFeeStructuresByCourse(typeId);
            for (FeeStructure fs : structures) {
                targetAllocations.addAll(
                    studentFeeAllocationRepository.findAll().stream()
                        .filter(a -> a.getFeeStructureId().equals(fs.getId()))
                        .collect(Collectors.toList())
                );
            }
        } 
        
        // (Note: No 'else' needed for invalid type because Enum prevents random strings)

        // 3. Apply Fee (Existing Logic)
        for (StudentFeeAllocation allocation : targetAllocations) {
            boolean alreadyLinked = examFeeLinkageRepository.findByStudentFeeAllocationId(allocation.getId())
                    .stream().anyMatch(e -> e.getExamId().equals(examId));
            
            if (alreadyLinked) continue;

            ExamFeeLinkage linkage = linkExamFeeToStudent(
                examId, 
                allocation.getUserId(), 
                allocation.getId(), 
                amount
            );
            results.add(linkage);
        }
        
        return results;
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
        
        // 1. Mark Refund Request as PROCESSED
        refund.setRefundStatus(FeeRefund.RefundStatus.PROCESSED);
        refund.setProcessedDate(LocalDate.now());
        refund.setRefundMode(refundMode);
        refund.setTransactionReference(transactionRef);
        
        // 2. Fetch Entities
        StudentFeeAllocation allocation = getFeeAllocationById(refund.getStudentFeeAllocationId());
        StudentFeePayment paymentToRefund = getPaymentById(refund.getStudentFeePaymentId());
        
        // 3. SETTLEMENT LOGIC (Forced Block / Final Settlement)
        
        // A. Calculate Total "Net" Paid so far (Adjusting for this refund)
        BigDecimal currentTotalPaid = studentFeePaymentRepository.getTotalPaidByAllocationId(allocation.getId());
        if (currentTotalPaid == null) currentTotalPaid = BigDecimal.ZERO;
        
        // 🔴 FIX: REMOVED '.add(advance)' 
        // currentTotalPaid already contains the Advance (if it was paid). 
        // We simply subtract the refund to find the "Final Net Collection".
        BigDecimal finalNetCollection = currentTotalPaid.subtract(refund.getRefundAmount());
        
        // B. Calculate Unpaid Exam Fees to keep as Pending (Optional but good logic)
        BigDecimal totalExamFees = BigDecimal.ZERO;
        List<ExamFeeLinkage> examLinks = examFeeLinkageRepository.findByUserId(allocation.getUserId());
        for (ExamFeeLinkage link : examLinks) {
            if (link.getStudentFeeAllocationId().equals(allocation.getId())) {
                totalExamFees = totalExamFees.add(link.getExamFeeAmount());
            }
        }
        
        // C. Update Allocation to "Settled"
        allocation.setPayableAmount(finalNetCollection); // Payable shrinks to match EXACTLY what we kept
        
        // Set Remaining to Exam Fees so debt remains visible (if any)
        allocation.setRemainingAmount(totalExamFees);  
        
        allocation.setStatus(StudentFeeAllocation.AllocationStatus.REFUNDED); // BLOCKED
        
        // 4. Update the Payment Record (Visual Correction for Reports)
        BigDecimal originalAmount = paymentToRefund.getPaidAmount();
        BigDecimal refundAmount = refund.getRefundAmount();
        BigDecimal newNetAmount = originalAmount.subtract(refundAmount);
        
        // Update the amount in the database
        paymentToRefund.setPaidAmount(newNetAmount);
        
        // Update Status
        if (newNetAmount.compareTo(BigDecimal.ZERO) > 0) {
            paymentToRefund.setPaymentStatus(StudentFeePayment.PaymentStatus.SUCCESS);
            String oldRef = paymentToRefund.getTransactionReference();
            if (oldRef != null && !oldRef.contains("(Partially Refunded)")) {
                paymentToRefund.setTransactionReference(oldRef + " (Partially Refunded " + refundAmount + ")");
            }
        } else {
            paymentToRefund.setPaymentStatus(StudentFeePayment.PaymentStatus.REFUNDED);
        }

        // Save Everything
        studentFeeAllocationRepository.save(allocation);
        studentFeePaymentRepository.save(paymentToRefund);
        
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
        return generateReceipt(paymentId, null);
    }

    @Override
    public FeeReceipt generateReceipt(Long paymentId, String manualEmail) {
        FeeReceipt existingReceipt = getReceiptByPaymentId(paymentId);
        if (existingReceipt != null) return existingReceipt;
        
        StudentFeePayment payment = getPaymentById(paymentId);
        StudentFeeAllocation allocation = getFeeAllocationById(payment.getStudentFeeAllocationId());
        
        String receiptNumber = "REC-" + LocalDate.now().getYear() + "-" + String.format("%06d", paymentId);
        
        FeeReceipt receipt = new FeeReceipt();
        receipt.setPaymentId(paymentId);
        receipt.setReceiptNumber(receiptNumber);
        receipt.setUserId(allocation.getUserId());
        receipt.setReceiptPdfUrl("/receipts/" + receiptNumber + ".pdf");
        
        FeeReceipt saved = feeReceiptRepository.save(receipt);
        
        String targetEmail = (manualEmail != null && !manualEmail.isEmpty()) 
                            ? manualEmail 
                            : "student@example.com"; 
        
        sendPaymentSuccessNotification(allocation.getUserId(), paymentId, targetEmail);
        
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
        // 1. Fetch Installment Details
        StudentInstallmentPlan installment = getInstallmentPlanById(installmentPlanId);
        
        // 2. Create Database Notification Record
        String message = "Reminder: Your installment of " + installment.getInstallmentAmount() + 
                        " is due on " + installment.getDueDate();
        
        PaymentNotification notification = new PaymentNotification();
        notification.setUserId(userId);
        notification.setNotificationType(PaymentNotification.NotificationType.DUE_REMINDER);
        notification.setMessage(message);
        notification.setEmail(email);
        notification.setSentAt(LocalDateTime.now());
        notification.setDeliveryStatus(PaymentNotification.DeliveryStatus.SENT);
        
        createNotification(notification); // Saves to DB
        
        // 3. 🔴 REAL EMAIL SENDING LOGIC
        // We use the specific method available in your EmailService class
        try {
            if (emailService != null) {
                emailService.sendDueReminderEmail(
                    email, 
                    "Student", // Placeholder for name (since User entity isn't fetched here)
                    installment.getInstallmentAmount(), 
                    installment.getDueDate()
                );
                System.out.println("✅ Real Email sent successfully to: " + email);
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
            // Update status to FAILED if email didn't go through
            notification.setDeliveryStatus(PaymentNotification.DeliveryStatus.FAILED);
            paymentNotificationRepository.save(notification);
        }
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
        processAutoDebit(null); // Delegate
    }
    @Override
    public void processAutoDebit(String manualEmail) {
        LocalDate today = LocalDate.now();
        int currentDay = today.getDayOfMonth();
        
        List<AutoDebitConfig> activeConfigs = autoDebitConfigRepository.findByIsActiveAndConsentGiven(true, true);
        
        for (AutoDebitConfig config : activeConfigs) {
            if (config.getAutoDebitDay() != null && config.getAutoDebitDay() == currentDay) {
                
                // FIX: Define 'installments' here so it is visible
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
                            .subtract(nextInstallment.getPaidAmount() != null ? nextInstallment.getPaidAmount() : BigDecimal.ZERO);
                    
                    String targetEmail = (manualEmail != null && !manualEmail.isEmpty()) 
                                       ? manualEmail 
                                       : "student@lms.com";

                    try {
                        String transactionRef = "AUTO-" + System.currentTimeMillis();
                        processOnlinePayment(
                            config.getStudentFeeAllocationId(),
                            nextInstallment.getId(),
                            amountToDebit,
                            "AUTO_DEBIT",
                            transactionRef,
                            "Auto-debit successful",
                            null, 
                            "AutoDebit User", 
                            targetEmail
                        );
                    } catch (Exception e) {
                        StudentFeeAllocation allocation = getFeeAllocationById(config.getStudentFeeAllocationId());
                        sendPaymentFailedNotification(allocation.getUserId(), targetEmail, "Auto-debit failed: " + e.getMessage());
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
        
        // 🔴 FIX: GET REAL IP ADDRESS
        String ipAddress = "SYSTEM"; // Default for Scheduler/Background tasks
        
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                
                // Check headers first (standard for production/proxies/load balancers)
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    // X-Forwarded-For can contain multiple IPs "client, proxy1, proxy2". We want the first one.
                    ipAddress = xForwardedFor.split(",")[0].trim();
                } else {
                    ipAddress = request.getRemoteAddr();
                }
            }
        } catch (Exception e) {
            // Fallback if anything goes wrong in fetching IP
            ipAddress = "UNKNOWN"; 
        }

        log.setIpAddress(ipAddress); // Sets real IP or "SYSTEM"
        
        auditLogRepository.save(log);
    }
    
    
    @Override
    public Map<String, Object> getDashboardAnalytics(int year) {
        Map<String, Object> analytics = new HashMap<>();
        
        // 1. OVERDUE AMOUNT (The missing metric)
        List<StudentInstallmentPlan> overduePlans = getOverdueInstallments();
        BigDecimal totalOverdue = overduePlans.stream()
                .map(p -> p.getInstallmentAmount().subtract(p.getPaidAmount() != null ? p.getPaidAmount() : BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        analytics.put("totalOverdue", totalOverdue);

        // 2. MONTHLY BAR CHART DATA (Group by Month)
        Map<String, BigDecimal> monthlyData = new LinkedHashMap<>();
        String[] months = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
        
        // Initialize with Zero
        for (String m : months) monthlyData.put(m, BigDecimal.ZERO);

        LocalDateTime start = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(year, 12, 31, 23, 59);
        
        List<StudentFeePayment> payments = studentFeePaymentRepository.findSuccessfulPaymentsBetween(start, end);
        
        for (StudentFeePayment p : payments) {
            String monthName = p.getPaymentDate().getMonth().name().substring(0, 3); // "JAN"
            monthlyData.put(monthName, monthlyData.get(monthName).add(p.getPaidAmount()));
        }
        analytics.put("monthlyChart", monthlyData);

        // 3. COURSE DISTRIBUTION (Donut Chart)
        // This requires fetching structures and grouping. 
        // For performance, we'll do a simple Java grouping (Production should use a custom JPQL Query)
        Map<Long, BigDecimal> courseRevenue = new HashMap<>();
        List<StudentFeeAllocation> allocations = getAllFeeAllocations();
        
        for (StudentFeeAllocation alloc : allocations) {
            // Get payments for this allocation
            BigDecimal paid = studentFeePaymentRepository.getTotalPaidByAllocationId(alloc.getId());
            if (paid == null || paid.compareTo(BigDecimal.ZERO) == 0) continue;

            FeeStructure structure = getFeeStructureById(alloc.getFeeStructureId());
            Long courseId = structure.getCourseId();
            
            courseRevenue.put(courseId, courseRevenue.getOrDefault(courseId, BigDecimal.ZERO).add(paid));
        }
        analytics.put("courseDistribution", courseRevenue);

        return analytics;
    }
    @Override
    public List<Map<String, Object>> getRecentTransactions() {
        List<Map<String, Object>> transactions = new ArrayList<>();

        // 1. FETCH RECENT PAYMENTS (Status: PAID)
        // We limit to 10 for performance
        List<StudentFeePayment> payments = studentFeePaymentRepository.findAll(); 
        // In production, use a custom query: "ORDER BY paymentDate DESC LIMIT 10"
        
        for (StudentFeePayment p : payments) {
            // Only show last 30 days or simply top 10
            if (transactions.size() >= 5) break; 

            StudentFeeAllocation allocation = getFeeAllocationById(p.getStudentFeeAllocationId());
            FeeStructure structure = getFeeStructureById(allocation.getFeeStructureId());
            FeeType type = getFeeTypeById(structure.getFeeTypeId());

            Map<String, Object> row = new HashMap<>();
            row.put("id", "TXN-" + p.getId());
            row.put("studentId", allocation.getUserId());
            row.put("studentName", "Student " + allocation.getUserId()); // ⚠️ TODO: Fetch Real Name from User Service
            row.put("feeType", type.getName()); // e.g., "Tuition Fee"
            row.put("date", p.getPaymentDate().toLocalDate());
            row.put("amount", p.getPaidAmount());
            row.put("status", "PAID");
            
            transactions.add(row);
        }

        // 2. FETCH PENDING INSTALLMENTS (Status: PENDING)
        // We want to show what is due soon or recently added
        List<StudentInstallmentPlan> installments = studentInstallmentPlanRepository.findAll();
        
        for (StudentInstallmentPlan plan : installments) {
            if (transactions.size() >= 10) break;
            
            if (plan.getStatus() == StudentInstallmentPlan.InstallmentStatus.PENDING) {
                StudentFeeAllocation allocation = getFeeAllocationById(plan.getStudentFeeAllocationId());
                FeeStructure structure = getFeeStructureById(allocation.getFeeStructureId());
                FeeType type = getFeeTypeById(structure.getFeeTypeId());

                Map<String, Object> row = new HashMap<>();
                row.put("id", "INST-" + plan.getId());
                row.put("studentId", allocation.getUserId());
                row.put("studentName", "Student " + allocation.getUserId()); // ⚠️ TODO: Fetch Real Name
                row.put("feeType", type.getName());
                row.put("date", plan.getDueDate());
                row.put("amount", plan.getInstallmentAmount());
                row.put("status", "PENDING");
                
                transactions.add(row);
            }
        }

        // 3. Sort by Date Descending (Newest First)
        transactions.sort((a, b) -> ((LocalDate) b.get("date")).compareTo((LocalDate) a.get("date")));
        
        // Return top 10
        return transactions.stream().limit(10).collect(Collectors.toList());
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
        // 1. Check if already blocked
        if (!canIssueCertificate(userId)) {
            throw new RuntimeException("Certificate already blocked for user: " + userId);
        }
        
        // 2. 🔴 FIX: Calculate TRUE PENDING from DB
        BigDecimal totalPending = BigDecimal.ZERO;
        
        List<StudentFeeAllocation> allocations = getFeeAllocationsByUserId(userId);
        for (StudentFeeAllocation allocation : allocations) {
            totalPending = totalPending.add(getTruePendingAmount(allocation));
        }
        
        // 3. Create the Block Record
        CertificateBlockList block = new CertificateBlockList();
        block.setUserId(userId);
        block.setPendingAmount(totalPending); // Now saves 120k instead of 100k
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
        
        BigDecimal totalFee = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal basePayable = BigDecimal.ZERO; 
        
        // 1. Calculate Base Amounts (Standard Fee)
        for (StudentFeeAllocation allocation : allocations) {
            totalFee = totalFee.add(allocation.getOriginalAmount());
            totalDiscount = totalDiscount.add(allocation.getTotalDiscount());
            basePayable = basePayable.add(allocation.getPayableAmount());
            // We IGNORE 'allocation.getRemainingAmount()' here because it follows the Roadmap logic (100k)
            // We will calculate the Real Pending (120k) dynamically below.
        }

        // 2. Calculate Late Fee Penalties
        BigDecimal totalLateFee = BigDecimal.ZERO;
        List<StudentInstallmentPlan> userInstallments = new ArrayList<>();
        for (StudentFeeAllocation allocation : allocations) {
            userInstallments.addAll(getInstallmentPlansByAllocationId(allocation.getId()));
        }

        for (StudentInstallmentPlan plan : userInstallments) {
            List<LateFeePenalty> penalties = lateFeePenaltyRepository.findByStudentInstallmentPlanId(plan.getId());
            for (LateFeePenalty p : penalties) {
                if (p.getIsWaived() == null || !p.getIsWaived()) {
                    totalLateFee = totalLateFee.add(p.getPenaltyAmount());
                }
            }
        }
        
        // 3. Calculate Exam Fees
        BigDecimal totalExamFee = BigDecimal.ZERO;
        List<ExamFeeLinkage> examFees = examFeeLinkageRepository.findByUserId(userId);
        for (ExamFeeLinkage exam : examFees) {
            totalExamFee = totalExamFee.add(exam.getExamFeeAmount());
        }

        // 4. Calculate Attendance Penalties
        BigDecimal totalAttendancePenalty = BigDecimal.ZERO;
        List<AttendancePenalty> attendancePenalties = attendancePenaltyRepository.findByUserId(userId);
        for (AttendancePenalty ap : attendancePenalties) {
             if (ap.getIsActive() != null && ap.getIsActive()) {
                 totalAttendancePenalty = totalAttendancePenalty.add(ap.getPenaltyAmount());
             }
        }
        
        // 5. 🔴 FINAL CALCULATIONS (The Fix)
        
        // A. Total Bill = Base + Late Fines + Exam Fees + Attendance
        BigDecimal finalTotalPayable = basePayable
                .add(totalLateFee)
                .add(totalExamFee)
                .add(totalAttendancePenalty);
        
        // B. Calculate Total PAID from Actual Transactions (Source of Truth)
        List<StudentFeePayment> payments = getPaymentHistory(userId);
        BigDecimal totalPaid = BigDecimal.ZERO;
        
        for (StudentFeePayment p : payments) {
            if (p.getPaymentStatus() == StudentFeePayment.PaymentStatus.SUCCESS) {
                totalPaid = totalPaid.add(p.getPaidAmount());
            }
        }
        
        // C. Derive Total Pending = Bill - Paid
        // This ensures Pending is 120k (Real) instead of 100k (Roadmap) if they haven't paid advance yet.
        BigDecimal finalTotalPending = finalTotalPayable.subtract(totalPaid).max(BigDecimal.ZERO);

        // 6. STATUS STRING GENERATION
        String paymentStatus = "DUE";
        
        boolean isRefunded = allocations.stream()
                .anyMatch(a -> a.getStatus() == StudentFeeAllocation.AllocationStatus.REFUNDED);

        if (isRefunded) {
            List<FeeRefund> refunds = feeRefundRepository.findByUserId(userId);
            FeeRefund lastRefund = refunds.stream()
                    .filter(r -> r.getRefundStatus() == FeeRefund.RefundStatus.PROCESSED)
                    .reduce((first, second) -> second)
                    .orElse(null);
            
            if (lastRefund != null) {
                paymentStatus = "Refunded " + lastRefund.getRefundAmount() + 
                               " on " + lastRefund.getProcessedDate();
            } else {
                paymentStatus = "REFUNDED";
            }
        } else if (finalTotalPending.compareTo(BigDecimal.ZERO) <= 0) {
            paymentStatus = "PAID";
        } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
            paymentStatus = "PARTIAL";
        }

        // 7. FILL RESPONSE MAP
        report.put("userId", userId);
        report.put("totalFee", totalFee);
        report.put("totalDiscount", totalDiscount);
        
        report.put("totalPayable", finalTotalPayable); 
        report.put("totalPending", finalTotalPending); 
        report.put("totalPaid", totalPaid);            
        report.put("paymentStatus", paymentStatus);
        
        report.put("totalLateFeePenalty", totalLateFee);
        report.put("totalAttendancePenalty", totalAttendancePenalty);
        report.put("totalExamFees", totalExamFee);
        
        report.put("allocations", allocations);
        
        // Using the helper method to format the history list
        List<Map<String, Object>> transformedHistory = transformPaymentsForReport(payments);
        report.put("paymentHistory", transformedHistory);
        
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
                // 1. Calculate Late Fees
                BigDecimal lateFees = BigDecimal.ZERO;
                List<StudentInstallmentPlan> plans = studentInstallmentPlanRepository
                        .findByStudentFeeAllocationId(allocation.getId());
                
                for (StudentInstallmentPlan plan : plans) {
                    BigDecimal planPenalty = lateFeePenaltyRepository.getTotalPenaltyByInstallmentPlanId(plan.getId());
                    if (planPenalty != null) {
                        lateFees = lateFees.add(planPenalty);
                    }
                }
                
                // 2. 🔴 NEW: Calculate Exam Fees
                BigDecimal examFees = examFeeLinkageRepository.getTotalExamFeeByAllocationId(allocation.getId());
                if (examFees == null) {
                    examFees = BigDecimal.ZERO;
                }

                // 3. Calculate Totals for this Student
                
                // EXPECTED = Base Fee + Late Fines + Exam Fees
                BigDecimal studentExpected = allocation.getPayableAmount()
                                            .add(lateFees)
                                            .add(examFees);
                
                // COLLECTED = Actual Payments from DB
                BigDecimal studentCollected = studentFeePaymentRepository.getTotalPaidByAllocationId(allocation.getId());
                if (studentCollected == null) {
                    studentCollected = BigDecimal.ZERO;
                }

                // PENDING = Expected - Collected
                BigDecimal studentPending = studentExpected.subtract(studentCollected).max(BigDecimal.ZERO);

                // 4. Add to Batch Accumulators
                totalExpected = totalExpected.add(studentExpected);
                totalCollected = totalCollected.add(studentCollected);
                totalPending = totalPending.add(studentPending);
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
                // 1. Calculate Late Fees
                BigDecimal lateFees = BigDecimal.ZERO;
                List<StudentInstallmentPlan> plans = studentInstallmentPlanRepository
                        .findByStudentFeeAllocationId(allocation.getId());
                
                for (StudentInstallmentPlan plan : plans) {
                    BigDecimal planPenalty = lateFeePenaltyRepository.getTotalPenaltyByInstallmentPlanId(plan.getId());
                    if (planPenalty != null) {
                        lateFees = lateFees.add(planPenalty);
                    }
                }
                
                // 2. 🔴 NEW: Calculate Exam Fees
                BigDecimal examFees = examFeeLinkageRepository.getTotalExamFeeByAllocationId(allocation.getId());
                if (examFees == null) {
                    examFees = BigDecimal.ZERO;
                }
                
                // 3. Calculate Totals
                
                // EXPECTED = Base Fee + Late Fines + Exam Fees
                BigDecimal studentExpected = allocation.getPayableAmount()
                                            .add(lateFees)
                                            .add(examFees);
                
                // COLLECTED = Actual Payments
                BigDecimal studentCollected = studentFeePaymentRepository.getTotalPaidByAllocationId(allocation.getId());
                if (studentCollected == null) {
                    studentCollected = BigDecimal.ZERO;
                }

                // PENDING = Expected - Collected
                BigDecimal studentPending = studentExpected.subtract(studentCollected).max(BigDecimal.ZERO);

                // 4. Add to Course Accumulators
                totalExpected = totalExpected.add(studentExpected);
                totalCollected = totalCollected.add(studentCollected);
                totalPending = totalPending.add(studentPending);
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
        
        // TRANSFORM LIST
        List<Map<String, Object>> displayPayments = transformPaymentsForReport(payments);
        
        Map<String, Object> report = new HashMap<>();
        report.put("year", year);
        report.put("month", month);
        report.put("totalCollected", totalCollected);
        report.put("totalTransactions", payments.size());
        report.put("payments", displayPayments); // <--- Added Clean List
        
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

        // TRANSFORM LIST
        List<Map<String, Object>> displayPayments = transformPaymentsForReport(payments);
        
        Map<String, Object> report = new HashMap<>();
        report.put("year", year);
        report.put("quarter", quarter);
        report.put("totalCollected", totalCollected);
        report.put("totalTransactions", payments.size());
        report.put("payments", displayPayments); // <--- Added Clean List
        
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

        // TRANSFORM LIST
        List<Map<String, Object>> displayPayments = transformPaymentsForReport(payments);
        
        Map<String, Object> report = new HashMap<>();
        report.put("year", year);
        report.put("totalCollected", totalCollected);
        report.put("totalTransactions", payments.size());
        report.put("payments", displayPayments); // <--- Added Clean List
        
        return report;
    }

    @Override
    public Map<String, Object> getOverallFinancialSummary() {
        List<StudentFeeAllocation> allAllocations = getAllFeeAllocations();
        
        BigDecimal totalExpected = BigDecimal.ZERO;
        BigDecimal totalCollected = BigDecimal.ZERO;
        BigDecimal totalPending = BigDecimal.ZERO;
        
        for (StudentFeeAllocation allocation : allAllocations) {
            // 1. Expected = The Full Bill (e.g., 120,000)
            totalExpected = totalExpected.add(allocation.getPayableAmount());

            // 2. Collected = Actual Money in Bank (Query Payments Table)
            // We ignore 'allocation.getRemainingAmount()' because it follows the Roadmap logic.
            // Instead, we sum the actual successful transactions.
            BigDecimal paidForUser = studentFeePaymentRepository.getTotalPaidByAllocationId(allocation.getId());
            if (paidForUser == null) paidForUser = BigDecimal.ZERO;
            
            totalCollected = totalCollected.add(paidForUser);
            
            // 3. Pending = Expected - Collected (120k - 0 = 120k)
            // This gives the CEO the "True Debt" figure, not the "Roadmap" figure.
            BigDecimal pendingForUser = allocation.getPayableAmount().subtract(paidForUser).max(BigDecimal.ZERO);
            totalPending = totalPending.add(pendingForUser);
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
    @Override
    public String createRazorpayOrder(Long allocationId, BigDecimal amount) {
        // 1. Security & Validation
        StudentFeeAllocation allocation = getFeeAllocationById(allocationId);
        
        if (allocation.getStatus() == StudentFeeAllocation.AllocationStatus.REFUNDED) {
            throw new RuntimeException("PAYMENT BLOCKED: This student has been refunded and cannot make further payments.");
        }
        // FIX: Removed CANCELLED check here to prevent error
        /*
        if (allocation.getStatus() == StudentFeeAllocation.AllocationStatus.CANCELLED) {
            throw new RuntimeException("PAYMENT BLOCKED: Admission cancelled.");
        }
        */
        
        BigDecimal truePending = getTruePendingAmount(allocation);
        if (truePending.compareTo(BigDecimal.ZERO) <= 0) {
             throw new RuntimeException("PAYMENT BLOCKED: No pending fees. All dues are clear.");
        }
        if (amount.compareTo(truePending) > 0) {
            throw new RuntimeException("Amount exceeds total pending fees of " + truePending);
        }

        // 2. FETCH DYNAMIC CURRENCY
        String currencyCode = (allocation.getCurrency() != null && !allocation.getCurrency().isEmpty()) 
                            ? allocation.getCurrency() 
                            : "INR"; // Safe Fallback

        // 3. Create Order
        try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount.multiply(new BigDecimal("100")).intValue());
            orderRequest.put("currency", currencyCode);
            orderRequest.put("receipt", "txn_" + allocationId + "_" + System.currentTimeMillis());

            Order order = razorpayClient.orders.create(orderRequest);
            return order.get("id"); 
        } catch (RazorpayException e) {
            throw new RuntimeException("Razorpay Error: " + e.getMessage());
        }
    }
 // In FeeServiceImpl.java

    @Override
    public FeeRefund getRefundByTransactionRef(String transactionRef) {
        // This will now work because you updated the Repository in Step 1
        return feeRefundRepository.findByTransactionReference(transactionRef)
                .orElseThrow(() -> new RuntimeException("Refund not found with Reference: " + transactionRef));
    }
 // ... inside FeeServiceImpl class ...

    // 1. IMPLEMENT THE NEW METHOD
    @Override
    public List<Map<String, Object>> getStudentTransactionHistory(Long userId) {
        // Fetch raw payments (Online + Offline)
        List<StudentFeePayment> rawPayments = getPaymentHistory(userId);
        
        // Transform them into your "Perfect JSON" format (With Refunds & Labels)
        return transformPaymentsForReport(rawPayments);
    }

    // 2. UPDATE THE TRANSFORMER (To handle Offline & Clear Descriptions)
 // In FeeServiceImpl.java

    @Override
    public boolean verifyRazorpayPayment(String orderId, String paymentId, String signature) {
        // 🔴 DEV MODE: BYPASS SIGNATURE CHECK
        // We simply return true so ANY signature (even "12345") is accepted.
        System.out.println(">>> DEV MODE: Skipping Signature Validation for " + paymentId);
        return true; 
    }
 // ============================================
    // NEW FEATURE: ALLOCATION + DISCOUNT MERGED
    // ============================================

    @Override
    @Transactional
    public StudentFeeAllocation createAllocationWithDiscount(
            StudentFeeAllocation allocation, 
            List<Long> discountIds) {

        // Validate Discounts
        if (discountIds != null && !discountIds.isEmpty()) {
            for (Long dId : discountIds) {
                FeeDiscount discount = feeDiscountRepository.findById(dId)
                        .orElseThrow(() -> new RuntimeException("Discount not found with ID: " + dId));
                
                // Security Check: Must belong to THIS user
                if (!discount.getUserId().equals(allocation.getUserId())) {
                    throw new RuntimeException("Discount ID " + dId + " does not belong to User ID " + allocation.getUserId());
                }
                if (!discount.getIsActive()) {
                     throw new RuntimeException("Discount ID " + dId + " is inactive.");
                }
            }
        }
        
        // Note: We don't save anything here. The calculation method below fetches 
        // ALL active discounts for this user/structure from the DB automatically.
        return createStudentFeeAllocation(allocation);
    }
 // ... inside FeeServiceImpl.java ...

    @Override
    @Transactional
    public List<StudentFeeAllocation> createBulkAllocation(
            List<Long> userIds, 
            Long feeStructureId, 
            List<Long> discountIds, // <--- Accepting IDs [55, 56]
            BigDecimal originalAmount, 
            BigDecimal advancePayment) {
        
        List<StudentFeeAllocation> results = new ArrayList<>();

        // A. Fetch the "Template" Discounts (Source of Truth)
        List<FeeDiscount> sourceDiscounts = new ArrayList<>();
        if (discountIds != null && !discountIds.isEmpty()) {
            sourceDiscounts = feeDiscountRepository.findAllById(discountIds);
            
            // Optional: Validation
            if (sourceDiscounts.size() != discountIds.size()) {
                throw new RuntimeException("Some Discount IDs provided do not exist.");
            }
        }

        // B. Iterate over each Student
        for (Long userId : userIds) {
            // Check duplicates
            boolean exists = studentFeeAllocationRepository
                .findByUserId(userId).stream()
                .anyMatch(a -> a.getFeeStructureId().equals(feeStructureId));
            
            if (exists) continue; 

            // 🔴 C. CLONE DISCOUNTS FOR THIS STUDENT
            // We take the values from IDs 55, 56 and create NEW records for User B, User C, etc.
            if (!sourceDiscounts.isEmpty()) {
                for (FeeDiscount source : sourceDiscounts) {
                    FeeDiscount newDiscount = new FeeDiscount();
                    newDiscount.setUserId(userId); // Assign to target student
                    newDiscount.setFeeStructureId(feeStructureId);
                    
                    // Copy values from the template
                    newDiscount.setDiscountName(source.getDiscountName());
                    newDiscount.setDiscountType(source.getDiscountType());
                    newDiscount.setDiscountValue(source.getDiscountValue());
                    newDiscount.setReason(source.getReason() + " (Bulk Applied)");
                    
                    newDiscount.setIsActive(true);
                    newDiscount.setApprovedBy(1L); // Admin
                    newDiscount.setApprovedDate(LocalDate.now());
                    
                    feeDiscountRepository.save(newDiscount); // Save NEW record
                }
            }

            // D. Create Allocation
            StudentFeeAllocation allocation = new StudentFeeAllocation();
            allocation.setUserId(userId);
            allocation.setFeeStructureId(feeStructureId);
            allocation.setAllocationDate(LocalDate.now());
            allocation.setStatus(StudentFeeAllocation.AllocationStatus.ACTIVE);
            
            if (originalAmount != null) allocation.setOriginalAmount(originalAmount);
            if (advancePayment != null) allocation.setAdvancePayment(advancePayment);
            
            // E. Calculate (Will pick up the cloned discounts we just saved)
            StudentFeeAllocation saved = createStudentFeeAllocation(allocation);
            
            results.add(saved);
        }
        
        return results;
    }
 // ============================================
    // HELPER: Transform Payment List for Reports
    // ============================================
 // ============================================
    // UPDATED HELPER: Mix Payments AND Refunds for History
    // ============================================
 // ============================================
    // UPDATED HELPER: Generates "Gross" History + Refund Lines
    // ============================================
    private List<Map<String, Object>> transformPaymentsForReport(List<StudentFeePayment> payments) {
        List<Map<String, Object>> historyList = new ArrayList<>();

        for (StudentFeePayment payment : payments) {
            
            // 1. Find refunds linked to this payment
            List<FeeRefund> linkedRefunds = feeRefundRepository.findByStudentFeePaymentId(payment.getId());
            
            BigDecimal totalRefunded = BigDecimal.ZERO;
            List<FeeRefund> processedRefunds = new ArrayList<>();

            for(FeeRefund r : linkedRefunds) {
                if(r.getRefundStatus() == FeeRefund.RefundStatus.PROCESSED) {
                    totalRefunded = totalRefunded.add(r.getRefundAmount());
                    processedRefunds.add(r);
                }
            }

            // 2. RESTORE Original Amount (Database has Net 10k, we display Gross 20k)
            BigDecimal originalAmount = payment.getPaidAmount().add(totalRefunded);

            Map<String, Object> paymentMap = new HashMap<>();
            paymentMap.put("id", payment.getId());
            paymentMap.put("amount", originalAmount); 
            paymentMap.put("paymentDate", payment.getPaymentDate());
            paymentMap.put("paymentMode", payment.getPaymentMode());
            
            String cleanRef = payment.getTransactionReference() != null ? 
                              payment.getTransactionReference().replace(" (Partially Refunded " + totalRefunded + ")", "") : "N/A";
            paymentMap.put("transactionRef", cleanRef);

            // 3. SMART DESCRIPTION LOGIC
            String baseDesc = "";
            if (payment.getStudentInstallmentPlanId() == null) {
                baseDesc = "Advance Fee Payment";
                paymentMap.put("paymentStatus", "ADVANCE_PAYMENT");
            } else {
                baseDesc = "Installment #" + payment.getStudentInstallmentPlanId();
                paymentMap.put("paymentStatus", "SUCCESS");
            }

            if (payment.getPaymentMode() == StudentFeePayment.PaymentMode.CASH || 
                payment.getPaymentMode() == StudentFeePayment.PaymentMode.BANK_TRANSFER) {
                baseDesc += " (Offline)";
            }
            paymentMap.put("description", baseDesc);

            historyList.add(paymentMap);

            // --- SEPARATE REFUND ROWS ---
            for (FeeRefund refund : processedRefunds) {
                Map<String, Object> refundMap = new HashMap<>();
                refundMap.put("id", "REF-" + refund.getId());
                refundMap.put("amount", refund.getRefundAmount().negate());
                refundMap.put("paymentDate", refund.getProcessedDate());
                refundMap.put("paymentMode", "REFUND_ADJUSTMENT");
                refundMap.put("transactionRef", refund.getTransactionReference());
                refundMap.put("paymentStatus", "REFUNDED");
                refundMap.put("description", "Refund for " + baseDesc);
                
                historyList.add(refundMap);
            }
        }
        
        historyList.sort((m1, m2) -> {
            String d1 = m1.get("paymentDate").toString();
            String d2 = m2.get("paymentDate").toString();
            return d1.compareTo(d2);
        });

        return historyList;
    }
 // Inside FeeServiceImpl.java

    @Override
    public void processAutoDebitForUser(Long userId, String studentName, String studentEmail) {
        // 1. Find the Config for this User
        List<AutoDebitConfig> configs = autoDebitConfigRepository.findByUserId(userId);
        
        if (configs.isEmpty()) {
            throw new RuntimeException("No Auto-Debit Config found for User ID: " + userId);
        }

        // Assuming 1 active config per user
        AutoDebitConfig config = configs.stream()
                .filter(c -> c.getIsActive() != null && c.getIsActive())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No Active Auto-Debit Config for User ID: " + userId));

        // 2. Calculate Amount (Same logic as main process)
        List<StudentInstallmentPlan> installments = studentInstallmentPlanRepository
                .findByStudentFeeAllocationId(config.getStudentFeeAllocationId())
                .stream()
                .filter(i -> i.getStatus() == StudentInstallmentPlan.InstallmentStatus.PENDING ||
                             i.getStatus() == StudentInstallmentPlan.InstallmentStatus.PARTIALLY_PAID)
                .sorted(Comparator.comparing(StudentInstallmentPlan::getDueDate))
                .collect(Collectors.toList());

        if (installments.isEmpty()) {
            throw new RuntimeException("No pending installments found for this user.");
        }

        StudentInstallmentPlan nextInstallment = installments.get(0);
        BigDecimal amountToDebit = nextInstallment.getInstallmentAmount()
                .subtract(nextInstallment.getPaidAmount() != null ? 
                         nextInstallment.getPaidAmount() : BigDecimal.ZERO);

        // 3. Process Payment (Passing the Name & Email you gave!)
        String transactionRef = "AUTO-MANUAL-" + System.currentTimeMillis();
        
        processOnlinePayment(
            config.getStudentFeeAllocationId(),
            nextInstallment.getId(),
            amountToDebit,
            "AUTO_DEBIT",
            transactionRef,
            "Manual Trigger of Auto-Debit",
            null,            // screenshotUrl
            studentName,     // <--- ✅ PASSED FROM CONTROLLER
            studentEmail     // <--- ✅ PASSED FROM CONTROLLER
        );
        
        System.out.println("✅ Single Auto-Debit success for: " + studentName);
    }
    @Override
    @Transactional
    public void runAutoBlockCheck(BigDecimal blockThreshold) {
        List<Long> allUserIds = studentFeeAllocationRepository.findAll()
                .stream().map(StudentFeeAllocation::getUserId).distinct().collect(Collectors.toList());

        for (Long userId : allUserIds) {
            BigDecimal totalPending = BigDecimal.ZERO;
            List<StudentFeeAllocation> allocations = getFeeAllocationsByUserId(userId);
            
            for (StudentFeeAllocation a : allocations) {
                totalPending = totalPending.add(getTruePendingAmount(a));
            }

            if (totalPending.compareTo(blockThreshold) > 0) {
                if (canIssueCertificate(userId)) { 
                    CertificateBlockList block = new CertificateBlockList();
                    block.setUserId(userId);
                    block.setPendingAmount(totalPending);
                    block.setBlockedReason("Auto-Blocked: Dues exceed limit of " + blockThreshold);
                    block.setBlockedBy(0L); 
                    block.setBlockedDate(LocalDate.now());
                    block.setIsActive(true);
                    
                    certificateBlockListRepository.save(block);
                }
            }
        }
    }
 // ============================================
    // HELPER: CALCULATE TRUE PENDING (IGNORES ROADMAP)
    // ============================================
    private BigDecimal getTruePendingAmount(StudentFeeAllocation allocation) {
        BigDecimal totalExpected = allocation.getPayableAmount();
        BigDecimal totalPaid = studentFeePaymentRepository.getTotalPaidByAllocationId(allocation.getId());
        if (totalPaid == null) totalPaid = BigDecimal.ZERO;
        return totalExpected.subtract(totalPaid).max(BigDecimal.ZERO);
    }
    public enum BulkLinkType {
        BATCH,
        COURSE
    }
}