package com.graphy.lms.repository;

import com.graphy.lms.entity.StockTransaction;
import com.graphy.lms.entity.enums.ApprovalStatus;
import com.graphy.lms.entity.enums.StockTransactionType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {

    // =========================
    // ITEM-WISE TRANSACTION HISTORY
    // =========================

    List<StockTransaction> findByItemIdOrderByCreatedAtDesc(Long itemId);

    // =========================
    // DASHBOARD & RECENT ACTIVITY
    // =========================

    List<StockTransaction> findTop20ByOrderByCreatedAtDesc();

    // =========================
    // APPROVAL QUEUE (Frontend Approval Screen)
    // =========================

    List<StockTransaction> findByApprovalStatusOrderByCreatedAtDesc(ApprovalStatus approvalStatus);

    // =========================
    // TRANSACTION TYPE FILTERING (Reports / Charts)
    // =========================

    List<StockTransaction> findByTransactionType(StockTransactionType transactionType);

    // =========================
    // LINKED TRANSACTIONS (Issue / Return / Purchase)
    // =========================

    List<StockTransaction> findByReferenceId(Long referenceId);
}
