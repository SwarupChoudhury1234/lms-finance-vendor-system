package com.graphy.lms.service.inventory;

import com.graphy.lms.entity.StockLevel;

/**
 * Service interface for Stock Level Management.
 * Includes Snapshot logic for updates and full CRUD capabilities.
 */
public interface StockService {

    // 1. CREATE - Initialize stock for a new inventory item
    StockLevel initializeStock(StockLevel stockLevel);

    // 2. READ - Fetch stock details using the Inventory Item ID
    StockLevel getStockByItem(Long inventoryId);

    // 3. UPDATE - The "Snapshot" update logic (Fetch-then-Update)
    // Handles Previous Balance and New Balance calculations
    StockLevel updateStock(Long inventoryId, Integer quantityChange);

    // 4. DELETE - Remove stock record for an inventory item
    void deleteStockByItem(Long inventoryId);
}