package com.graphy.lms.repository;

import com.graphy.lms.entity.VendorTransactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorTransactionsRepository extends JpaRepository<VendorTransactions, Long> {
}