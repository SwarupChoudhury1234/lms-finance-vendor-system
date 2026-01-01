package com.graphy.lms.service.impl;

import com.graphy.lms.entity.StockLevel;
import com.graphy.lms.repository.StockLevelRepository;
import com.graphy.lms.service.inventory.StockService;
import com.graphy.lms.service.inventory.TransactionService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private StockLevelRepository stockRepository;

    @Autowired
    private TransactionService transactionService;

    @PersistenceContext
    private EntityManager entityManager;

    // 1. CREATE - Initialize Stock (With Validation)
    @Override
    @Transactional
    public StockLevel initializeStock(StockLevel stockLevel) {
        if (stockLevel.getInventoryItem() == null || stockLevel.getInventoryItem().getId() == null) {
            throw new RuntimeException("Cannot initialize stock: Inventory Item ID is missing.");
        }

        Long itemId = stockLevel.getInventoryItem().getId();
        
        // Check if stock already exists for this item to prevent duplicates
        StockLevel existing = stockRepository.findByInventoryItemId(itemId);
        if (existing != null) {
            throw new RuntimeException("Stock record already exists for Item ID: " + itemId);
        }
        
        return stockRepository.save(stockLevel);
    }

    // 2. READ - Get stock by Item ID (Fetch-then-Read pattern)
    @Override
    public StockLevel getStockByItem(Long inventoryId) {
        StockLevel stock = stockRepository.findByInventoryItemId(inventoryId);
        if (stock == null) {
            throw new RuntimeException("Stock record not found for Item ID: " + inventoryId);
        }
        return stock;
    }

    // 3. UPDATE - Fetch-then-Update with Snapshots (Mentor's Pattern)
    @Override
    @Transactional
    public StockLevel updateStock(Long inventoryId, Integer quantityChange) {
        // Step A: Fetch current stock
        StockLevel stock = stockRepository.findByInventoryItemId(inventoryId);
        
        if (stock == null) {
            throw new RuntimeException("Update failed: Stock record not found for Item ID: " + inventoryId);
        }

        // Step B: Capture Snapshot values
        Integer previousBalance = stock.getAvailableQuantity();
        Integer newBalance = previousBalance + quantityChange;

        // Step C: Update the existing count
        stock.setAvailableQuantity(newBalance);
        StockLevel savedStock = stockRepository.save(stock);

        // Step D: Record the transaction with 5 arguments (ID, Qty, Type, Prev, Current)
        transactionService.recordTransaction(
            inventoryId, 
            Math.abs(quantityChange), 
            quantityChange > 0 ? "STOCK_ADD" : "STOCK_ISSUE", 
            previousBalance, 
            newBalance
        );

        return savedStock;
    }

    // 4. DELETE - Fetch-then-Delete
    @Override
    @Transactional
    public void deleteStockByItem(Long inventoryId) {
        StockLevel stock = stockRepository.findByInventoryItemId(inventoryId);
        if (stock != null) {
            stockRepository.delete(stock);
        } else {
            throw new RuntimeException("Delete failed: Stock record not found for Item ID: " + inventoryId);
        }
    }
}