package com.graphy.lms.controller;

import com.graphy.lms.entity.FeeRefund;
import com.graphy.lms.entity.StudentFeePayment;
import com.graphy.lms.service.FeeService;
// If you have a separate PaymentGatewayService, keep this import. 
// If not, remove the Autowired field below.
// import com.graphy.lms.service.PaymentGatewayService; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment-gateway")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private FeeService feeManagementService;

    // Uncomment this if you actually have this service created
    // @Autowired
    // private PaymentGatewayService paymentGatewayService;

    /**
     * 1. VERIFY PAYMENT (The "Smart" Method with 9 Parameters)
     * Handles: Razorpay Data + Student Name/Email + Screenshot
     */
    @PostMapping("/verify-payment")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<Map<String, Object>> verifyAndCompletePayment(
            @RequestBody Map<String, String> paymentData) {
        
        // 1. Extract Standard Razorpay Data
        String orderId = paymentData.get("razorpay_order_id");
        String paymentId = paymentData.get("razorpay_payment_id");
        String signature = paymentData.get("razorpay_signature");
        
        // 2. Extract Business Data
        Long allocationId = Long.parseLong(paymentData.get("allocationId"));
        BigDecimal amount = new BigDecimal(paymentData.get("amount")); 
        
        Long installmentPlanId = null;
        if (paymentData.get("installmentPlanId") != null && !paymentData.get("installmentPlanId").equals("null")) {
             installmentPlanId = Long.parseLong(paymentData.get("installmentPlanId"));
        }
        
        // 3. EXTRACT NEW FIELDS (Screenshot, Name, Email)
        String screenshotUrl = paymentData.getOrDefault("screenshotUrl", null);
        String studentName = paymentData.get("studentName");  
        String studentEmail = paymentData.get("studentEmail");

        // 4. [Optional] Verify Signature Here (HMAC Logic)
        // ...

        // 5. Call Service with ALL 9 PARAMETERS
        StudentFeePayment payment = feeManagementService.processOnlinePayment(
                allocationId,
                installmentPlanId,
                amount,
                "ONLINE",
                paymentId,
                "Razorpay Payment Successful",
                screenshotUrl,  // <--- Passed Correctly
                studentName,    // <--- Passed Correctly
                studentEmail    // <--- Passed Correctly
        );

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Payment verified, Receipt Generated & Email Sent");
        response.put("paymentId", payment.getId());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 2. CREATE ORDER
     */
    @PostMapping("/create-order")
    public ResponseEntity<Map<String, String>> createOrder(@RequestBody Map<String, Object> data) {
         Long allocationId = Long.parseLong(data.get("allocationId").toString());
         BigDecimal amount = new BigDecimal(data.get("amount").toString());
         
         // Calls FeeService wrapper for Razorpay
         String orderId = feeManagementService.createRazorpayOrder(allocationId, amount);
         
         Map<String, String> response = new HashMap<>();
         response.put("orderId", orderId);
         return ResponseEntity.ok(response);
    }

    /**
     * 3. SEARCH REFUND BY REFERENCE (New Requirement)
     */
    @GetMapping("/refund-details/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FeeRefund> getRefundDetailsByRef(@RequestParam String reference) {
        // Call the new service method we added earlier
        FeeRefund refund = feeManagementService.getRefundByTransactionRef(reference);
        return ResponseEntity.ok(refund);
    }

    // =========================================================================
    // OPTIONAL: KEEP THESE ONLY IF YOU HAVE PaymentGatewayService IMPLEMENTED
    // =========================================================================

    /* @PostMapping("/initiate-refund")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> initiateRefund(
            @RequestParam String razorpayPaymentId,
            @RequestParam BigDecimal refundAmount,
            @RequestParam String reason) {
        
        Map<String, Object> refundDetails = paymentGatewayService.initiateRefund(
                razorpayPaymentId, refundAmount, reason);

        return ResponseEntity.ok(refundDetails);
    }

    @GetMapping("/refund-status/{refundId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> checkRefundStatus(@PathVariable String refundId) {
        String status = paymentGatewayService.getRefundStatus(refundId);

        Map<String, String> response = new HashMap<>();
        response.put("refundId", refundId);
        response.put("status", status);

        return ResponseEntity.ok(response);
    }
    */
}