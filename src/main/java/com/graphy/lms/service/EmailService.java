package com.graphy.lms.service;

import com.graphy.lms.entity.*;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Send payment success email with receipt attachment
     */
    public void sendPaymentSuccessEmail(String toEmail, String studentName, String receiptNumber, 
                                       BigDecimal amount, String currency, byte[] receiptPdf) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("Payment Successful - Receipt " + receiptNumber);
            
            String emailBody = buildPaymentSuccessEmailBody(studentName, receiptNumber, amount, currency);
            helper.setText(emailBody, true);

            // Attach PDF receipt
            if (receiptPdf != null && receiptPdf.length > 0) {
                helper.addAttachment("Receipt_" + receiptNumber + ".pdf", 
                                   new ByteArrayResource(receiptPdf));
            }

            mailSender.send(message);
            logger.info("Payment success email sent to: {}", toEmail);
            
        } catch (MessagingException e) {
            logger.error("Failed to send payment success email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
    /**
     * Send email when a new fee is assigned (Trigger: On Creation)
     */
    public void sendFeeAssignedEmail(String toEmail, String studentName, String feeType, 
                                     BigDecimal amount, LocalDate dueDate) {
        try {
            jakarta.mail.internet.MimeMessage message = mailSender.createMimeMessage();
            org.springframework.mail.javamail.MimeMessageHelper helper = 
                new org.springframework.mail.javamail.MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("New Fee Assigned: " + feeType);
            
            String emailBody = "<html>" +
                    "<body style='font-family: Arial, sans-serif;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd;'>" +
                    "<h2 style='color: #007bff;'>New Fee Assigned</h2>" +
                    "<p>Dear " + studentName + ",</p>" +
                    "<p>A new fee has been assigned to your account.</p>" +
                    "<div style='background-color: #f8f9fa; padding: 15px; margin: 20px 0; border-radius: 5px;'>" +
                    "<p><strong>Fee Type:</strong> " + feeType + "</p>" +
                    "<p><strong>Amount:</strong> ₹" + amount + "</p>" +
                    "<p><strong>Due Date:</strong> " + dueDate + "</p>" +
                    "</div>" +
                    "<p>Please login to your portal to view details and make a payment.</p>" +
                    "<hr style='margin-top: 30px;'>" +
                    "<p style='color: #666; font-size: 12px;'>This is an automated email.</p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(emailBody, true);
            mailSender.send(message);
            
            logger.info("Fee assignment email sent to {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send fee assignment email: {}", e.getMessage());
        }
    }

    /**
     * Send payment failed notification
     */
    public void sendPaymentFailedEmail(String toEmail, String studentName, 
                                      BigDecimal amount, String reason) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Payment Failed - Action Required");
            
            String emailBody = "Dear " + studentName + ",\n\n" +
                    "Your payment of " + amount + " has failed.\n\n" +
                    "Reason: " + reason + "\n\n" +
                    "Please try again or contact our support team.\n\n" +
                    "Payment Link: [Click here to retry payment]\n\n" +
                    "Best regards,\n" +
                    "LMS Fee Management Team";
            
            message.setText(emailBody);
            mailSender.send(message);
            
            logger.info("Payment failed email sent to: {}", toEmail);
            
        } catch (Exception e) {
            logger.error("Failed to send payment failed email to {}: {}", toEmail, e.getMessage());
        }
    }

    /**
     * Send due reminder (3 days before due date)
     */
    public void sendDueReminderEmail(String toEmail, String studentName, 
                                    BigDecimal amount, LocalDate dueDate) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Fee Payment Reminder - Due on " + 
                             dueDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));
            
            String emailBody = "Dear " + studentName + ",\n\n" +
                    "This is a friendly reminder that your fee payment is due soon.\n\n" +
                    "Amount Due: ₹" + amount + "\n" +
                    "Due Date: " + dueDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")) + "\n\n" +
                    "Please make the payment before the due date to avoid late fees.\n\n" +
                    "Payment Link: [Click here to pay online]\n\n" +
                    "If you have already made the payment, please ignore this email.\n\n" +
                    "Best regards,\n" +
                    "LMS Fee Management Team";
            
            message.setText(emailBody);
            mailSender.send(message);
            
            logger.info("Due reminder email sent to: {}", toEmail);
            
        } catch (Exception e) {
            logger.error("Failed to send due reminder email to {}: {}", toEmail, e.getMessage());
        }
    }

    /**
     * Send overdue warning with late fee information
     */
    public void sendOverdueWarningEmail(String toEmail, String studentName, 
                                       BigDecimal amount, BigDecimal lateFee, 
                                       int daysOverdue) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("⚠️ URGENT: Overdue Payment - Late Fee Applied");
            
            BigDecimal totalDue = amount.add(lateFee);
            
            String emailBody = "Dear " + studentName + ",\n\n" +
                    "⚠️ IMPORTANT NOTICE ⚠️\n\n" +
                    "Your payment is OVERDUE by " + daysOverdue + " day(s).\n\n" +
                    "Original Amount: ₹" + amount + "\n" +
                    "Late Fee Applied: ₹" + lateFee + "\n" +
                    "Total Amount Due: ₹" + totalDue + "\n\n" +
                    "Please clear your dues immediately to avoid:\n" +
                    "- Additional late fees\n" +
                    "- Certificate issuance blockage\n" +
                    "- Suspension of academic services\n\n" +
                    "Payment Link: [Click here to pay now]\n\n" +
                    "For any queries, contact our accounts department.\n\n" +
                    "Best regards,\n" +
                    "LMS Fee Management Team";
            
            message.setText(emailBody);
            mailSender.send(message);
            
            logger.info("Overdue warning email sent to: {}", toEmail);
            
        } catch (Exception e) {
            logger.error("Failed to send overdue warning email to {}: {}", toEmail, e.getMessage());
        }
    }

    /**
     * Send refund processed notification
     */
    public void sendRefundProcessedEmail(String toEmail, String studentName, 
                                        BigDecimal refundAmount, String transactionRef) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Refund Processed Successfully");
            
            String emailBody = "Dear " + studentName + ",\n\n" +
                    "Your refund has been processed successfully.\n\n" +
                    "Refund Amount: ₹" + refundAmount + "\n" +
                    "Transaction Reference: " + transactionRef + "\n\n" +
                    "The amount will be credited to your account within 5-7 business days.\n\n" +
                    "Best regards,\n" +
                    "LMS Fee Management Team";
            
            message.setText(emailBody);
            mailSender.send(message);
            
            logger.info("Refund processed email sent to: {}", toEmail);
            
        } catch (Exception e) {
            logger.error("Failed to send refund processed email to {}: {}", toEmail, e.getMessage());
        }
    }

    /**
     * Send installment plan created notification
     */
    public void sendInstallmentPlanCreatedEmail(String toEmail, String studentName, 
                                               int numberOfInstallments, 
                                               BigDecimal totalAmount) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Installment Plan Created Successfully");
            
            String emailBody = "Dear " + studentName + ",\n\n" +
                    "Your payment plan has been created successfully.\n\n" +
                    "Total Amount: ₹" + totalAmount + "\n" +
                    "Number of Installments: " + numberOfInstallments + "\n\n" +
                    "You can view your installment details by logging into your account.\n\n" +
                    "Best regards,\n" +
                    "LMS Fee Management Team";
            
            message.setText(emailBody);
            mailSender.send(message);
            
            logger.info("Installment plan email sent to: {}", toEmail);
            
        } catch (Exception e) {
            logger.error("Failed to send installment plan email to {}: {}", toEmail, e.getMessage());
        }
    }

    /**
     * Build HTML email body for payment success
     */
    private String buildPaymentSuccessEmailBody(String studentName, String receiptNumber, 
                                                BigDecimal amount, String currency) {
        return "<html>" +
                "<body style='font-family: Arial, sans-serif;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd;'>" +
                "<h2 style='color: #28a745;'>✓ Payment Successful</h2>" +
                "<p>Dear " + studentName + ",</p>" +
                "<p>Your payment has been received successfully.</p>" +
                "<div style='background-color: #f8f9fa; padding: 15px; margin: 20px 0; border-radius: 5px;'>" +
                "<p><strong>Receipt Number:</strong> " + receiptNumber + "</p>" +
                "<p><strong>Amount Paid:</strong> " + currency + " " + amount + "</p>" +
                "<p><strong>Payment Date:</strong> " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")) + "</p>" +
                "</div>" +
                "<p>Please find the receipt attached with this email.</p>" +
                "<p>Thank you for your payment!</p>" +
                "<hr style='margin-top: 30px;'>" +
                "<p style='color: #666; font-size: 12px;'>This is an automated email. Please do not reply.</p>" +
                "<p style='color: #666; font-size: 12px;'>LMS Fee Management Team</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}