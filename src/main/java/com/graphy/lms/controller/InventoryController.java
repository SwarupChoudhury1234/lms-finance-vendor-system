package com.graphy.lms.controller;

import com.graphy.lms.entity.*;
import com.graphy.lms.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired private InventoryService service;

    // 1. CATEGORIES
    @PostMapping("/categories") public InventoryCategory addCat(@RequestBody InventoryCategory c) { return service.saveCategory(c); }
    @GetMapping("/categories/{id}") public InventoryCategory getCat(@PathVariable Long id) { return service.getCategoryById(id); }
    @GetMapping("/categories") public List<InventoryCategory> getAllCat() { return service.getAllCategories(); }
    @PutMapping("/categories/{id}") public InventoryCategory updateCat(@PathVariable Long id, @RequestBody InventoryCategory c) { return service.updateCategory(id, c); }
    @DeleteMapping("/categories/{id}") public String delCat(@PathVariable Long id) { service.deleteCategory(id); return "Category Deleted: " + id; }

    // 2. ITEMS
    @PostMapping("/items") public InventoryItem addItem(@RequestBody InventoryItem i) { return service.saveItem(i); }
    @GetMapping("/items/{id}") public InventoryItem getItem(@PathVariable Long id) { return service.getItemById(id); }
    @GetMapping("/items") public List<InventoryItem> getAllItems() { return service.getAllItems(); }
    @PutMapping("/items/{id}") public InventoryItem updateItem(@PathVariable Long id, @RequestBody InventoryItem i) { return service.updateItem(id, i); }
    @DeleteMapping("/items/{id}") public String delItem(@PathVariable Long id) { service.deleteItem(id); return "Item Deleted: " + id; }

    // 3. STOCK LEVELS
    @PostMapping("/stock") public StockLevel addStock(@RequestBody StockLevel s) { return service.saveStockLevel(s); }
    @GetMapping("/stock/{id}") public StockLevel getStock(@PathVariable Long id) { return service.getStockLevelById(id); }
    @GetMapping("/stock") public List<StockLevel> getAllStock() { return service.getAllStockLevels(); }
    @PutMapping("/stock/{id}") public StockLevel updateStock(@PathVariable Long id, @RequestBody StockLevel s) { return service.updateStockLevel(id, s); }
    @DeleteMapping("/stock/{id}") public String delStock(@PathVariable Long id) { service.deleteStockLevel(id); return "Stock Level Deleted: " + id; }

    // 4. ASSETS ASSIGNED
    @PostMapping("/assignments") public AssetsAssigned addAss(@RequestBody AssetsAssigned a) { return service.saveAssignment(a); }
    @GetMapping("/assignments/{id}") public AssetsAssigned getAss(@PathVariable Long id) { return service.getAssignmentById(id); }
    @GetMapping("/assignments") public List<AssetsAssigned> getAllAss() { return service.getAllAssignments(); }
    @PutMapping("/assignments/{id}") public AssetsAssigned updateAss(@PathVariable Long id, @RequestBody AssetsAssigned a) { return service.updateAssignment(id, a); }
    @DeleteMapping("/assignments/{id}") public String delAss(@PathVariable Long id) { service.deleteAssignment(id); return "Assignment Deleted: " + id; }

    // 5. ASSETS RETURN
    @PostMapping("/returns") public AssetsReturn addRet(@RequestBody AssetsReturn r) { return service.saveReturn(r); }
    @GetMapping("/returns/{id}") public AssetsReturn getRet(@PathVariable Long id) { return service.getReturnById(id); }
    @GetMapping("/returns") public List<AssetsReturn> getAllRet() { return service.getAllReturns(); }
    @PutMapping("/returns/{id}") public AssetsReturn updateRet(@PathVariable Long id, @RequestBody AssetsReturn r) { return service.updateReturn(id, r); }
    @DeleteMapping("/returns/{id}") public String delRet(@PathVariable Long id) { service.deleteReturn(id); return "Return Deleted: " + id; }

    // 6. PROCUREMENT
    @PostMapping("/procurement") public Procurement addPro(@RequestBody Procurement p) { return service.saveProcurement(p); }
    @GetMapping("/procurement/{id}") public Procurement getPro(@PathVariable Long id) { return service.getProcurementById(id); }
    @GetMapping("/procurement") public List<Procurement> getAllPro() { return service.getAllProcurements(); }
    @PutMapping("/procurement/{id}") public Procurement updatePro(@PathVariable Long id, @RequestBody Procurement p) { return service.updateProcurement(id, p); }
    @DeleteMapping("/procurement/{id}") public String delPro(@PathVariable Long id) { service.deleteProcurement(id); return "Procurement Deleted: " + id; }

    // 7. STOCK TRANSACTIONS
    @PostMapping("/transactions") public StockTransaction addTra(@RequestBody StockTransaction t) { return service.saveTransaction(t); }
    @GetMapping("/transactions/{id}") public StockTransaction getTra(@PathVariable Long id) { return service.getTransactionById(id); }
    @GetMapping("/transactions") public List<StockTransaction> getAllTra() { return service.getAllTransactions(); }
    @PutMapping("/transactions/{id}") public StockTransaction updateTra(@PathVariable Long id, @RequestBody StockTransaction t) { return service.updateTransaction(id, t); }
    @DeleteMapping("/transactions/{id}") public String delTra(@PathVariable Long id) { service.deleteTransaction(id); return "Transaction Deleted: " + id; }
}