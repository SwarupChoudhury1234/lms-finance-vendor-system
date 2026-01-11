package com.graphy.lms.repository;

import com.graphy.lms.entity.StockLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockLevelRepository extends JpaRepository<StockLevel, Long> {

    List<StockLevel> findByAvailableQuantityLessThanEqual(Integer threshold);

    java.util.Optional<StockLevel> findByInventoryId(Long inventoryId);
}
