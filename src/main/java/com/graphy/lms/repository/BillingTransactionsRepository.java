package com.graphy.lms.repository;

import com.graphy.lms.entity.BillingTransactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillingTransactionsRepository extends JpaRepository<BillingTransactions, Long> {
}