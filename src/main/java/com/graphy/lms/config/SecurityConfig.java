package com.graphy.lms.config; // Keep your package name

import com.graphy.lms.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity // Enables @PreAuthorize
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configure(http)) // Allow CORS

            // Define URL Access Rules
            .authorizeHttpRequests(auth -> auth
                // Public Endpoints (if any)
                .requestMatchers("/api/fee-management/public/**").permitAll()
                // Swagger UI (Optional)
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                // All other requests require a Valid Token
                .anyRequest().authenticated()
            )

            // Stateless Session (No Cookies)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Add our Filter before the standard Spring Security Filter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}