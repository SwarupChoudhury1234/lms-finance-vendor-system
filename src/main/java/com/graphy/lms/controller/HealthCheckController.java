package com.graphy.lms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/fee-management/health")
public class HealthCheckController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getHealthStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", System.currentTimeMillis());
        status.put("service", "Fee Management System");
        status.put("version", "1.0.0");
        
        // Check database connection
        try (Connection connection = dataSource.getConnection()) {
            status.put("database", "CONNECTED");
        } catch (Exception e) {
            status.put("database", "DISCONNECTED");
            status.put("status", "DOWN");
        }
        
        return ResponseEntity.ok(status);
    }
}