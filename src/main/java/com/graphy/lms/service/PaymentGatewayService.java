package com.graphy.lms.service;

import com.graphy.lms.exception.PaymentProcessingException;
import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentGatewayService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentGatewayService.class);

    @Autowired
    private RazorpayClient razorpayClient;

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Value("${razorpay.currency}")
    private String currency;

    @Value("${razorpay.company.name}")
    private String companyName;

    /**
     * Create Razorpay order for payment
     */
    public Map<String, String> createOrder(BigDecimal amount, String receiptId, 
                                           String studentName, String studentEmail) {
        try {
            // Convert amount to paise (smallest currency unit)
            int amountInPaise = amount.multiply(BigDecimal.valueOf(100)).intValue();

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", receiptId);

            // Add notes for reference
            JSONObject notes = new JSONObject();
            notes.put("student_name", studentName);
            notes.put("student_email", studentEmail);
            orderRequest.put("notes", notes);

            Order order = razorpayClient.orders.create(orderRequest);

            Map<String, String> response = new HashMap<>();
            response.put("orderId", order.get("id"));
            response.put("amount", String.valueOf(amountInPaise));
            response.put("currency", currency);
            response.put("keyId", keyId);
            response.put("companyName", companyName);

            logger.info("Razorpay order created successfully: {}", String.valueOf(order.get("id")));
            return response;

        } catch (RazorpayException e) {
            logger.error("Failed to create Razorpay order: {}", e.getMessage());
            throw new PaymentProcessingException("Failed to create payment order: " + e.getMessage());
        }
    }

    /**
     * Verify payment signature
     */
    public boolean verifyPaymentSignature(String orderId, String paymentId, String signature) {
        try {
            String payload = orderId + "|" + paymentId;
            String generatedSignature = generateHmacSHA256(payload, keySecret);

            boolean isValid = generatedSignature.equals(signature);
            
            if (isValid) {
                logger.info("Payment signature verified successfully for payment: {}", paymentId);
            } else {
                logger.warn("Payment signature verification failed for payment: {}", paymentId);
            }

            return isValid;

        } catch (Exception e) {
            logger.error("Error verifying payment signature: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Fetch payment details from Razorpay
     */
    public Map<String, Object> getPaymentDetails(String paymentId) {
        try {
            Payment payment = razorpayClient.payments.fetch(paymentId);

            Map<String, Object> details = new HashMap<>();
            details.put("paymentId", payment.get("id"));
            details.put("orderId", payment.get("order_id"));
            details.put("amount", payment.get("amount"));
            details.put("currency", payment.get("currency"));
            details.put("status", payment.get("status"));
            details.put("method", payment.get("method"));
            details.put("email", payment.get("email"));
            details.put("contact", payment.get("contact"));
            details.put("createdAt", payment.get("created_at"));

            logger.info("Fetched payment details for payment: {}", paymentId);
            return details;

        } catch (RazorpayException e) {
            logger.error("Failed to fetch payment details: {}", e.getMessage());
            throw new PaymentProcessingException("Failed to fetch payment details: " + e.getMessage());
        }
    }

    /**
     * Initiate refund
     */
    public Map<String, Object> initiateRefund(String paymentId, BigDecimal refundAmount, String reason) {
        try {
            int amountInPaise = refundAmount.multiply(BigDecimal.valueOf(100)).intValue();

            JSONObject refundRequest = new JSONObject();
            refundRequest.put("amount", amountInPaise);

            JSONObject notes = new JSONObject();
            notes.put("reason", reason);
            refundRequest.put("notes", notes);

            Payment payment = razorpayClient.payments.fetch(paymentId);
            com.razorpay.Refund refund = razorpayClient.payments.refund(paymentId, refundRequest);

            Map<String, Object> response = new HashMap<>();
            response.put("refundId", refund.get("id"));
            response.put("paymentId", refund.get("payment_id"));
            response.put("amount", refund.get("amount"));
            response.put("status", refund.get("status"));
            response.put("createdAt", refund.get("created_at"));

            logger.info("Refund initiated successfully: {}", String.valueOf(refund.get("id")));
            return response;

        } catch (RazorpayException e) {
            logger.error("Failed to initiate refund: {}", e.getMessage());
            throw new PaymentProcessingException("Failed to initiate refund: " + e.getMessage());
        }
    }

    /**
     * Check refund status
     */
    public String getRefundStatus(String refundId) {
        try {
            com.razorpay.Refund refund = razorpayClient.refunds.fetch(refundId);
            String status = refund.get("status");
            
            logger.info("Refund status for {}: {}", refundId, status);
            return status;

        } catch (RazorpayException e) {
            logger.error("Failed to check refund status: {}", e.getMessage());
            throw new PaymentProcessingException("Failed to check refund status: " + e.getMessage());
        }
    }

    /**
     * Generate HMAC SHA256 signature
     */
    private String generateHmacSHA256(String data, String secret) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        
        byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        
        return hexString.toString();
    }
}