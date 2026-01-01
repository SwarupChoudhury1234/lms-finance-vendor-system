package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "assets_assigned")
public class AssetsAssigned {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id") // Matches your schema
    private Long userId; 

    @ManyToOne
    @JoinColumn(name = "item_id") // Matches your schema
    private InventoryItem inventoryItem; 

    @Column(name = "quantity") // Matches your schema
    private Integer quantity;

    @Column(name = "given_date") // Matches your schema
    private LocalDate givenDate;

    // --- MANUAL GETTERS AND SETTERS ---
    // These must match the names above exactly so the Service can find them!

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public InventoryItem getInventoryItem() { return inventoryItem; }
    public void setInventoryItem(InventoryItem inventoryItem) { this.inventoryItem = inventoryItem; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public LocalDate getGivenDate() { return givenDate; }
    public void setGivenDate(LocalDate givenDate) { this.givenDate = givenDate; }
}