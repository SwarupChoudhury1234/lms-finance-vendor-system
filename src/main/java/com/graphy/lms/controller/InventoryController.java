package com.graphy.lms.controller;

import com.graphy.lms.entity.*;
import org.springframework.security.access.prepost.PreAuthorize;

import com.graphy.lms.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @PostMapping("/category")
    @PreAuthorize("hasRole('ADMIN')")
    public InventoryCategory createCategory(@RequestBody InventoryCategory c){
        return inventoryService.createCategory(c);
    }

    @GetMapping("/category")
    @PreAuthorize("hasAnyRole('ADMIN','FACULTY','STUDENT')")
    public List<InventoryCategory> getAllCategory(){
        return inventoryService.getAllCategory();
    }

    @GetMapping("/category/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FACULTY','STUDENT')")
    public InventoryCategory getCategory(@PathVariable Long id){
        return inventoryService.getCategory(id);
    }

    @PutMapping("/category/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public InventoryCategory updateCategory(@PathVariable Long id,@RequestBody InventoryCategory c){
        return inventoryService.updateCategory(id,c);
    }

    @DeleteMapping("/category/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(@PathVariable Long id){
        inventoryService.deleteCategory(id);
    }
    @PostMapping("/item")
    @PreAuthorize("hasRole('ADMIN')")
    public InventoryItem createItem(@RequestBody InventoryItem item){
        return inventoryService.createItem(item);
    }

    @GetMapping("/item")
    @PreAuthorize("hasAnyRole('ADMIN','FACULTY')")
    public List<InventoryItem> getAllItems(){
        return inventoryService.getAllItems();
    }

    @GetMapping("/item/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FACULTY')")
    public InventoryItem getItem(@PathVariable Long id){
        return inventoryService.getItem(id);
    }

    @PutMapping("/item/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public InventoryItem updateItem(@PathVariable Long id,@RequestBody InventoryItem item){
        return inventoryService.updateItem(id,item);
    }
    @PostMapping("/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public AssetsAssigned assign(@RequestBody AssetsAssigned a,
                                 @RequestHeader("X-USER-ID") Long userId,
                                 @RequestHeader("X-ROLE") String role){
        return inventoryService.assignAsset(a,userId,role);
    }

    @GetMapping("/my-assets")
    @PreAuthorize("hasAnyRole('FACULTY','STUDENT')")
    public List<AssetsAssigned> myAssets(@RequestHeader("X-USER-ID") Long userId){
        return inventoryService.getMyAssets(userId);
    }
    @PostMapping("/return")
    @PreAuthorize("hasAnyRole('FACULTY','STUDENT')")
    public AssetsReturn returnAsset(@RequestBody AssetsReturn r){
        return inventoryService.returnAsset(r);
    }
    @PostMapping("/procurement")
    @PreAuthorize("hasRole('ADMIN')")
    public Procurement procure(@RequestBody Procurement p){
        return inventoryService.procureItem(p);
    }

    @GetMapping("/procurement")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Procurement> allProcurement(){
        return inventoryService.getAllProcurement();
    }

    @GetMapping("/procurement/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Procurement getProcurement(@PathVariable Long id){
        return inventoryService.getProcurement(id);
    }
    @GetMapping("/transactions")
    @PreAuthorize("hasRole('ADMIN')")
    public List<StockTransaction> allTransactions(){
        return inventoryService.getAllStockTransactions();
    }

    @GetMapping("/transactions/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public StockTransaction transaction(@PathVariable Long id){
        return inventoryService.getStockTransaction(id);
    }
    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('ADMIN')")
    public List<StockLevel> lowStock(){
        return inventoryService.getLowStockItems();
    }
    @GetMapping("/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public List<StockLevel> allStock(){
        return inventoryService.getAllStockLevels();
    }

    @GetMapping("/stock/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public StockLevel stock(@PathVariable Long id){
        return inventoryService.getStockLevel(id);
    }
    @GetMapping("/assigned")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AssetsAssigned> allAssigned(){
        return inventoryService.getAllAssignedAssets();
    }
    @GetMapping("/returns")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AssetsReturn> allReturns(){
        return inventoryService.getAllReturns();
    }

    @GetMapping("/returns/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public AssetsReturn getReturn(@PathVariable Long id){
        return inventoryService.getReturn(id);
    }

    @PutMapping("/returns/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public AssetsReturn updateReturn(@PathVariable Long id, @RequestBody AssetsReturn r){
        return inventoryService.updateReturn(id, r);
    }





}