package com.graphy.lms.repository;

import com.graphy.lms.entity.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {
    // Find history for a specific item (e.g., all history for "Markers")
    List<StockTransaction> findByInventoryItemId(Long itemId);
}