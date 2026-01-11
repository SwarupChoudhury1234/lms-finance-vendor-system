package com.graphy.lms.repository;

import com.graphy.lms.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeeDiscountRepository extends JpaRepository<FeeDiscount, Long> {
    List<FeeDiscount> findByUserId(Long userId);
    List<FeeDiscount> findByFeeStructureId(Long feeStructureId);
    List<FeeDiscount> findByStatus(String status);
    List<FeeDiscount> findByApprovedBy(Long approvedBy);
    
    @Query("SELECT fd FROM FeeDiscount fd WHERE fd.userId = :userId AND fd.feeStructureId = :feeStructureId AND fd.status = 'APPROVED'")
    Optional<FeeDiscount> findApprovedDiscount(
        @Param("userId") Long userId,
        @Param("feeStructureId") Long feeStructureId
    );
}