package com.graphy.lms.config;

import com.graphy.lms.security.JwtAuthenticationFilter; 
// Ensure HeaderRoleFilter is in the correct package, likely com.graphy.lms.config or .security
import com.graphy.lms.config.HeaderRoleFilter; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private HeaderRoleFilter headerRoleFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Disable CSRF & CORS (Standard for stateless APIs)
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable())

            // 2. Authorization Rules
            .authorizeHttpRequests(auth -> auth
                // Allow Fee Management Auth endpoints publicly
                .requestMatchers("/api/fee-management/auth/**").permitAll()
                
                // Allow Swagger/OpenAPI if you use them (Optional, good practice)
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()

                // ALL other requests (Inventory & Fee) require Authentication
                .anyRequest().authenticated()
            )

            // 3. Stateless Session Management (Best for REST APIs)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 4. Disable default login forms
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())

            // 5. Register Filters
            // We place JWT filter first. If it finds a token, it logs the user in.
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            // We place Header filter after. If JWT failed or wasn't present, 
            // this allows the Inventory logic to try checking headers.
            .addFilterAfter(headerRoleFilter, JwtAuthenticationFilter.class);

        return http.build();
    }
}
