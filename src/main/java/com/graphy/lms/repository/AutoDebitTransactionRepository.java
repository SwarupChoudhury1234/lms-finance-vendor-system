package com.graphy.lms.repository;

import com.graphy.lms.entity.AutoDebitTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoDebitTransactionRepository extends JpaRepository<AutoDebitTransaction, Long> {
}