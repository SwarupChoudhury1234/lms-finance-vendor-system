package com.graphy.lms.entity;

import jakarta.persistence.*; 
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.ToString;

@Entity
@ToString
@Table(name="assets_assigned")
@Data
public class AssetsAssigned {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;          // from JWT
    private String userRole;
    private Long itemId;
    private Integer quantity;
    private LocalDate givenDate;
    private String status;

    @Column(name="created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
