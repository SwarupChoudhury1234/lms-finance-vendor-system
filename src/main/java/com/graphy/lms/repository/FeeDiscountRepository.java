package com.graphy.lms.repository;

import com.graphy.lms.entity.FeeDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeeDiscountRepository extends JpaRepository<FeeDiscount, Long> {

    /**
     * Satisfies Access Matrix:
     * GET (self/child) ✅ Student, ✅ Parent
     * Allows students to view only their applicable discounts in Postman.
     */
    List<FeeDiscount> findByUserId(Long userId);

    /**
     * Supports fetching all discounts for a specific Fee Structure
     */
    List<FeeDiscount> findByFeeStructureId(Long feeStructureId);
}