package com.graphy.lms.repository;

import com.graphy.lms.entity.StudentFeeAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudentFeeAllocationRepository extends JpaRepository<StudentFeeAllocation, Long> {
    List<StudentFeeAllocation> findByUserId(Long userId);
    List<StudentFeeAllocation> findByStatus(String status);
    boolean existsByUserId(Long userId);
}