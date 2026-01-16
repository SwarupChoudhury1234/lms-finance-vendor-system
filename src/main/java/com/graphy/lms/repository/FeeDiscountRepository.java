package com.graphy.lms.repository;

import com.graphy.lms.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeeDiscountRepository extends JpaRepository<FeeDiscount, Long> {
    List<FeeDiscount> findByUserId(Long userId);
    List<FeeDiscount> findByUserIdAndIsActive(Long userId, Boolean isActive);
    List<FeeDiscount> findByFeeStructureId(Long feeStructureId);
    List<FeeDiscount> findByUserIdAndFeeStructureId(Long userId, Long feeStructureId);
}