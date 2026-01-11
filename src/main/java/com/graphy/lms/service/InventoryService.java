package com.graphy.lms.service;

// ===== ALL REQUIRED IMPORTS =====
import com.graphy.lms.entity.*;

import java.util.List;

public interface InventoryService {

    // inventory_categories
    InventoryCategory createCategory(InventoryCategory c);
    List<InventoryCategory> getAllCategory();
    InventoryCategory getCategory(Long id);
    InventoryCategory updateCategory(Long id, InventoryCategory c);
    void deleteCategory(Long id);

    // inventory_items
    InventoryItem createItem(InventoryItem item);
    List<InventoryItem> getAllItems();
    InventoryItem getItem(Long id);
    InventoryItem updateItem(Long id, InventoryItem item);

    // assets_assigned
    AssetsAssigned assignAsset(AssetsAssigned a, Long tokenUserId, String role);
    List<AssetsAssigned> getMyAssets(Long userId);

    // asset_returns  ❗ FIXED HERE
    AssetsReturn returnAsset(AssetsReturn r);

    // procurement
    Procurement procureItem(Procurement p);
    List<Procurement> getAllProcurement();
    Procurement getProcurement(Long id);

    // stock_transactions
    List<StockTransaction> getAllStockTransactions();
    StockTransaction getStockTransaction(Long id);

    // low stock
    List<StockLevel> getLowStockItems();
    List<StockLevel> getAllStockLevels();
    StockLevel getStockLevel(Long id);
    List<AssetsAssigned> getAllAssignedAssets();
    List<AssetsReturn> getAllReturns();
    AssetsReturn getReturn(Long id);
    AssetsReturn updateReturn(Long id, AssetsReturn r);



}
