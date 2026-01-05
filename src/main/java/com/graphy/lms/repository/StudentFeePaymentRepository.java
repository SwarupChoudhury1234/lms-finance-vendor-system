package com.graphy.lms.repository;

import com.graphy.lms.entity.StudentFeePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudentFeePaymentRepository extends JpaRepository<StudentFeePayment, Long> {

    /**
     * Satisfies Access Matrix:
     * GET (self/child) ✅ Student, ✅ Parent
     * Logic: Navigates Payment -> StudentFeeAllocation -> userId.
     * SQL equivalent: SELECT * FROM payments p 
     * JOIN student_fee_allocations sfa ON p.allocation_id = sfa.id 
     * WHERE sfa.user_id = ?
     */
    List<StudentFeePayment> findByStudentFeeAllocationUserId(Long userId);

    /**
     * Optional: Find all payments for a specific allocation
     */
    List<StudentFeePayment> findByStudentFeeAllocationId(Long allocationId);
}