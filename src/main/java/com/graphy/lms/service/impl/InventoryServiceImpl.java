package com.graphy.lms.service.impl;

//===== ALL REQUIRED IMPORTS WILL ALWAYS BE INCLUDED =====
import com.graphy.lms.entity.*;
import com.graphy.lms.repository.*;
import com.graphy.lms.service.InventoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryCategoryRepository categoryRepo;

    public InventoryCategory createCategory(InventoryCategory c){
        return categoryRepo.save(c);
    }

    public List<InventoryCategory> getAllCategory(){
        return categoryRepo.findAll();
    }

    public InventoryCategory getCategory(Long id){
        return categoryRepo.findById(id).orElseThrow();
    }

    public InventoryCategory updateCategory(Long id, InventoryCategory c){

        InventoryCategory existing = categoryRepo.findById(id).orElseThrow();

        existing.setCategoryName(c.getCategoryName());
        existing.setDescription(c.getDescription());
        existing.setStatus(c.getStatus());

        return categoryRepo.save(existing);
    }


    public void deleteCategory(Long id){
        categoryRepo.deleteById(id);
    }
    @Autowired private InventoryItemRepository itemRepo;
    @Autowired private StockLevelRepository stockRepo;

    public InventoryItem createItem(InventoryItem item){

        InventoryItem saved = itemRepo.save(item);

        StockLevel stock = new StockLevel();
        stock.setInventoryId(saved.getId());
        stock.setAvailableQuantity(saved.getTotalQuantity());
        stock.setLowStockThreshold(5); // default
        stockRepo.save(stock);

        return saved;
    }

    public List<InventoryItem> getAllItems(){
        return itemRepo.findAll();
    }

    public InventoryItem getItem(Long id){
        return itemRepo.findById(id).orElseThrow();
    }

    public InventoryItem updateItem(Long id, InventoryItem item){

        InventoryItem existing = itemRepo.findById(id).orElseThrow();

        existing.setItemName(item.getItemName());
        existing.setCategoryId(item.getCategoryId());
        existing.setTotalQuantity(item.getTotalQuantity());
        existing.setUnitPrice(item.getUnitPrice());
        existing.setStatus(item.getStatus());

        return itemRepo.save(existing);
    }

    @Autowired private AssetsAssignedRepository assetRepo;
    
    @Autowired private StockTransactionRepository txnRepo;

    public AssetsAssigned assignAsset(AssetsAssigned a, Long tokenUserId, String role){

        a.setUserId(tokenUserId);
        a.setUserRole(role);

        StockLevel stock = stockRepo.findByInventoryId(a.getItemId()).orElseThrow();

        if(stock.getAvailableQuantity() < a.getQuantity()){
            throw new RuntimeException("Insufficient Stock");
        }

        stock.setAvailableQuantity(stock.getAvailableQuantity() - a.getQuantity());
        stockRepo.save(stock);

        AssetsAssigned saved = assetRepo.save(a);

        StockTransaction txn = new StockTransaction();
        txn.setItemId(a.getItemId());
        txn.setTransactionType("OUT");
        txn.setQuantity(a.getQuantity());
        txn.setTransactionDate(LocalDate.now());
        txn.setReferenceId(saved.getId());
        txn.setRemarks("Asset Assigned");

        txnRepo.save(txn);

        return saved;
    }

    public List<AssetsAssigned> getMyAssets(Long userId){
        return assetRepo.findByUserId(userId);
    }
    @Autowired private AssetsReturnRepository returnRepo;
    @Autowired private AssetsAssignedRepository assignRepo;

    public AssetsReturn returnAsset(AssetsReturn r){

        AssetsAssigned assigned = assignRepo.findById(r.getAssignmentId()).orElseThrow();

        StockLevel stock = stockRepo.findByInventoryId(assigned.getItemId()).orElseThrow();

        stock.setAvailableQuantity(stock.getAvailableQuantity() + r.getReturnedQuantity());
        stockRepo.save(stock);

        AssetsReturn saved = returnRepo.save(r);

        StockTransaction txn = new StockTransaction();
        txn.setItemId(assigned.getItemId());
        txn.setTransactionType("IN");
        txn.setQuantity(r.getReturnedQuantity());
        txn.setTransactionDate(LocalDate.now());
        txn.setReferenceId(saved.getId());
        txn.setRemarks("Asset Returned : " + r.getConditionStatus());

        txnRepo.save(txn);

        return saved;
    }
    @Autowired private ProcurementRepository procurementRepo;

    public Procurement procureItem(Procurement p){

        Procurement saved = procurementRepo.save(p);

        StockLevel stock = stockRepo.findByInventoryId(p.getItemId()).orElseThrow();
        stock.setAvailableQuantity(stock.getAvailableQuantity() + p.getQuantity());
        stockRepo.save(stock);

        StockTransaction txn = new StockTransaction();
        txn.setItemId(p.getItemId());
        txn.setTransactionType("IN");
        txn.setQuantity(p.getQuantity());
        txn.setTransactionDate(LocalDate.now());
        txn.setReferenceId(saved.getId());
        txn.setRemarks("Procurement from Vendor");

        txnRepo.save(txn);

        return saved;
    }

    public List<Procurement> getAllProcurement(){
        return procurementRepo.findAll();
    }

    public Procurement getProcurement(Long id){
        return procurementRepo.findById(id).orElseThrow();
    }
    public List<StockTransaction> getAllStockTransactions(){
        return txnRepo.findAll();
    }

    public StockTransaction getStockTransaction(Long id){
        return txnRepo.findById(id).orElseThrow();
    }
    public List<StockLevel> getLowStockItems() {
        return stockRepo.findAll()
                .stream()
                .filter(s -> s.getAvailableQuantity() <= s.getLowStockThreshold())
                .toList();
    }
    @Override
    public List<StockLevel> getAllStockLevels() {
        return stockRepo.findAll();
    }

    @Override
    public StockLevel getStockLevel(Long id) {
        return stockRepo.findById(id).orElseThrow();
    }
    @Override
    public List<AssetsAssigned> getAllAssignedAssets() {
        return assetRepo.findAll();
    }
    @Override
    public List<AssetsReturn> getAllReturns() {
        return returnRepo.findAll();
    }

    @Override
    public AssetsReturn getReturn(Long id) {
        return returnRepo.findById(id).orElseThrow();
    }

    public AssetsReturn updateReturn(Long id, AssetsReturn r){

        AssetsReturn existing = returnRepo.findById(id).orElseThrow();

        existing.setReturnedQuantity(r.getReturnedQuantity());
        existing.setReturnDate(r.getReturnDate());
        existing.setConditionStatus(r.getConditionStatus());
        existing.setRemarks(r.getRemarks());

        return returnRepo.save(existing);
    }


}
