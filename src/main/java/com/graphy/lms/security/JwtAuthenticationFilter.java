package com.graphy.lms.security; // Keep your package name

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                // 1. Validate the Token Signature (Stateless check)
                if (jwtUtil.validateToken(token)) {

                    // 2. Extract Claims directly from Token (No Database Call needed)
                    Long userId = jwtUtil.extractUserId(token);
                    String email = jwtUtil.extractEmail(token);
                    List<String> roles = jwtUtil.extractRoles(token);
                    List<String> permissions = jwtUtil.extractPermissions(token);

                    // 3. Build Authorities (Roles + Permissions)
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();

                    // Handle Roles
                    if (roles != null) {
                        for (String role : roles) {
                            // Ensure "ROLE_" prefix exists (Standard Spring Security)
                            String authRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                            authorities.add(new SimpleGrantedAuthority(authRole));
                        }
                    }

                    // Handle Permissions
                    if (permissions != null) {
                        for (String perm : permissions) {
                            authorities.add(new SimpleGrantedAuthority(perm));
                        }
                    }

                    // 4. Create Authentication Object
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userId, // Principal is User ID
                            null,   // No credentials needed
                            authorities
                    );

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 5. Log User In (Set Context)
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // 6. Set Request Attributes for Controllers
                    request.setAttribute("userId", userId);
                    request.setAttribute("userEmail", email);
                    // Legacy support: set simple "role" attribute (e.g. "ADMIN")
                    if (roles != null && !roles.isEmpty()) {
                        request.setAttribute("role", roles.get(0).replace("ROLE_", ""));
                    }
                }
            } catch (Exception e) {
                logger.error("Authentication Error: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}