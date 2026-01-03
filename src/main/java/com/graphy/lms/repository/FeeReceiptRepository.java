package com.graphy.lms.repository;

import com.graphy.lms.entity.FeeReceipt; // Import the Entity
import org.springframework.data.jpa.repository.JpaRepository; // Fixed: Required for JpaRepository
import org.springframework.stereotype.Repository; // Fixed: Required for @Repository

@Repository
public interface FeeReceiptRepository extends JpaRepository<FeeReceipt, Long> {
}