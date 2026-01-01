package com.graphy.lms.service.inventory;

import com.graphy.lms.entity.StockTransaction;
import java.util.List;

public interface TransactionService {
    // CREATE (Internal & External)
    void recordTransaction(Long itemId, Integer quantity, String type, Integer prevBal, Integer currentBal);
    StockTransaction createManualTransaction(StockTransaction transaction);

    // READ
    List<StockTransaction> getHistoryByItem(Long itemId);

    // UPDATE (Fetch-then-Update Pattern)
    StockTransaction updateTransaction(Long id, StockTransaction details);

    // DELETE (Fetch-then-Delete Pattern)
    void deleteTransaction(Long id);
}