package com.graphy.lms.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtUtil {

    // 1. INJECT KEY FROM PROPERTIES (Fixes Security & Hardcoding)
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}") // Default to 24h if missing
    private long expirationTime;

    private Key getSigningKey() {
        // Use the instance variable injected by Spring
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // Generate JWT token (Updated to match Santosh's "user_id" format)
    public String generateToken(Long userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", userId); // CHANGED: "userId" -> "user_id"
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extract all claims from token
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 2. FIXED: Extract userId using Santosh's key ("user_id")
    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        
        // Safety Fix: JWT might parse numbers as Integer. This handles both Integer and Long safely.
        if (claims.get("user_id") != null) {
            return ((Number) claims.get("user_id")).longValue();
        }
        return null;
    }

    // Extract role from token
    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }

    // 3. NEW: Extract Permissions (From Santosh's Token)
    public List<String> extractPermissions(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("permissions", List.class);
    }

    // 4. NEW: Extract Linked Student IDs (For Parents/Students)
    public List<Long> extractStudentIds(String token) {
        Claims claims = extractAllClaims(token);
        
        // Case 1: It's a PARENT (List of IDs)
        if (claims.containsKey("student_ids")) {
            return claims.get("student_ids", List.class);
        }
        
        // Case 2: It's a STUDENT (Single ID)
        if (claims.containsKey("student_id")) {
             // Wrap single ID in a list so your logic stays consistent
            return Collections.singletonList(((Number) claims.get("student_id")).longValue());
        }
        
        return Collections.emptyList();
    }

    // Validate token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Check if token is expired
    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
}