package com.graphy.lms.service.impl;

// ===== REQUIRED IMPORTS =====
import com.graphy.lms.entity.*;
import com.graphy.lms.entity.enums.ApprovalStatus;
import com.graphy.lms.entity.enums.StockTransactionType;
import com.graphy.lms.entity.enums.TransactionSourceType;
import com.graphy.lms.repository.*;
import com.graphy.lms.service.InventoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

    // =============================
    // CATEGORY
    // =============================

    @Autowired
    private InventoryCategoryRepository categoryRepo;

    @Override
    public InventoryCategory createCategory(InventoryCategory c) {
        return categoryRepo.save(c);
    }

    @Override
    public List<InventoryCategory> getAllCategory() {
        return categoryRepo.findAll();
    }

    @Override
    public InventoryCategory getCategory(Long id) {
        return categoryRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public InventoryCategory updateCategory(Long id, InventoryCategory c) {
        InventoryCategory existing = getCategory(id);
        existing.setCategoryName(c.getCategoryName());
        existing.setDescription(c.getDescription());
        existing.setStatus(c.getStatus());
        return categoryRepo.save(existing);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepo.deleteById(id);
    }

    // =============================
    // ITEM MASTER
    // =============================

    @Autowired
    private InventoryItemRepository itemRepo;

    @Autowired
    private StockLevelRepository stockRepo;

    @Override
    public InventoryItem createItem(InventoryItem item) {

        if (itemRepo.existsBySku(item.getSku())) {
            throw new RuntimeException("SKU already exists");
        }

        if (Boolean.TRUE.equals(item.getIsTrackable())) {
            if (item.getTotalQuantity() == null || item.getTotalQuantity() < 0) {
                throw new RuntimeException("Trackable item requires quantity");
            }
        } else {
            item.setTotalQuantity(null);
        }

        InventoryItem saved = itemRepo.save(item);

        if (Boolean.TRUE.equals(saved.getIsTrackable())) {
            StockLevel stock = new StockLevel();
            stock.setInventoryId(saved.getId());
            stock.setAvailableQuantity(saved.getTotalQuantity());
            stock.setLowStockThreshold(5);
            stockRepo.save(stock);
        }

        return saved;
    }

    @Override
    public List<InventoryItem> getAllItems() {
        return itemRepo.findAll();
    }

    @Override
    public InventoryItem getItem(Long id) {
        return itemRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
    }

    @Override
    public InventoryItem updateItem(Long id, InventoryItem updated) {

        InventoryItem existing = getItem(id);

        if (!existing.getSku().equals(updated.getSku())) {
            throw new RuntimeException("SKU cannot be modified");
        }

        existing.setItemName(updated.getItemName());
        existing.setCategoryId(updated.getCategoryId());
        existing.setUnitOfMeasure(updated.getUnitOfMeasure());
        existing.setIsTrackable(updated.getIsTrackable());
        existing.setIsConsumable(updated.getIsConsumable());
        existing.setCourseId(updated.getCourseId());
        existing.setBatchId(updated.getBatchId());
        existing.setUnitPrice(updated.getUnitPrice());
        existing.setStatus(updated.getStatus());

        if (Boolean.TRUE.equals(existing.getIsTrackable())) {
            existing.setTotalQuantity(updated.getTotalQuantity());
        }

        return itemRepo.save(existing);
    }

    // =============================
    // STOCK TRANSACTIONS
    // =============================

    @Autowired
    private StockTransactionRepository txnRepo;

    private void applyStockMovement(StockTransaction txn, StockLevel stock) {

        switch (txn.getTransactionType()) {

            case PURCHASE_INWARD:
            case RETURN_GOOD:
            case ADJUSTMENT_ADD:
                stock.setAvailableQuantity(
                        stock.getAvailableQuantity() + txn.getQuantity());
                break;

            case ISSUE_STUDENT:
            case ISSUE_FACULTY:
            case ISSUE_BATCH:
            case CONSUMPTION:
            case DAMAGE_WRITE_OFF:
            case LOST_WRITE_OFF:
            case ADJUSTMENT_SUBTRACT:
                if (stock.getAvailableQuantity() < txn.getQuantity()) {
                    throw new RuntimeException("Insufficient stock");
                }
                stock.setAvailableQuantity(
                        stock.getAvailableQuantity() - txn.getQuantity());
                break;

            default:
                throw new IllegalStateException("Unhandled transaction type");
        }
    }

    public StockTransaction processStockTransaction(StockTransaction txn) {

        StockLevel stock = stockRepo.findByInventoryId(txn.getItemId())
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        if (txn.getApprovalStatus() == ApprovalStatus.PENDING) {
            return txnRepo.save(txn);
        }

        applyStockMovement(txn, stock);

        stockRepo.save(stock);
        return txnRepo.save(txn);
    }

    // =============================
    // PROCUREMENT
    // =============================

    @Autowired
    private ProcurementRepository procurementRepo;

    @Override
    public Procurement procureItem(Procurement p) {

        Procurement saved = procurementRepo.save(p);

        StockTransaction txn = new StockTransaction();
        txn.setItemId(p.getItemId());
        txn.setQuantity(p.getQuantity());
        txn.setTransactionDate(LocalDate.now());
        txn.setTransactionType(StockTransactionType.PURCHASE_INWARD);
        txn.setSourceType(TransactionSourceType.VENDOR);
        txn.setSourceId(p.getVendorId());
        txn.setApprovalStatus(ApprovalStatus.NOT_REQUIRED);
        txn.setRemarks("Procurement");

        processStockTransaction(txn);
        return saved;
    }

    @Override
    public List<Procurement> getAllProcurement() {
        return procurementRepo.findAll();
    }

    @Override
    public Procurement getProcurement(Long id) {
        return procurementRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Procurement not found"));
    }

    // =============================
    // ASSIGNMENTS
    // =============================

    @Autowired
    private AssetsAssignedRepository assetRepo;

    @Override
    public AssetsAssigned assignAsset(AssetsAssigned a, Long userId, String role) {

        a.setUserId(userId);
        a.setUserRole(role);

        AssetsAssigned saved = assetRepo.save(a);

        StockTransaction txn = new StockTransaction();
        txn.setItemId(a.getItemId());
        txn.setQuantity(a.getQuantity());
        txn.setTransactionDate(LocalDate.now());
        txn.setTransactionType(StockTransactionType.ISSUE_STUDENT);
        txn.setSourceType(TransactionSourceType.STUDENT);
        txn.setSourceId(userId);
        txn.setApprovalStatus(ApprovalStatus.NOT_REQUIRED);
        txn.setReferenceId(saved.getId());
        txn.setRemarks("Asset issued");

        processStockTransaction(txn);
        return saved;
    }

    @Override
    public List<AssetsAssigned> getMyAssets(Long userId) {
        return assetRepo.findByUserId(userId);
    }

    @Override
    public List<AssetsAssigned> getAllAssignedAssets() {
        return assetRepo.findAll();
    }

    // =============================
    // RETURNS
    // =============================

    @Autowired
    private AssetsReturnRepository returnRepo;

    @Autowired
    private AssetsAssignedRepository assignRepo;

    @Override
    public AssetsReturn returnAsset(AssetsReturn r) {

        AssetsAssigned assigned = assignRepo.findById(r.getAssignmentId())
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        AssetsReturn saved = returnRepo.save(r);

        StockTransaction txn = new StockTransaction();
        txn.setItemId(assigned.getItemId());
        txn.setQuantity(r.getReturnedQuantity());
        txn.setTransactionDate(LocalDate.now());
        txn.setTransactionType(
                "GOOD".equalsIgnoreCase(r.getConditionStatus())
                        ? StockTransactionType.RETURN_GOOD
                        : StockTransactionType.DAMAGE_WRITE_OFF
        );
        txn.setApprovalStatus(ApprovalStatus.NOT_REQUIRED);
        txn.setReferenceId(saved.getId());
        txn.setRemarks("Asset return");

        processStockTransaction(txn);
        return saved;
    }

    @Override
    public List<AssetsReturn> getAllReturns() {
        return returnRepo.findAll();
    }

    @Override
    public AssetsReturn getReturn(Long id) {
        return returnRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Return not found"));
    }

    @Override
    public AssetsReturn updateReturn(Long id, AssetsReturn r) {
        AssetsReturn existing = getReturn(id);
        existing.setReturnedQuantity(r.getReturnedQuantity());
        existing.setReturnDate(r.getReturnDate());
        existing.setConditionStatus(r.getConditionStatus());
        existing.setRemarks(r.getRemarks());
        return returnRepo.save(existing);
    }

    // =============================
    // STOCK VIEW
    // =============================

    @Override
    public List<StockTransaction> getAllStockTransactions() {
        return txnRepo.findAll();
    }

    @Override
    public StockTransaction getStockTransaction(Long id) {
        return txnRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    @Override
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
        return stockRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock not found"));
    }
}
