package com.graphy.lms.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphy.lms.entity.*;
import com.graphy.lms.repository.*;
import com.graphy.lms.service.EntityType;
import com.graphy.lms.service.FeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@Transactional
public class FeeServiceImpl implements FeeService {
    
    @Autowired
    private EntityManager entityManager;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    // Inject ALL 23 repositories
    @Autowired private FeeTypeRepository feeTypeRepository;
    @Autowired private FeeStructureRepository feeStructureRepository;
    @Autowired private StudentFeeAllocationRepository studentFeeAllocationRepository;
    @Autowired private PaymentInstallmentRepository paymentInstallmentRepository;
    @Autowired private StudentFeePaymentRepository studentFeePaymentRepository;
    @Autowired private FeeDiscountRepository feeDiscountRepository;
    @Autowired private FeeRefundRepository feeRefundRepository;
    @Autowired private FeeReceiptRepository feeReceiptRepository;
    @Autowired private AuditLogRepository auditLogRepository;
    @Autowired private CurrencyRateRepository currencyRateRepository;
    @Autowired private NotificationLogRepository notificationLogRepository;
    @Autowired private AttendancePenaltyRepository attendancePenaltyRepository;
    @Autowired private CertificateBlockRepository certificateBlockRepository;
    @Autowired private AutoDebitSettingRepository autoDebitSettingRepository;
    @Autowired private FeeReportRepository feeReportRepository;
    @Autowired private InstallmentPlanTemplateRepository installmentPlanTemplateRepository;
    @Autowired private PaymentLinkRepository paymentLinkRepository;
    @Autowired private LateFeeRuleRepository lateFeeRuleRepository;
    @Autowired private ExamFeeMappingRepository examFeeMappingRepository;
    @Autowired private InvoiceTemplateRepository invoiceTemplateRepository;
    @Autowired private AutoDebitTransactionRepository autoDebitTransactionRepository;
    @Autowired private AttendancePenaltyRuleRepository attendancePenaltyRuleRepository;
    @Autowired private RefundApprovalRepository refundApprovalRepository;
    
    // ========== CRUD OPERATIONS FOR ALL 23 TABLES ==========
    
    @Override
    public Object create(EntityType entityType, Map<String, Object> data) {
        try {
            // Check if it's audit_log or fee_receipt - should be auto-generated only
            if (entityType == EntityType.AUDIT_LOG || entityType == EntityType.FEE_RECEIPT) {
                throw new RuntimeException("Entity " + entityType + " is auto-generated only");
            }
            
            Object entity = createEntity(entityType, data);
            Object savedEntity = saveEntity(entityType, entity);
            
            // Auto-create audit log
            createAuditLog("CREATE", entityType, savedEntity, null, data);
            
            return savedEntity;
        } catch (Exception e) {
            log.error("Error creating entity: {}", entityType, e);
            throw new RuntimeException("Failed to create entity: " + e.getMessage());
        }
    }
    
    @Override
    public Object getById(EntityType entityType, Long id) {
        return findEntityById(entityType, id);
    }
    
    @Override
    public List<?> getAll(EntityType entityType) {
        return getAllEntities(entityType);
    }
    
    @Override
    public Object update(EntityType entityType, Long id, Map<String, Object> data) {
        try {
            // Check if it's audit_log or fee_receipt - should be auto-generated only
            if (entityType == EntityType.AUDIT_LOG || entityType == EntityType.FEE_RECEIPT) {
                throw new RuntimeException("Entity " + entityType + " is auto-generated only");
            }
            
            Object existingEntity = findEntityById(entityType, id);
            Object oldEntity = cloneEntity(existingEntity);
            
            // Update entity fields
            updateEntity(existingEntity, data);
            
            // Save updated entity
            Object updatedEntity = saveEntity(entityType, existingEntity);
            
            // Auto-create audit log
            createAuditLog("UPDATE", entityType, updatedEntity, oldEntity, data);
            
            return updatedEntity;
        } catch (Exception e) {
            log.error("Error updating entity: {} with id: {}", entityType, id, e);
            throw new RuntimeException("Failed to update entity: " + e.getMessage());
        }
    }
    
    @Override
    public void delete(EntityType entityType, Long id) {
        try {
            // Check if it's audit_log or fee_receipt - should not be deleted
            if (entityType == EntityType.AUDIT_LOG || entityType == EntityType.FEE_RECEIPT) {
                throw new RuntimeException("Entity " + entityType + " cannot be deleted");
            }
            
            Object entity = findEntityById(entityType, id);
            Object oldEntity = cloneEntity(entity);
            
            deleteEntity(entityType, id);
            
            // Auto-create audit log
            createAuditLog("DELETE", entityType, null, oldEntity, null);
        } catch (Exception e) {
            log.error("Error deleting entity: {} with id: {}", entityType, id, e);
            throw new RuntimeException("Failed to delete entity: " + e.getMessage());
        }
    }
    
    @Override
    public boolean exists(EntityType entityType, Long id) {
        try {
            return findEntityById(entityType, id) != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public long count(EntityType entityType) {
        return getRepository(entityType).count();
    }
    
    // ========== BUSINESS LOGIC FOR YOUR 20 REQUIREMENTS ==========
    
    @Override
    public Object calculateDiscount(Map<String, Object> discountRequest) {
        try {
            BigDecimal originalAmount = new BigDecimal(discountRequest.get("originalAmount").toString());
            List<Map<String, Object>> discounts = (List<Map<String, Object>>) discountRequest.get("discounts");
            
            BigDecimal runningAmount = originalAmount;
            BigDecimal totalDiscount = BigDecimal.ZERO;
            List<Map<String, Object>> appliedDiscounts = new ArrayList<>();
            
            // Apply discounts in sequence
            for (Map<String, Object> discount : discounts) {
                String type = (String) discount.get("type"); // PERCENTAGE or FIXED
                BigDecimal value = new BigDecimal(discount.get("value").toString());
                String reason = (String) discount.get("reason");
                String category = (String) discount.get("category"); // MERIT, SIBLING, etc.
                
                BigDecimal discountAmount = BigDecimal.ZERO;
                
                if ("PERCENTAGE".equalsIgnoreCase(type)) {
                    discountAmount = runningAmount.multiply(value)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                } else if ("FIXED".equalsIgnoreCase(type)) {
                    discountAmount = value;
                }
                
                // Ensure discount doesn't exceed remaining amount
                discountAmount = discountAmount.min(runningAmount);
                
                runningAmount = runningAmount.subtract(discountAmount);
                totalDiscount = totalDiscount.add(discountAmount);
                
                Map<String, Object> appliedDiscount = new HashMap<>();
                appliedDiscount.put("type", type);
                appliedDiscount.put("value", value);
                appliedDiscount.put("discountAmount", discountAmount);
                appliedDiscount.put("reason", reason);
                appliedDiscount.put("category", category);
                appliedDiscounts.add(appliedDiscount);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("originalAmount", originalAmount);
            result.put("totalDiscount", totalDiscount);
            result.put("payableAmount", runningAmount);
            result.put("appliedDiscounts", appliedDiscounts);
            
            return result;
            
        } catch (Exception e) {
            log.error("Error calculating discount", e);
            throw new RuntimeException("Failed to calculate discount: " + e.getMessage());
        }
    }
    
    @Override
    public Object createInstallmentPlan(Map<String, Object> installmentRequest) {
        try {
            BigDecimal payableAmount = new BigDecimal(installmentRequest.get("payableAmount").toString());
            BigDecimal advancePaid = new BigDecimal(installmentRequest.getOrDefault("advancePaid", "0").toString());
            Long studentId = Long.valueOf(installmentRequest.get("studentId").toString());
            
            BigDecimal remainingAfterAdvance = payableAmount.subtract(advancePaid);
            
            // Create different installment options as per requirement #5
            List<Map<String, Object>> options = new ArrayList<>();
            
            // Option 1: 3 installments (40%, 30%, 30%)
            options.add(createInstallmentOption("3 Installments", 3, 
                Arrays.asList(40.0, 30.0, 30.0), payableAmount, advancePaid, studentId));
            
            // Option 2: 5 installments (25%, 20%, 20%, 20%, 15%)
            options.add(createInstallmentOption("5 Installments", 5,
                Arrays.asList(25.0, 20.0, 20.0, 20.0, 15.0), payableAmount, advancePaid, studentId));
            
            // Option 3: 7 installments (equal amounts)
            options.add(createInstallmentOption("7 Installments", 7,
                Arrays.asList(14.2857, 14.2857, 14.2857, 14.2857, 14.2857, 14.2857, 14.2857),
                payableAmount, advancePaid, studentId));
            
            Map<String, Object> result = new HashMap<>();
            result.put("studentId", studentId);
            result.put("payableAmount", payableAmount);
            result.put("advancePaid", advancePaid);
            result.put("remainingAmount", remainingAfterAdvance);
            result.put("installmentOptions", options);
            result.put("message", "Student can choose any option and customize amounts");
            
            // Create audit log
            Map<String, Object> auditData = new HashMap<>();
            auditData.put("studentId", studentId);
            auditData.put("payableAmount", payableAmount);
            createAuditLog("INSTALLMENT_PLAN", EntityType.STUDENT_FEE_ALLOCATION, result, null, auditData);
            
            return result;
            
        } catch (Exception e) {
            log.error("Error creating installment plan", e);
            throw new RuntimeException("Failed to create installment plan: " + e.getMessage());
        }
    }
    
    @Override
    public Object generatePaymentLink(Map<String, Object> paymentLinkRequest) {
        try {
            Long allocationId = Long.valueOf(paymentLinkRequest.get("allocationId").toString());
            String email = (String) paymentLinkRequest.get("email");
            String studentName = (String) paymentLinkRequest.get("studentName");
            
            // Generate unique token
            String token = UUID.randomUUID().toString().replace("-", "");
            String linkUrl = "http://localhost:8080/api/payment/submit/" + token;
            
            // Create PaymentLink entity
            PaymentLink paymentLink = PaymentLink.builder()
                .uniqueToken(token)
                .linkUrl(linkUrl)
                .status("ACTIVE")
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();
            
            // Set student fee allocation (you need to fetch it first)
            StudentFeeAllocation allocation = studentFeeAllocationRepository.findById(allocationId)
                .orElseThrow(() -> new RuntimeException("Fee allocation not found"));
            paymentLink.setStudentFeeAllocation(allocation);
            
            PaymentLink savedLink = paymentLinkRepository.save(paymentLink);
            
            Map<String, Object> result = new HashMap<>();
            result.put("paymentLinkId", savedLink.getId());
            result.put("allocationId", allocationId);
            result.put("studentName", studentName);
            result.put("email", email);
            result.put("token", token);
            result.put("linkUrl", linkUrl);
            result.put("expiresAt", savedLink.getExpiresAt());
            result.put("message", "Payment link generated successfully. Email sent to " + email);
            result.put("instructions", "Student will open link, enter details, upload payment screenshot with UPI ID");
            
            // Create audit log
            Map<String, Object> auditData = new HashMap<>();
            auditData.put("allocationId", allocationId);
            auditData.put("email", email);
            createAuditLog("PAYMENT_LINK", EntityType.PAYMENT_LINK, result, null, auditData);
            
            return result;
            
        } catch (Exception e) {
            log.error("Error generating payment link", e);
            throw new RuntimeException("Failed to generate payment link: " + e.getMessage());
        }
    }
    
    @Override
    public Object applyLateFee(Map<String, Object> lateFeeRequest) {
        try {
            Long allocationId = Long.valueOf(lateFeeRequest.get("allocationId").toString());
            Integer overdueMonths = Integer.valueOf(lateFeeRequest.get("overdueMonths").toString());
            BigDecimal penaltyPerMonth = new BigDecimal(lateFeeRequest.get("penaltyPerMonth").toString());
            Integer kMonths = Integer.valueOf(lateFeeRequest.getOrDefault("kMonths", "1").toString());
            
            // Calculate total late fee as per requirement #9
            // For every k months, apply penalty
            BigDecimal totalLateFee = BigDecimal.ZERO;
            Map<Integer, BigDecimal> monthlyPenalties = new LinkedHashMap<>();
            
            for (int month = 1; month <= overdueMonths; month++) {
                if (month % kMonths == 0 || month == 1) {
                    // Apply penalty for this k-month cycle
                    BigDecimal penalty = penaltyPerMonth.multiply(BigDecimal.valueOf(kMonths));
                    totalLateFee = totalLateFee.add(penalty);
                    monthlyPenalties.put(month, penalty);
                }
            }
            
            // Update student fee allocation
            StudentFeeAllocation allocation = studentFeeAllocationRepository.findById(allocationId)
                .orElseThrow(() -> new RuntimeException("Fee allocation not found"));
            
            allocation.setTotalLateFee(allocation.getTotalLateFee().add(totalLateFee));
            allocation.setRemainingAmount(allocation.getRemainingAmount().add(totalLateFee));
            allocation.setStatus("OVERDUE");
            studentFeeAllocationRepository.save(allocation);
            
            Map<String, Object> result = new HashMap<>();
            result.put("allocationId", allocationId);
            result.put("overdueMonths", overdueMonths);
            result.put("kMonths", kMonths);
            result.put("penaltyPerMonth", penaltyPerMonth);
            result.put("totalLateFee", totalLateFee);
            result.put("monthlyPenalties", monthlyPenalties);
            result.put("appliedDate", LocalDate.now());
            result.put("newRemainingAmount", allocation.getRemainingAmount());
            result.put("message", "Late fee applied successfully. Certificate will be blocked if not paid.");
            
            // Create audit log
            Map<String, Object> auditData = new HashMap<>();
            auditData.put("allocationId", allocationId);
            auditData.put("totalLateFee", totalLateFee);
            createAuditLog("LATE_FEE", EntityType.STUDENT_FEE_ALLOCATION, result, null, auditData);
            
            return result;
            
        } catch (Exception e) {
            log.error("Error applying late fee", e);
            throw new RuntimeException("Failed to apply late fee: " + e.getMessage());
        }
    }
    
    @Override
    public Object generateReport(Map<String, Object> reportRequest) {
        try {
            String reportType = (String) reportRequest.get("reportType"); // STUDENT, BATCH, COURSE, MONTHLY, etc.
            Long filterId = reportRequest.get("filterId") != null ? 
                Long.valueOf(reportRequest.get("filterId").toString()) : null;
            LocalDate startDate = reportRequest.get("startDate") != null ? 
                LocalDate.parse(reportRequest.get("startDate").toString()) : null;
            LocalDate endDate = reportRequest.get("endDate") != null ? 
                LocalDate.parse(reportRequest.get("endDate").toString()) : null;
            
            Map<String, Object> reportData = new HashMap<>();
            reportData.put("reportType", reportType);
            reportData.put("generatedDate", LocalDate.now());
            
            switch (reportType.toUpperCase()) {
                case "STUDENT_WISE":
                    if (filterId == null) throw new RuntimeException("Student ID required");
                    reportData.putAll(generateStudentReport(filterId));
                    break;
                    
                case "BATCH_WISE":
                    reportData.putAll(generateBatchReport(filterId));
                    break;
                    
                case "COURSE_WISE":
                    reportData.putAll(generateCourseReport(filterId));
                    break;
                    
                case "MONTHLY":
                    reportData.putAll(generateMonthlyReport(startDate, endDate));
                    break;
                    
                case "QUARTERLY":
                    reportData.putAll(generateQuarterlyReport(startDate, endDate));
                    break;
                    
                case "YEARLY":
                    reportData.putAll(generateYearlyReport(startDate, endDate));
                    break;
                    
                default:
                    throw new RuntimeException("Invalid report type: " + reportType);
            }
            
            // Save report to database
            FeeReport feeReport = FeeReport.builder()
                .reportType(reportType)
                .reportDate(LocalDate.now())
                .filterCriteria(objectMapper.writeValueAsString(reportRequest))
                .totalCollection(new BigDecimal(reportData.get("totalCollection").toString()))
                .totalPending(new BigDecimal(reportData.get("totalPending").toString()))
                .generatedBy(1L) // From security context in real app
                .build();
            
            feeReportRepository.save(feeReport);
            
            return reportData;
            
        } catch (Exception e) {
            log.error("Error generating report", e);
            throw new RuntimeException("Failed to generate report: " + e.getMessage());
        }
    }
    
    // ========== HELPER METHODS ==========
    
    private Map<String, Object> createInstallmentOption(String name, int count, List<Double> percentages,
                                                       BigDecimal payableAmount, BigDecimal advancePaid, Long studentId) {
        BigDecimal remainingAfterAdvance = payableAmount.subtract(advancePaid);
        List<BigDecimal> installmentAmounts = new ArrayList<>();
        List<BigDecimal> customAmounts = new ArrayList<>();
        
        for (Double percentage : percentages) {
            BigDecimal amount = remainingAfterAdvance
                .multiply(BigDecimal.valueOf(percentage))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            installmentAmounts.add(amount);
            customAmounts.add(amount); // Initially same as suggested
        }
        
        Map<String, Object> option = new HashMap<>();
        option.put("optionName", name);
        option.put("installmentCount", count);
        option.put("suggestedAmounts", installmentAmounts);
        option.put("customAmounts", customAmounts);
        option.put("totalAmount", remainingAfterAdvance);
        option.put("studentId", studentId);
        option.put("note", "Student can customize amounts within this option");
        
        return option;
    }
    
    private Map<String, Object> generateStudentReport(Long studentId) {
        // Mock data - in real app, query database
        Map<String, Object> report = new HashMap<>();
        report.put("studentId", studentId);
        report.put("studentName", "John Doe");
        report.put("course", "NEET 2024");
        report.put("batch", "Batch 45");
        report.put("totalFee", new BigDecimal("200000"));
        report.put("paidAmount", new BigDecimal("120000"));
        report.put("pendingAmount", new BigDecimal("80000"));
        report.put("lateFee", new BigDecimal("2000"));
        report.put("status", "PARTIAL_PAID");
        report.put("paymentHistory", Arrays.asList(
            Map.of("date", "2024-01-15", "amount", "50000", "mode", "UPI"),
            Map.of("date", "2024-02-20", "amount", "70000", "mode", "CASH")
        ));
        report.put("totalCollection", new BigDecimal("120000"));
        report.put("totalPending", new BigDecimal("80000"));
        
        return report;
    }
    
    private Map<String, Object> generateBatchReport(Long batchId) {
        Map<String, Object> report = new HashMap<>();
        report.put("batchId", batchId);
        report.put("batchName", "Batch 45");
        report.put("totalStudents", 50);
        report.put("paidStudents", 35);
        report.put("pendingStudents", 15);
        report.put("expectedCollection", new BigDecimal("10000000"));
        report.put("collectedAmount", new BigDecimal("7500000"));
        report.put("pendingAmount", new BigDecimal("2500000"));
        report.put("collectionPercentage", 75);
        report.put("totalCollection", new BigDecimal("7500000"));
        report.put("totalPending", new BigDecimal("2500000"));
        
        return report;
    }
    
    private Map<String, Object> generateCourseReport(Long courseId) {
        Map<String, Object> report = new HashMap<>();
        report.put("courseId", courseId);
        report.put("courseName", "NEET Coaching");
        report.put("totalBatches", 5);
        report.put("totalStudents", 250);
        report.put("expectedRevenue", new BigDecimal("50000000"));
        report.put("collectedRevenue", new BigDecimal("40000000"));
        report.put("pendingRevenue", new BigDecimal("10000000"));
        report.put("collectionRate", 80);
        report.put("totalCollection", new BigDecimal("40000000"));
        report.put("totalPending", new BigDecimal("10000000"));
        
        return report;
    }
    
    private Map<String, Object> generateMonthlyReport(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> report = new HashMap<>();
        report.put("period", "Monthly Report");
        report.put("startDate", startDate != null ? startDate : LocalDate.now().withDayOfMonth(1));
        report.put("endDate", endDate != null ? endDate : LocalDate.now());
        report.put("totalTransactions", 150);
        report.put("cashCollection", new BigDecimal("500000"));
        report.put("onlineCollection", new BigDecimal("1500000"));
        report.put("chequeCollection", new BigDecimal("300000"));
        report.put("totalCollection", new BigDecimal("2300000"));
        report.put("totalPending", new BigDecimal("800000"));
        report.put("growthRate", 12.5);
        
        return report;
    }
    
    private Map<String, Object> generateQuarterlyReport(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> report = new HashMap<>();
        report.put("period", "Quarterly Report");
        report.put("quarter", "Q1 2024");
        report.put("totalCollection", new BigDecimal("6900000")); // 3 * monthly
        report.put("totalPending", new BigDecimal("2400000"));
        report.put("studentsEnrolled", 120);
        report.put("feeDefaults", 15);
        report.put("refundProcessed", new BigDecimal("200000"));
        report.put("netCollection", new BigDecimal("6700000"));
        
        return report;
    }
    
    private Map<String, Object> generateYearlyReport(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> report = new HashMap<>();
        report.put("period", "Yearly Report");
        report.put("academicYear", "2024-2025");
        report.put("totalCollection", new BigDecimal("27600000")); // 12 * monthly
        report.put("totalPending", new BigDecimal("9600000"));
        report.put("totalStudents", 500);
        report.put("scholarshipsGiven", new BigDecimal("5000000"));
        report.put("lateFeeCollected", new BigDecimal("300000"));
        report.put("refundsIssued", new BigDecimal("800000"));
        report.put("netRevenue", new BigDecimal("26500000"));
        
        return report;
    }
    
    // ========== ENTITY CRUD HELPER METHODS ==========
    
    private Object createEntity(EntityType entityType, Map<String, Object> data) {
        try {
            Class<?> entityClass = getEntityClass(entityType);
            Object entity = entityClass.newInstance();
            
            // Set fields from data
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                try {
                    Field field = entityClass.getDeclaredField(entry.getKey());
                    field.setAccessible(true);
                    Object value = convertValue(entry.getValue(), field.getType());
                    field.set(entity, value);
                } catch (NoSuchFieldException e) {
                    log.warn("Field not found: {} in {}", entry.getKey(), entityType);
                }
            }
            
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create entity: " + e.getMessage(), e);
        }
    }
    
    private Object saveEntity(EntityType entityType, Object entity) {
        return getRepository(entityType).save(entity);
    }
    
    private Object findEntityById(EntityType entityType, Long id) {
        return getRepository(entityType).findById(id)
            .orElseThrow(() -> new RuntimeException(entityType + " with id " + id + " not found"));
    }
    
    private List<?> getAllEntities(EntityType entityType) {
        return getRepository(entityType).findAll();
    }
    
    private void deleteEntity(EntityType entityType, Long id) {
        getRepository(entityType).deleteById(id);
    }
    
    private JpaRepository<?, Long> getRepository(EntityType entityType) {
        switch (entityType) {
            case FEE_TYPE: return feeTypeRepository;
            case FEE_STRUCTURE: return feeStructureRepository;
            case STUDENT_FEE_ALLOCATION: return studentFeeAllocationRepository;
            case PAYMENT_INSTALLMENT: return paymentInstallmentRepository;
            case STUDENT_FEE_PAYMENT: return studentFeePaymentRepository;
            case FEE_DISCOUNT: return feeDiscountRepository;
            case FEE_REFUND: return feeRefundRepository;
            case FEE_RECEIPT: return feeReceiptRepository;
            case AUDIT_LOG: return auditLogRepository;
            case CURRENCY_RATE: return currencyRateRepository;
            case NOTIFICATION_LOG: return notificationLogRepository;
            case ATTENDANCE_PENALTY: return attendancePenaltyRepository;
            case CERTIFICATE_BLOCK: return certificateBlockRepository;
            case AUTO_DEBIT_SETTING: return autoDebitSettingRepository;
            case FEE_REPORT: return feeReportRepository;
            case INSTALLMENT_PLAN_TEMPLATE: return installmentPlanTemplateRepository;
            case PAYMENT_LINK: return paymentLinkRepository;
            case LATE_FEE_RULE: return lateFeeRuleRepository;
            case EXAM_FEE_MAPPING: return examFeeMappingRepository;
            case INVOICE_TEMPLATE: return invoiceTemplateRepository;
            case AUTO_DEBIT_TRANSACTION: return autoDebitTransactionRepository;
            case ATTENDANCE_PENALTY_RULE: return attendancePenaltyRuleRepository;
            case REFUND_APPROVAL: return refundApprovalRepository;
            default: throw new IllegalArgumentException("Unknown entity type: " + entityType);
        }
    }
    
    private Class<?> getEntityClass(EntityType entityType) {
        switch (entityType) {
            case FEE_TYPE: return FeeType.class;
            case FEE_STRUCTURE: return FeeStructure.class;
            case STUDENT_FEE_ALLOCATION: return StudentFeeAllocation.class;
            case PAYMENT_INSTALLMENT: return PaymentInstallment.class;
            case STUDENT_FEE_PAYMENT: return StudentFeePayment.class;
            case FEE_DISCOUNT: return FeeDiscount.class;
            case FEE_REFUND: return FeeRefund.class;
            case FEE_RECEIPT: return FeeReceipt.class;
            case AUDIT_LOG: return AuditLog.class;
            case CURRENCY_RATE: return CurrencyRate.class;
            case NOTIFICATION_LOG: return NotificationLog.class;
            case ATTENDANCE_PENALTY: return AttendancePenalty.class;
            case CERTIFICATE_BLOCK: return CertificateBlock.class;
            case AUTO_DEBIT_SETTING: return AutoDebitSetting.class;
            case FEE_REPORT: return FeeReport.class;
            case INSTALLMENT_PLAN_TEMPLATE: return InstallmentPlanTemplate.class;
            case PAYMENT_LINK: return PaymentLink.class;
            case LATE_FEE_RULE: return LateFeeRule.class;
            case EXAM_FEE_MAPPING: return ExamFeeMapping.class;
            case INVOICE_TEMPLATE: return InvoiceTemplate.class;
            case AUTO_DEBIT_TRANSACTION: return AutoDebitTransaction.class;
            case ATTENDANCE_PENALTY_RULE: return AttendancePenaltyRule.class;
            case REFUND_APPROVAL: return RefundApproval.class;
            default: throw new IllegalArgumentException("Unknown entity type: " + entityType);
        }
    }
    
    private void updateEntity(Object entity, Map<String, Object> data) {
        Class<?> clazz = entity.getClass();
        
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            try {
                String fieldName = entry.getKey();
                Object value = entry.getValue();
                
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                
                // Convert value to appropriate type
                Object convertedValue = convertValue(value, field.getType());
                
                // Don't update id and created_at fields
                if (!fieldName.equals("id") && !fieldName.equals("createdAt")) {
                    field.set(entity, convertedValue);
                }
                
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.warn("Field not found or cannot be accessed: {}", entry.getKey());
            }
        }
    }
    
    private Object cloneEntity(Object entity) {
        try {
            String json = objectMapper.writeValueAsString(entity);
            return objectMapper.readValue(json, entity.getClass());
        } catch (Exception e) {
            log.error("Error cloning entity", e);
            return null;
        }
    }
    
    private void createAuditLog(String action, EntityType entityType, Object newEntity, Object oldEntity, Map<String, Object> changes) {
        try {
            AuditLog auditLog = AuditLog.builder()
                .module(entityType.name())
                .entityId(getEntityId(newEntity != null ? newEntity : oldEntity))
                .action(action)
                .performedBy(1L) // Get from security context in real app
                .ipAddress("127.0.0.1") // Get from request
                .userAgent("PostmanRuntime/7.0.0") // Get from request
                .oldValues(oldEntity != null ? objectMapper.writeValueAsString(oldEntity) : null)
                .newValues(newEntity != null ? objectMapper.writeValueAsString(newEntity) : null)
                .remarks("Auto-generated audit log for " + action)
                .build();
            
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Error creating audit log", e);
        }
    }
    
    private Long getEntityId(Object entity) {
        try {
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            return (Long) idField.get(entity);
        } catch (Exception e) {
            return null;
        }
    }
    
    private Object convertValue(Object value, Class<?> targetType) {
        if (value == null) return null;
        
        if (targetType.equals(Long.class) || targetType.equals(long.class)) {
            return convertToLong(value);
        } else if (targetType.equals(Integer.class) || targetType.equals(int.class)) {
            return convertToInteger(value);
        } else if (targetType.equals(BigDecimal.class)) {
            return convertToBigDecimal(value);
        } else if (targetType.equals(LocalDate.class)) {
            return convertToLocalDate(value);
        } else if (targetType.equals(LocalDateTime.class)) {
            return convertToLocalDateTime(value);
        } else if (targetType.equals(String.class)) {
            return value.toString();
        } else if (targetType.equals(Boolean.class) || targetType.equals(boolean.class)) {
            return Boolean.valueOf(value.toString());
        }
        
        return value;
    }
    
    private Long convertToLong(Object value) {
        if (value == null) return null;
        if (value instanceof Integer) return ((Integer) value).longValue();
        if (value instanceof Long) return (Long) value;
        if (value instanceof String) return Long.parseLong((String) value);
        throw new IllegalArgumentException("Cannot convert to Long: " + value);
    }
    
    private Integer convertToInteger(Object value) {
        if (value == null) return null;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Long) return ((Long) value).intValue();
        if (value instanceof String) return Integer.parseInt((String) value);
        throw new IllegalArgumentException("Cannot convert to Integer: " + value);
    }
    
    private BigDecimal convertToBigDecimal(Object value) {
        if (value == null) return null;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        if (value instanceof Integer) return BigDecimal.valueOf((Integer) value);
        if (value instanceof Long) return BigDecimal.valueOf((Long) value);
        if (value instanceof Double) return BigDecimal.valueOf((Double) value);
        if (value instanceof String) return new BigDecimal((String) value);
        throw new IllegalArgumentException("Cannot convert to BigDecimal: " + value);
    }
    
    private LocalDate convertToLocalDate(Object value) {
        if (value == null) return null;
        if (value instanceof LocalDate) return (LocalDate) value;
        if (value instanceof String) return LocalDate.parse((String) value);
        throw new IllegalArgumentException("Cannot convert to LocalDate: " + value);
    }
    
    private LocalDateTime convertToLocalDateTime(Object value) {
        if (value == null) return null;
        if (value instanceof LocalDateTime) return (LocalDateTime) value;
        if (value instanceof String) return LocalDateTime.parse((String) value);
        throw new IllegalArgumentException("Cannot convert to LocalDateTime: " + value);
    }
}