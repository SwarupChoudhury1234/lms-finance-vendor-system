package com.graphy.lms.service;

import java.util.List;
import java.util.Map;

public interface FeeService {
    
    // ========== CRUD OPERATIONS FOR ALL 23 ENTITIES ==========
    
    /**
     * Create a new entity
     * @param entityType Type of entity to create
     * @param data Entity data in key-value pairs
     * @return Created entity
     */
    Object create(EntityType entityType, Map<String, Object> data);
    
    /**
     * Get entity by ID
     * @param entityType Type of entity
     * @param id Entity ID
     * @return Entity object
     */
    Object getById(EntityType entityType, Long id);
    
    /**
     * Get all entities of a type
     * @param entityType Type of entity
     * @return List of entities
     */
    List<?> getAll(EntityType entityType);
    
    /**
     * Update an existing entity
     * @param entityType Type of entity
     * @param id Entity ID
     * @param data Updated data
     * @return Updated entity
     */
    Object update(EntityType entityType, Long id, Map<String, Object> data);
    
    /**
     * Delete an entity
     * @param entityType Type of entity
     * @param id Entity ID
     */
    void delete(EntityType entityType, Long id);
    
    /**
     * Check if entity exists
     * @param entityType Type of entity
     * @param id Entity ID
     * @return true if exists
     */
    boolean exists(EntityType entityType, Long id);
    
    /**
     * Count entities of a type
     * @param entityType Type of entity
     * @return Count of entities
     */
    long count(EntityType entityType);
    
    // ========== BUSINESS LOGIC METHODS FOR 20 REQUIREMENTS ==========
    
    /**
     * Requirement #4: Auto calculate discounts
     * @param discountRequest Contains original amount and list of discounts
     * @return Calculated payable amount with discount breakdown
     */
    Object calculateDiscount(Map<String, Object> discountRequest);
    
    /**
     * Requirement #5: Create installment plans with alternatives
     * @param installmentRequest Contains payable amount, advance paid, student ID
     * @return Installment options with suggested amounts
     */
    Object createInstallmentPlan(Map<String, Object> installmentRequest);
    
    /**
     * Requirement #7: Generate payment link for student
     * @param paymentLinkRequest Contains allocation ID, email, student details
     * @return Payment link with token and expiry
     */
    Object generatePaymentLink(Map<String, Object> paymentLinkRequest);
    
    /**
     * Requirement #9: Apply late fee for overdue payments
     * @param lateFeeRequest Contains allocation ID, overdue months, penalty rules
     * @return Late fee calculation result
     */
    Object applyLateFee(Map<String, Object> lateFeeRequest);
    
    /**
     * Requirements #11 & #12: Generate various reports
     * @param reportRequest Contains report type and filters
     * @return Report data based on type
     */
    Object generateReport(Map<String, Object> reportRequest);
    
    /**
     * Requirement #13: Process refund request
     * @param refundRequest Contains payment ID, refund amount, reason
     * @return Refund processing result
     */
    Object processRefund(Map<String, Object> refundRequest);
    
    /**
     * Requirement #16: Apply attendance penalty
     * @param penaltyRequest Contains student ID, absent days, penalty rate
     * @return Penalty calculation result
     */
    Object applyAttendancePenalty(Map<String, Object> penaltyRequest);
    
    /**
     * Requirement #18: Check certificate block status
     * @param studentId Student ID
     * @return Certificate status information
     */
    Object getCertificateStatus(Long studentId);
    
    /**
     * Requirement #20: Setup auto debit
     * @param autoDebitRequest Contains payment method, account details
     * @return Auto debit setup result
     */
    Object setupAutoDebit(Map<String, Object> autoDebitRequest);
    
    /**
     * Requirement #17: Link exam fee automatically
     * @param examFeeRequest Contains exam ID and fee structure
     * @return Exam fee linking result
     */
    Object linkExamFee(Map<String, Object> examFeeRequest);
    
    /**
     * Requirement #19: Generate recurring invoice
     * @param invoiceRequest Contains template, frequency, student details
     * @return Generated invoice
     */
    Object generateInvoice(Map<String, Object> invoiceRequest);
    
    /**
     * Requirement #15: Currency conversion
     * @param conversionRequest Contains amount, from currency, to currency
     * @return Converted amount
     */
    Object convertCurrency(Map<String, Object> conversionRequest);
    
    /**
     * Requirement #8: Get payment history
     * @param studentId Student ID
     * @return Payment history with status
     */
    Object getPaymentHistory(Long studentId);
    
    /**
     * Requirement #14: Get audit trail
     * @param entityType Type of entity
     * @param entityId Entity ID
     * @return Audit logs for the entity
     */
    Object getAuditTrail(EntityType entityType, Long entityId);
    
    /**
     * Requirement #10: Process online payment
     * @param paymentRequest Contains payment details, gateway info
     * @return Payment processing result
     */
    Object processOnlinePayment(Map<String, Object> paymentRequest);
    
    /**
     * Requirement #11: Send payment notification
     * @param notificationRequest Contains recipient, message type, content
     * @return Notification sending result
     */
    Object sendNotification(Map<String, Object> notificationRequest);
}