package com.graphy.lms.repository;

import com.graphy.lms.entity.FeeStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {
    List<FeeStructure> findByCourseIdAndAcademicYear(Long courseId, String academicYear);
    List<FeeStructure> findByCourseId(Long courseId);
    List<FeeStructure> findByBatchId(Long batchId);
    List<FeeStructure> findByIsActiveTrue();
    
    // ADD THIS METHOD to fix the error:
    List<FeeStructure> findByAcademicYear(String academicYear);
}