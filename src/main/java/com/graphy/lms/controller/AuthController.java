package com.graphy.lms.controller;

import com.graphy.lms.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/fee-management/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    // ❌ REMOVED: /generate-token 
    // Reason: You are a Resource Server. You strictly consume Santosh's tokens. 
    // Do not generate them locally.

    /**
     * Validate token (Debug Endpoint)
     * Use this to check if Santosh's token is valid in your system.
     */
    @PostMapping("/validate-token")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();

        // 1. Check Header Format
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put("valid", false);
            response.put("error", "Invalid Header Format");
            return ResponseEntity.ok(response);
        }

        String token = authHeader.substring(7);

        // 2. Validate (Checks Signature & Expiration internally)
        boolean isValid = jwtUtil.validateToken(token);
        response.put("valid", isValid);

        if (isValid) {
            // 3. Extract Info using the NEW methods (Lists)
            response.put("userId", jwtUtil.extractUserId(token));
            response.put("email", jwtUtil.extractEmail(token));
            
            // Santosh sends a LIST of roles, not a single role
            response.put("roles", jwtUtil.extractRoles(token)); 
            
            // Extract permissions if needed
            response.put("permissions", jwtUtil.extractPermissions(token));
        }

        return ResponseEntity.ok(response);
    }
}