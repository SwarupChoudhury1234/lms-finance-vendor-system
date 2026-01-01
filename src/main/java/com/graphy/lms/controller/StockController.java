package com.graphy.lms.controller;

import com.graphy.lms.entity.StockLevel;
import com.graphy.lms.service.inventory.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    @Autowired
    private StockService stockService;

    // 1. CREATE - Initialize Stock for an Item
    @PostMapping("/add")
    public StockLevel addStock(@RequestBody StockLevel stockLevel) {
        // Note: For the first time, we use a standard save via a repository 
        // or a new service method you might add.
        return stockService.initializeStock(stockLevel); 
    }

    // 2. READ - Get stock details by Inventory Item ID
    @GetMapping("/item/{inventoryId}")
    public StockLevel getByItem(@PathVariable Long inventoryId) {
        return stockService.getStockByItem(inventoryId);
    }

    // 3. UPDATE - Trigger the Snapshot & Transaction logic
    // URL Example: /api/stock/update/1?quantityChange=-5
    @PutMapping("/update/{inventoryId}")
    public StockLevel updateStock(
            @PathVariable Long inventoryId, 
            @RequestParam Integer quantityChange) {
        return stockService.updateStock(inventoryId, quantityChange);
    }

    // 4. DELETE - Remove stock record
    @DeleteMapping("/delete/{inventoryId}")
    public String deleteStock(@PathVariable Long inventoryId) {
        stockService.deleteStockByItem(inventoryId);
        return "Stock record for Item " + inventoryId + " deleted successfully.";
    }
}