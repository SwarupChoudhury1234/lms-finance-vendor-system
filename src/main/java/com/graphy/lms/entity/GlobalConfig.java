package com.graphy.lms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "global_configs")
public class GlobalConfig {

    @Id
    @Column(name = "config_key", nullable = false, unique = true)
    private String configKey; // e.g. "INVOICE_PREFIX"

    @Column(name = "config_value")
    private String configValue; // e.g. "INV-"
    
    @Column(name = "description")
    private String description; // Optional: "Prefix for fee receipts"
}