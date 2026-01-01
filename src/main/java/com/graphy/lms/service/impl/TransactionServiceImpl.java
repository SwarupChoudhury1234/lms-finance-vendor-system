package com.graphy.lms.service.impl;

import com.graphy.lms.entity.StockTransaction;
import com.graphy.lms.entity.InventoryItem;
import com.graphy.lms.repository.StockTransactionRepository;
import com.graphy.lms.service.inventory.TransactionService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private StockTransactionRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void recordTransaction(Long itemId, Integer quantity, String type, Integer prevBal, Integer currentBal) {
        StockTransaction tx = new StockTransaction();
        InventoryItem itemReference = entityManager.getReference(InventoryItem.class, itemId);
        tx.setInventoryItem(itemReference);
        tx.setQuantity(quantity);
        tx.setTransactionType(type);
        tx.setPreviousBalance(prevBal);
        tx.setNewBalance(currentBal);
        tx.setTransactionDate(LocalDate.now());
        repository.save(tx);
    }

    @Override
    @Transactional
    public StockTransaction createManualTransaction(StockTransaction transaction) {
        transaction.setTransactionDate(LocalDate.now());
        return repository.save(transaction);
    }

    @Override
    public List<StockTransaction> getHistoryByItem(Long itemId) {
        return repository.findByInventoryItemId(itemId);
    }

    @Override
    @Transactional
    public StockTransaction updateTransaction(Long id, StockTransaction details) {
        // 1. FETCH (Mentor's Pattern)
        StockTransaction existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Update failed: Transaction ID " + id + " not found"));

        // 2. UPDATE fields
        if (details.getTransactionType() != null) existing.setTransactionType(details.getTransactionType());
        if (details.getQuantity() != null) existing.setQuantity(details.getQuantity());
        
        return repository.save(existing);
    }

    @Override
    @Transactional
    public void deleteTransaction(Long id) {
        // FETCH-THEN-DELETE
        StockTransaction existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Delete failed: Transaction ID " + id + " not found"));
        repository.delete(existing);
    }
}