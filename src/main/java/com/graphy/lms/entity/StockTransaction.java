package com.graphy.lms.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "stock_transactions")
public class StockTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private InventoryItem inventoryItem;

    @Column(name = "transaction_type")
    private String transactionType; 

    private Integer quantity;

    // --- MENTOR'S SNAPSHOT REQUIREMENTS ---
    @Column(name = "previous_balance")
    private Integer previousBalance;

    @Column(name = "new_balance")
    private Integer newBalance;

    @Column(name = "transaction_date")
    private LocalDate transactionDate;

    // --- GETTERS AND SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public InventoryItem getInventoryItem() { return inventoryItem; }
    public void setInventoryItem(InventoryItem inventoryItem) { this.inventoryItem = inventoryItem; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getPreviousBalance() { return previousBalance; }
    public void setPreviousBalance(Integer previousBalance) { this.previousBalance = previousBalance; }

    public Integer getNewBalance() { return newBalance; }
    public void setNewBalance(Integer newBalance) { this.newBalance = newBalance; }

    public LocalDate getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDate transactionDate) { this.transactionDate = transactionDate; }
}