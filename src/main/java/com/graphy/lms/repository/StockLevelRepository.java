package com.graphy.lms.repository;

import com.graphy.lms.entity.StockLevel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockLevelRepository extends JpaRepository<StockLevel, Long> {
    // Find stock by looking at the inventory item ID
    StockLevel findByInventoryItemId(Long inventoryId);
}