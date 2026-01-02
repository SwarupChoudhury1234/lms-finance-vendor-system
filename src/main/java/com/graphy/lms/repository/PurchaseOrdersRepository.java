package com.graphy.lms.repository;

import com.graphy.lms.entity.PurchaseOrders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrdersRepository extends JpaRepository<PurchaseOrders, Long> {
}