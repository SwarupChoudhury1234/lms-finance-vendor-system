package com.graphy.lms.scheduler;

import com.graphy.lms.entity.*;
import com.graphy.lms.service.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private FeeService feeService;

    @Autowired
    private EmailService emailService;

    /**
     * Apply late fees daily at 2:00 AM
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void applyLateFees() {
        logger.info("Scheduled Task: Applying late fees - Started");
        try {
            feeService.applyLateFees();
            logger.info("Scheduled Task: Applying late fees - Completed successfully");
        } catch (Exception e) {
            logger.error("Scheduled Task: Applying late fees - Failed: {}", e.getMessage());
        }
    }

    /**
     * Process auto-debits daily at 3:00 AM
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void processAutoDebits() {
        logger.info("Scheduled Task: Processing auto-debits - Started");
        try {
            feeService.processAutoDebit();
            logger.info("Scheduled Task: Processing auto-debits - Completed successfully");
        } catch (Exception e) {
            logger.error("Scheduled Task: Processing auto-debits - Failed: {}", e.getMessage());
        }
    }

    /**
     * Send due reminders 3 days before due date at 9:00 AM daily
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendDueReminders() {
        logger.info("Scheduled Task: Sending due reminders - Started");
        try {
            LocalDate reminderDate = LocalDate.now().plusDays(3);
            
            List<StudentInstallmentPlan> upcomingInstallments = feeService.getAllInstallmentPlans()
                    .stream()
                    .filter(plan -> plan.getDueDate().equals(reminderDate))
                    .filter(plan -> plan.getStatus() == StudentInstallmentPlan.InstallmentStatus.PENDING)
                    .toList();

            for (StudentInstallmentPlan installment : upcomingInstallments) {
                try {
                    // Get student allocation details
                    var allocation = feeService.getFeeAllocationById(
                            installment.getStudentFeeAllocationId());
                    
                    // Send reminder email
                    emailService.sendDueReminderEmail(
                            "student@example.com", // TODO: Get actual email from user service
                            "Student Name", // TODO: Get actual name from user service
                            installment.getInstallmentAmount(),
                            installment.getDueDate()
                    );
                    
                    logger.debug("Due reminder sent for installment: {}", installment.getId());
                } catch (Exception e) {
                    logger.error("Failed to send due reminder for installment {}: {}", 
                               installment.getId(), e.getMessage());
                }
            }

            logger.info("Scheduled Task: Sending due reminders - Completed ({} reminders sent)", 
                       upcomingInstallments.size());
        } catch (Exception e) {
            logger.error("Scheduled Task: Sending due reminders - Failed: {}", e.getMessage());
        }
    }

    /**
     * Send overdue warnings daily at 10:00 AM
     */
    @Scheduled(cron = "0 0 10 * * ?")
    public void sendOverdueWarnings() {
        logger.info("Scheduled Task: Sending overdue warnings - Started");
        try {
            List<StudentInstallmentPlan> overdueInstallments = feeService.getOverdueInstallments();

            for (StudentInstallmentPlan installment : overdueInstallments) {
                try {
                    // Get student allocation details
                    var allocation = feeService.getFeeAllocationById(
                            installment.getStudentFeeAllocationId());
                    
                    // Calculate days overdue
                    long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(
                            installment.getDueDate(), LocalDate.now());
                    
                    // Get total late fees
                    var lateFees = feeService.getLateFeePenaltiesByInstallmentId(installment.getId());
                    java.math.BigDecimal totalLateFee = lateFees.stream()
                            .map(com.graphy.lms.entity.LateFeePenalty::getPenaltyAmount)
                            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
                    
                    // Send overdue warning
                    emailService.sendOverdueWarningEmail(
                            "student@example.com", // TODO: Get actual email
                            "Student Name", // TODO: Get actual name
                            installment.getInstallmentAmount(),
                            totalLateFee,
                            (int) daysOverdue
                    );
                    
                    logger.debug("Overdue warning sent for installment: {}", installment.getId());
                } catch (Exception e) {
                    logger.error("Failed to send overdue warning for installment {}: {}", 
                               installment.getId(), e.getMessage());
                }
            }

            logger.info("Scheduled Task: Sending overdue warnings - Completed ({} warnings sent)", 
                       overdueInstallments.size());
        } catch (Exception e) {
            logger.error("Scheduled Task: Sending overdue warnings - Failed: {}", e.getMessage());
        }
    }

    /**
     * Generate monthly revenue report on 1st of every month at 8:00 AM
     */
    @Scheduled(cron = "0 0 8 1 * ?")
    public void generateMonthlyReport() {
        logger.info("Scheduled Task: Generating monthly revenue report - Started");
        try {
            LocalDate lastMonth = LocalDate.now().minusMonths(1);
            var report = feeService.getMonthlyRevenueReport(
                    lastMonth.getYear(), 
                    lastMonth.getMonthValue()
            );
            
            logger.info("Monthly Revenue Report: {}", report);
            // TODO: Send report via email to admin
            
            logger.info("Scheduled Task: Generating monthly revenue report - Completed");
        } catch (Exception e) {
            logger.error("Scheduled Task: Generating monthly revenue report - Failed: {}", e.getMessage());
        }
    }
}