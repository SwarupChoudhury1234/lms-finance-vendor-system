package com.graphy.lms.repository;

import com.graphy.lms.entity.FeeDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeeDiscountRepository extends JpaRepository<FeeDiscount, Long> {
    // Standard JpaRepository provides save, findById, findAll, deleteById, etc.
}