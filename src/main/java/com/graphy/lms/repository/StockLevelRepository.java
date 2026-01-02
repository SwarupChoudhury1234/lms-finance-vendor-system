package com.graphy.lms.repository;

import com.graphy.lms.entity.StockLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockLevelRepository extends JpaRepository<StockLevel, Long> {
}