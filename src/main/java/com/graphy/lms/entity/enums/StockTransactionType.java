package com.graphy.lms.entity.enums;

public enum StockTransactionType {

    // ===== INWARD =====
    PURCHASE_INWARD,
    RETURN_GOOD,
    ADJUSTMENT_ADD,

    // ===== OUTWARD =====
    ISSUE_STUDENT,
    ISSUE_FACULTY,
    ISSUE_BATCH,
    CONSUMPTION,
    RESERVE,

    // ===== NEUTRAL / RECOVERY =====
    RELEASE,

    // ===== LOSS / DAMAGE =====
    RETURN_DAMAGED,
    DAMAGE_WRITE_OFF,
    LOST_WRITE_OFF,

    // ===== MANUAL =====
    ADJUSTMENT_SUBTRACT
}
