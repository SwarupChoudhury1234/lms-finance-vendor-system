package com.graphy.lms.controller;

import com.graphy.lms.security.*;
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

    /**
     * Generate JWT token for testing
     * In production, this would authenticate against user database
     */
    @PostMapping("/generate-token")
    public ResponseEntity<Map<String, String>> generateToken(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        String role = request.get("role").toString(); // ADMIN, FACULTY, STUDENT, PARENT

        String token = jwtUtil.generateToken(userId, role);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", userId.toString());
        response.put("role", role);

        return ResponseEntity.ok(response);
    }

    /**
     * Validate token
     */
    @PostMapping("/validate-token")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);

        boolean isValid = jwtUtil.validateToken(token) && !jwtUtil.isTokenExpired(token);

        Map<String, Object> response = new HashMap<>();
        response.put("valid", isValid);

        if (isValid) {
            response.put("userId", jwtUtil.extractUserId(token));
            response.put("role", jwtUtil.extractRole(token));
        }

        return ResponseEntity.ok(response);
    }
}