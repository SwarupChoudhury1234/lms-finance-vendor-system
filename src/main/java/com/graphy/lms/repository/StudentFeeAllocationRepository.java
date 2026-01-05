package com.graphy.lms.repository;

import com.graphy.lms.entity.StudentFeeAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudentFeeAllocationRepository extends JpaRepository<StudentFeeAllocation, Long> {

    /**
     * Satisfies Access Matrix:
     * GET (self/child) ✅ Student, ✅ Parent
     * Fetches allocations belonging to the specific student ID.
     */
    List<StudentFeeAllocation> findByUserId(Long userId);

    /**
     * Required for Parent-Child Verification Logic:
     * Used by FeeServiceImpl to verify if a student has any financial 
     * record before allowing a Parent role to access the data.
     */
    boolean existsByUserId(Long userId);

    /**
     * Optional: Check if an allocation exists for a specific student and fee structure
     */
    boolean existsByUserIdAndFeeStructureId(Long userId, Long feeStructureId);
}