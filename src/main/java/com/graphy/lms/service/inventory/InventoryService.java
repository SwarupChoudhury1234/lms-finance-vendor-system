package com.graphy.lms.service.inventory;

import com.graphy.lms.entity.InventoryItem;
import java.util.List;

/**
 * Service interface for Inventory Item Management.
 * Follows full CRUD requirements: Create, Read, Update, Delete.
 */
public interface InventoryService {

    // 1. CREATE (POST)
    InventoryItem addItem(InventoryItem item);

    // 2. READ (GET)
    List<InventoryItem> getAllItems();
    InventoryItem getItemById(Long id);

    // 3. UPDATE (PUT) - Mentor's "Fetch-then-Update" Requirement
    /**
     * Updates an existing inventory item.
     * @param id The ID of the item to update.
     * @param itemDetails The new data to be applied.
     * @return The updated InventoryItem entity.
     */
    InventoryItem updateItem(Long id, InventoryItem itemDetails);

    // 4. DELETE (DELETE)
    /**
     * Removes an item from the system.
     * @param id The ID of the item to be deleted.
     */
    void deleteItem(Long id);
}