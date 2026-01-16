package com.graphy.lms.controller;

import com.graphy.lms.entity.*;

import com.graphy.lms.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/fee-management/payment-gateway")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentGatewayService paymentGatewayService;

    @Autowired
    private FeeService feeManagementService;

    /**
     * Create payment order (Student initiates payment)
     */
    @PostMapping("/create-order")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<Map<String, String>> createPaymentOrder(
            @RequestParam Long allocationId,
            @RequestParam BigDecimal amount,
            @RequestParam String studentName,
            @RequestParam String studentEmail) {
        
        StudentFeeAllocation allocation = feeManagementService.getFeeAllocationById(allocationId);
        String receiptId = "RCPT-" + System.currentTimeMillis();

        Map<String, String> orderDetails = paymentGatewayService.createOrder(
                amount, receiptId, studentName, studentEmail);

        orderDetails.put("allocationId", String.valueOf(allocationId));
        orderDetails.put("receiptId", receiptId);

        return ResponseEntity.ok(orderDetails);
    }

    /**
     * Verify and complete payment (After Razorpay callback)
     */
    @PostMapping("/verify-payment")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<Map<String, Object>> verifyAndCompletePayment(
            @RequestBody Map<String, String> paymentData) {
        
        String orderId = paymentData.get("razorpay_order_id");
        String paymentId = paymentData.get("razorpay_payment_id");
        String signature = paymentData.get("razorpay_signature");
        Long allocationId = Long.valueOf(paymentData.get("allocationId"));
        Long installmentPlanId = paymentData.containsKey("installmentPlanId") ? 
                                 Long.valueOf(paymentData.get("installmentPlanId")) : null;

        Map<String, Object> response = new HashMap<>();

        // Verify signature
        boolean isValid = paymentGatewayService.verifyPaymentSignature(orderId, paymentId, signature);

        if (!isValid) {
            response.put("success", false);
            response.put("message", "Payment verification failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Fetch payment details
        Map<String, Object> paymentDetails = paymentGatewayService.getPaymentDetails(paymentId);
        
        BigDecimal amount = new BigDecimal(paymentDetails.get("amount").toString()).divide(BigDecimal.valueOf(100));
        String paymentMethod = paymentDetails.get("method").toString().toUpperCase();

        // Record payment in database
        StudentFeePayment payment = feeManagementService.processOnlinePayment(
                allocationId,
                installmentPlanId,
                amount,
                paymentMethod,
                paymentId,
                "Razorpay Payment Successful"
        );

        response.put("success", true);
        response.put("message", "Payment processed successfully");
        response.put("paymentId", payment.getId());
        response.put("receiptNumber", payment.getTransactionReference());

        return ResponseEntity.ok(response);
    }

    /**
     * Initiate refund
     */
    @PostMapping("/initiate-refund")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> initiateRefund(
            @RequestParam String razorpayPaymentId,
            @RequestParam BigDecimal refundAmount,
            @RequestParam String reason) {
        
        Map<String, Object> refundDetails = paymentGatewayService.initiateRefund(
                razorpayPaymentId, refundAmount, reason);

        return ResponseEntity.ok(refundDetails);
    }

    /**
     * Check refund status
     */
    @GetMapping("/refund-status/{refundId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> checkRefundStatus(@PathVariable String refundId) {
        String status = paymentGatewayService.getRefundStatus(refundId);

        Map<String, String> response = new HashMap<>();
        response.put("refundId", refundId);
        response.put("status", status);

        return ResponseEntity.ok(response);
    }
}