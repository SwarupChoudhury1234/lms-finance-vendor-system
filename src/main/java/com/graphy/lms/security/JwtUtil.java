package com.graphy.lms.security; // Keep your package name

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.List;

@Component
public class JwtUtil {

    // 🔴 IMPORTANT: This MUST match Santosh's secret key exactly
    @Value("${jwt.secret}")
    private String secret;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token) // This line validates the signature automatically
                .getBody();
    }

    // 1. Validate Token (Signature Check)
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            System.err.println("❌ JWT Validation Error: " + e.getMessage());
            return false;
        }
    }

    // 2. Extract User ID (Matches Santosh's "userId" claim)
    public Long extractUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }

    // 3. Extract Email (Subject)
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    // 4. Extract Roles (Matches Santosh's "roles" list)
    public List<String> extractRoles(String token) {
        try {
            return extractAllClaims(token).get("roles", List.class);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // 5. Extract Permissions (Matches Santosh's "permissions" list)
    public List<String> extractPermissions(String token) {
        try {
            return extractAllClaims(token).get("permissions", List.class);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}