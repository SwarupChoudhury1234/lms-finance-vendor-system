package com.graphy.lms.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class InstallmentPlanRequest {
    private String planType; // "Quarterly", "Custom", etc.
    private BigDecimal totalFee;
    private List<InstallmentItem> installments;

    @Data
    public static class InstallmentItem {
        private String name;
        private BigDecimal amount;
        private LocalDate dueDate;
        private String status;
    }
}