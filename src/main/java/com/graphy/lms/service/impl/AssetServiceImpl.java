package com.graphy.lms.service.impl;

import com.graphy.lms.entity.AssetsAssigned;
import com.graphy.lms.entity.StockLevel;
import com.graphy.lms.repository.AssetsAssignedRepository;
import com.graphy.lms.service.inventory.AssetService;
import com.graphy.lms.service.inventory.StockService;
import com.graphy.lms.service.inventory.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class AssetServiceImpl implements AssetService {

    @Autowired
    private AssetsAssignedRepository repository;

    @Autowired
    private StockService stockService;

    @Autowired
    private TransactionService transactionService;

    // 1. CREATE - Assign Asset with Stock Reduction & Snapshots
    @Override
    @Transactional
    public AssetsAssigned assignAssetToFaculty(AssetsAssigned assignment) {
        assignment.setGivenDate(LocalDate.now());

        if (assignment.getInventoryItem() != null && assignment.getQuantity() != null) {
            Long itemId = assignment.getInventoryItem().getId();
            Integer qty = assignment.getQuantity();

            // Fetch current stock for Snapshot
            StockLevel currentStock = stockService.getStockByItem(itemId);
            Integer prevBal = currentStock.getAvailableQuantity();

            // Update Stock
            stockService.updateStock(itemId, -qty);

            // Snapshot history
            Integer newBal = prevBal - qty;
            transactionService.recordTransaction(
                itemId, 
                qty, 
                "FACULTY_ASSIGNMENT", 
                prevBal, 
                newBal
            );
        }

        return repository.save(assignment);
    }

    // 2. READ - Filter by Faculty User ID
    @Override
    public List<AssetsAssigned> getFacultyAssets(Long facultyId) {
        return repository.findByUserId(facultyId);
    }

    // 3. UPDATE - Fetch-then-Update Pattern
    @Override
    @Transactional
    public AssetsAssigned updateAssignment(Long id, AssetsAssigned details) {
        // Step A: Fetch current record (Mentor's Pattern)
        AssetsAssigned existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Update failed: Asset Assignment ID " + id + " not found"));

        // Step B: Update specific fields
        if (details.getQuantity() != null) {
            // Note: In a production app, changing quantity here would also 
            // require reversing and re-calculating stock levels.
            existing.setQuantity(details.getQuantity());
        }
        if (details.getGivenDate() != null) {
            existing.setGivenDate(details.getGivenDate());
        }
        if (details.getUserId() != null) {
            existing.setUserId(details.getUserId());
        }

        // Step C: Save updated record
        return repository.save(existing);
    }

    // 4. DELETE - Fetch-then-Delete Pattern
    @Override
    @Transactional
    public void deleteAssignment(Long id) {
        // Step A: Fetch to verify existence (Mentor's Pattern)
        AssetsAssigned existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Delete failed: Asset Assignment ID " + id + " not found"));

        // Step B: Perform Delete
        repository.delete(existing);
    }
}