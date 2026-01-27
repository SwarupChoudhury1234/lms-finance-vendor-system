package com.graphy.lms.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserContext {

    // Get current authenticated user ID
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof Long) {
            return (Long) authentication.getPrincipal();
        }
        throw new RuntimeException("User not authenticated or invalid principal type");
    }

    // Get current user role (Safely finds the ROLE_ entry)
    public String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getAuthorities() != null) {
            // Loop through all authorities to find the one that starts with "ROLE_"
            // This ignores PERMISSIONS (which don't start with ROLE_) and finds the actual Role.
            for (GrantedAuthority auth : authentication.getAuthorities()) {
                String authority = auth.getAuthority();
                if (authority.startsWith("ROLE_")) {
                    return authority.replace("ROLE_", ""); // Found it! Return "ADMIN"
                }
            }
        }
        
        // Fallback or Exception if no role is found (but user might still be authenticated)
        throw new RuntimeException("User role not found in security context");
    }

    // Check if current user is Admin
    public boolean isAdmin() {
        return "ADMIN".equals(getCurrentUserRole());
    }

    // Check if current user is Faculty
    public boolean isFaculty() {
        return "FACULTY".equals(getCurrentUserRole());
    }

    // Check if current user is Student
    public boolean isStudent() {
        return "STUDENT".equals(getCurrentUserRole());
    }

    // Check if current user is Parent
    public boolean isParent() {
        return "PARENT".equals(getCurrentUserRole());
    }
}