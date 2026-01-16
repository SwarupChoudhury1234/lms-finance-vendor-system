package com.graphy.lms.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserContext {

    // Get current authenticated user ID
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return (Long) authentication.getPrincipal();
        }
        throw new RuntimeException("User not authenticated");
    }

    // Get current user role
    public String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            String authority = authentication.getAuthorities().iterator().next().getAuthority();
            return authority.replace("ROLE_", ""); // Remove "ROLE_" prefix
        }
        throw new RuntimeException("User role not found");
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