package com.graphy.lms.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccessControlService {

    @Autowired
    private UserContext userContext;

    // Check if user can access specific resource
    public boolean canAccessUserData(Long targetUserId) {
        String role = userContext.getCurrentUserRole();
        Long currentUserId = userContext.getCurrentUserId();

        switch (role) {
            case "ADMIN":
                return true; // Admin can access all
            case "FACULTY":
                return true; // Faculty can view (limited by endpoints)
            case "STUDENT":
                return currentUserId.equals(targetUserId); // Students can only access their own data
            case "PARENT":
                // TODO: Implement parent-child relationship check
                // For now, allow if parent ID matches
                return currentUserId.equals(targetUserId);
            default:
                return false;
        }
    }

    // Validate access or throw exception
    public void validateAccess(Long targetUserId, String operation) {
        if (!canAccessUserData(targetUserId)) {
            throw new RuntimeException("Access Denied: You don't have permission to " + operation);
        }
    }

    // Check if user can perform CREATE operations
    public boolean canCreate() {
        return userContext.isAdmin();
    }

    // Check if user can perform UPDATE operations
    public boolean canUpdate() {
        return userContext.isAdmin();
    }

    // Check if user can perform DELETE operations
    public boolean canDelete() {
        return userContext.isAdmin();
    }

    // Check if user can view all records
    public boolean canViewAll() {
        String role = userContext.getCurrentUserRole();
        return "ADMIN".equals(role) || "FACULTY".equals(role);
    }
}