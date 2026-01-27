package com.graphy.lms.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class HeaderRoleFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Extract Role AND User ID (Santosh's Requirement)
        String role = request.getHeader("X-ROLE");
        String userIdStr = request.getHeader("X-USER-ID");

        // 2. Only run if headers exist AND no other auth (like JWT) happened yet
        if (role != null && userIdStr != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            try {
                // 3. Convert String ID to Long (Crucial for UserContext)
                Long userId = Long.parseLong(userIdStr);

                System.out.println(">>> Header Auth: User=" + userId + ", Role=" + role);

                // 4. Create Authentication with Long ID
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userId, // 🔴 MUST BE LONG, NOT STRING "user"
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);
                
            } catch (NumberFormatException e) {
                System.err.println(">>> Header Auth Failed: Invalid X-USER-ID format: " + userIdStr);
            }
        }

        filterChain.doFilter(request, response);
    }
}