package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "currency_rates")
public class CurrencyRate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "base_currency") private String baseCurrency = "INR";
    @Column(name = "target_currency", nullable = false) private String targetCurrency;
    @Column(name = "exchange_rate", nullable = false, precision = 10, scale = 4) private BigDecimal exchangeRate;
    @Column(name = "effective_date", nullable = false) private LocalDate effectiveDate;
    @Column(name = "created_at") private LocalDateTime createdAt = LocalDateTime.now();
    
    public CurrencyRate() {}
    
    // Getters/Setters
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getBaseCurrency() { return baseCurrency; } public void setBaseCurrency(String baseCurrency) { this.baseCurrency = baseCurrency; }
    public String getTargetCurrency() { return targetCurrency; } public void setTargetCurrency(String targetCurrency) { this.targetCurrency = targetCurrency; }
    public BigDecimal getExchangeRate() { return exchangeRate; } public void setExchangeRate(BigDecimal exchangeRate) { this.exchangeRate = exchangeRate; }
    public LocalDate getEffectiveDate() { return effectiveDate; } public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}