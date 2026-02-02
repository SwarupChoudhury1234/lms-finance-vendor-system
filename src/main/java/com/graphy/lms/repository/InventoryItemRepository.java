package com.graphy.lms.repository;

import com.graphy.lms.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    // Prevent duplicate Item Master creation
    boolean existsBySku(String sku);

    // Future-proofing (audit, reconciliation, lookup)
    Optional<InventoryItem> findBySku(String sku);
}
