package com.graphy.lms.dto;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterSettingsRequest {
    private GeneralSettings general;
    private LateFeeSettings lateFee;
    private NotificationSettings notifications;

    // Getters and Setters for Main Class
    public GeneralSettings getGeneral() { return general; }
    public void setGeneral(GeneralSettings general) { this.general = general; }

    public LateFeeSettings getLateFee() { return lateFee; }
    public void setLateFee(LateFeeSettings lateFee) { this.lateFee = lateFee; }

    public NotificationSettings getNotifications() { return notifications; }
    public void setNotifications(NotificationSettings notifications) { this.notifications = notifications; }

    // ==========================
    // 1. GENERAL SETTINGS
    // ==========================
    public static class GeneralSettings {
        private String currency;
        private String currencySymbol;
        private String taxName;
        private Double taxPercentage;
        private String invoicePrefix;
        private String financialYear;

        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }

        public String getCurrencySymbol() { return currencySymbol; }
        public void setCurrencySymbol(String currencySymbol) { this.currencySymbol = currencySymbol; }

        public String getTaxName() { return taxName; }
        public void setTaxName(String taxName) { this.taxName = taxName; }

        public Double getTaxPercentage() { return taxPercentage; }
        public void setTaxPercentage(Double taxPercentage) { this.taxPercentage = taxPercentage; }

        public String getInvoicePrefix() { return invoicePrefix; }
        public void setInvoicePrefix(String invoicePrefix) { this.invoicePrefix = invoicePrefix; }

        public String getFinancialYear() { return financialYear; }
        public void setFinancialYear(String financialYear) { this.financialYear = financialYear; }
    }

    // ==========================
    // 2. LATE FEE SETTINGS
    // ==========================
    public static class LateFeeSettings {
        private Boolean enabled;
        private BigDecimal amount;
        private String type;
        private BigDecimal maxCap;
        private Boolean sendEmail;
        private String frequency;

        public Boolean getEnabled() { return enabled; }
        public void setEnabled(Boolean enabled) { this.enabled = enabled; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public BigDecimal getMaxCap() { return maxCap; }
        public void setMaxCap(BigDecimal maxCap) { this.maxCap = maxCap; }

        public Boolean getSendEmail() { return sendEmail; }
        public void setSendEmail(Boolean sendEmail) { this.sendEmail = sendEmail; }

        public String getFrequency() { return frequency; }
        public void setFrequency(String frequency) { this.frequency = frequency; }
    }

    // ==========================
    // 3. NOTIFICATION SETTINGS
    // ==========================
    public static class NotificationSettings {
        private Boolean creation;
        private Boolean pending;
        private Boolean overdue;
        private Boolean paymentSuccess;
        private Boolean partialPayment;
        private Boolean refundStatus;

        public Boolean getCreation() { return creation; }
        public void setCreation(Boolean creation) { this.creation = creation; }

        public Boolean getPending() { return pending; }
        public void setPending(Boolean pending) { this.pending = pending; }

        public Boolean getOverdue() { return overdue; }
        public void setOverdue(Boolean overdue) { this.overdue = overdue; }

        public Boolean getPaymentSuccess() { return paymentSuccess; }
        public void setPaymentSuccess(Boolean paymentSuccess) { this.paymentSuccess = paymentSuccess; }

        public Boolean getPartialPayment() { return partialPayment; }
        public void setPartialPayment(Boolean partialPayment) { this.partialPayment = partialPayment; }

        public Boolean getRefundStatus() { return refundStatus; }
        public void setRefundStatus(Boolean refundStatus) { this.refundStatus = refundStatus; }
    }
}