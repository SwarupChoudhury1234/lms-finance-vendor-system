package com.graphy.lms.service;

import com.graphy.lms.entity.*;
import java.util.List;

public interface InventoryService {
    // 1. Categories
    InventoryCategory saveCategory(InventoryCategory c);
    InventoryCategory getCategoryById(Long id);
    List<InventoryCategory> getAllCategories();
    InventoryCategory updateCategory(Long id, InventoryCategory c);
    void deleteCategory(Long id);

    // 2. Items
    InventoryItem saveItem(InventoryItem i);
    InventoryItem getItemById(Long id);
    List<InventoryItem> getAllItems();
    InventoryItem updateItem(Long id, InventoryItem i);
    void deleteItem(Long id);

    // 3. Stock Levels
    StockLevel saveStockLevel(StockLevel s);
    StockLevel getStockLevelById(Long id);
    List<StockLevel> getAllStockLevels();
    StockLevel updateStockLevel(Long id, StockLevel s);
    void deleteStockLevel(Long id);

    // 4. Assets Assigned
    AssetsAssigned saveAssignment(AssetsAssigned a);
    AssetsAssigned getAssignmentById(Long id);
    List<AssetsAssigned> getAllAssignments();
    AssetsAssigned updateAssignment(Long id, AssetsAssigned a);
    void deleteAssignment(Long id);

    // 5. Assets Return
    AssetsReturn saveReturn(AssetsReturn r);
    AssetsReturn getReturnById(Long id);
    List<AssetsReturn> getAllReturns();
    AssetsReturn updateReturn(Long id, AssetsReturn r);
    void deleteReturn(Long id);

    // 6. Procurement
    Procurement saveProcurement(Procurement p);
    Procurement getProcurementById(Long id);
    List<Procurement> getAllProcurements();
    Procurement updateProcurement(Long id, Procurement p);
    void deleteProcurement(Long id);

    // 7. Stock Transactions
    StockTransaction saveTransaction(StockTransaction t);
    StockTransaction getTransactionById(Long id);
    List<StockTransaction> getAllTransactions();
    StockTransaction updateTransaction(Long id, StockTransaction t);
    void deleteTransaction(Long id);
}