package com.graphy.lms.service.impl;

import com.graphy.lms.entity.*;
import com.graphy.lms.repository.*;
import com.graphy.lms.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired private InventoryCategoryRepository catRepo;
    @Autowired private InventoryItemRepository itemRepo;
    @Autowired private StockLevelRepository stockRepo;
    @Autowired private AssetsAssignedRepository assignRepo;
    @Autowired private AssetsReturnRepository returnRepo;
    @Autowired private ProcurementRepository procRepo;
    @Autowired private StockTransactionRepository transRepo;

    // --- 1. CATEGORIES ---
    @Override public InventoryCategory saveCategory(InventoryCategory c) { return catRepo.save(c); }
    @Override public InventoryCategory getCategoryById(Long id) { return catRepo.findById(id).orElseThrow(() -> new RuntimeException("Category not found with ID: " + id)); }
    @Override public List<InventoryCategory> getAllCategories() { return catRepo.findAll(); }
    @Override public InventoryCategory updateCategory(Long id, InventoryCategory c) {
        InventoryCategory ex = getCategoryById(id);
        ex.setCategoryName(c.getCategoryName()); 
        ex.setDescription(c.getDescription()); 
        ex.setStatus(c.getStatus());
        return catRepo.save(ex);
    }
    @Override public void deleteCategory(Long id) { catRepo.deleteById(id); }

    // --- 2. ITEMS ---
    @Override public InventoryItem saveItem(InventoryItem i) { return itemRepo.save(i); }
    @Override public InventoryItem getItemById(Long id) { return itemRepo.findById(id).orElseThrow(() -> new RuntimeException("Item not found with ID: " + id)); }
    @Override public List<InventoryItem> getAllItems() { return itemRepo.findAll(); }
    @Override public InventoryItem updateItem(Long id, InventoryItem i) {
        InventoryItem ex = getItemById(id);
        ex.setItemName(i.getItemName()); 
        ex.setCategory(i.getCategory()); 
        ex.setTotalQuantity(i.getTotalQuantity());
        ex.setUnitPrice(i.getUnitPrice()); 
        ex.setStatus(i.getStatus());
        return itemRepo.save(ex);
    }
    @Override public void deleteItem(Long id) { itemRepo.deleteById(id); }

    // --- 3. STOCK LEVELS ---
    @Override public StockLevel saveStockLevel(StockLevel s) { return stockRepo.save(s); }
    @Override public StockLevel getStockLevelById(Long id) { return stockRepo.findById(id).orElseThrow(() -> new RuntimeException("Stock Level not found with ID: " + id)); }
    @Override public List<StockLevel> getAllStockLevels() { return stockRepo.findAll(); }
    @Override public StockLevel updateStockLevel(Long id, StockLevel s) {
        StockLevel ex = getStockLevelById(id);
        ex.setItem(s.getItem()); 
        ex.setAvailableQuantity(s.getAvailableQuantity()); 
        ex.setLowStockThreshold(s.getLowStockThreshold());
        return stockRepo.save(ex);
    }
    @Override public void deleteStockLevel(Long id) { stockRepo.deleteById(id); }

    // --- 4. ASSETS ASSIGNED ---
    @Override public AssetsAssigned saveAssignment(AssetsAssigned a) { return assignRepo.save(a); }
    @Override public AssetsAssigned getAssignmentById(Long id) { return assignRepo.findById(id).orElseThrow(() -> new RuntimeException("Assignment not found with ID: " + id)); }
    @Override public List<AssetsAssigned> getAllAssignments() { return assignRepo.findAll(); }
    @Override public AssetsAssigned updateAssignment(Long id, AssetsAssigned a) {
        AssetsAssigned ex = getAssignmentById(id);
        ex.setUserId(a.getUserId()); 
        ex.setUserRole(a.getUserRole()); 
        ex.setItem(a.getItem());
        ex.setQuantity(a.getQuantity()); 
        ex.setGivenDate(a.getGivenDate()); 
        ex.setStatus(a.getStatus());
        return assignRepo.save(ex);
    }
    @Override public void deleteAssignment(Long id) { assignRepo.deleteById(id); }

    // --- 5. ASSETS RETURN ---
    @Override public AssetsReturn saveReturn(AssetsReturn r) { return returnRepo.save(r); }
    @Override public AssetsReturn getReturnById(Long id) { return returnRepo.findById(id).orElseThrow(() -> new RuntimeException("Return record not found with ID: " + id)); }
    @Override public List<AssetsReturn> getAllReturns() { return returnRepo.findAll(); }
    @Override public AssetsReturn updateReturn(Long id, AssetsReturn r) {
        AssetsReturn ex = getReturnById(id);
        ex.setAssignment(r.getAssignment()); 
        ex.setReturnedQuantity(r.getReturnedQuantity());
        ex.setReturnDate(r.getReturnDate()); 
        ex.setConditionStatus(r.getConditionStatus()); 
        ex.setRemarks(r.getRemarks());
        return returnRepo.save(ex);
    }
    @Override public void deleteReturn(Long id) { returnRepo.deleteById(id); }

    // --- 6. PROCUREMENT ---
    @Override public Procurement saveProcurement(Procurement p) { return procRepo.save(p); }
    @Override public Procurement getProcurementById(Long id) { return procRepo.findById(id).orElseThrow(() -> new RuntimeException("Procurement not found with ID: " + id)); }
    @Override public List<Procurement> getAllProcurements() { return procRepo.findAll(); }
    @Override public Procurement updateProcurement(Long id, Procurement p) {
        Procurement ex = getProcurementById(id);
        ex.setItem(p.getItem()); 
        ex.setVendorId(p.getVendorId()); 
        ex.setQuantity(p.getQuantity());
        ex.setPurchaseDate(p.getPurchaseDate()); 
        ex.setCost(p.getCost());
        return procRepo.save(ex);
    }
    @Override public void deleteProcurement(Long id) { procRepo.deleteById(id); }

    // --- 7. STOCK TRANSACTIONS (With Snapshot Logic) ---
    @Override 
    public StockTransaction saveTransaction(StockTransaction t) {
        // Fetch current stock for the item to calculate snapshots
        StockLevel stock = stockRepo.findAll().stream()
            .filter(s -> s.getItem().getId().equals(t.getItem().getId()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Stock level not found for Item ID: " + t.getItem().getId()));

        // Snapshot: Previous balance
        t.setPreviousBalance(stock.getAvailableQuantity());
        
        // Logic: Calculate New Balance based on type
        if ("IN".equalsIgnoreCase(t.getTransactionType())) {
            t.setNewBalance(t.getPreviousBalance() + t.getQuantity());
        } else {
            t.setNewBalance(t.getPreviousBalance() - t.getQuantity());
        }

        // Sync StockLevel table with the new balance
        stock.setAvailableQuantity(t.getNewBalance());
        stockRepo.save(stock);

        return transRepo.save(t);
    }
    
    @Override public StockTransaction getTransactionById(Long id) { return transRepo.findById(id).orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + id)); }
    @Override public List<StockTransaction> getAllTransactions() { return transRepo.findAll(); }
    @Override public StockTransaction updateTransaction(Long id, StockTransaction t) {
        StockTransaction ex = getTransactionById(id);
        ex.setItem(t.getItem()); 
        ex.setTransactionType(t.getTransactionType()); 
        ex.setQuantity(t.getQuantity());
        ex.setTransactionDate(t.getTransactionDate()); 
        ex.setReferenceId(t.getReferenceId());
        ex.setRemarks(t.getRemarks()); 
        ex.setPreviousBalance(t.getPreviousBalance()); 
        ex.setNewBalance(t.getNewBalance());
        return transRepo.save(ex);
    }
    @Override public void deleteTransaction(Long id) { transRepo.deleteById(id); }
}