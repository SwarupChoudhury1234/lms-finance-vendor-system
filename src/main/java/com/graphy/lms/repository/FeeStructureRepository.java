package com.graphy.lms.repository;

import com.graphy.lms.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {
    List<FeeStructure> findByCourseId(Long courseId);
    List<FeeStructure> findByAcademicYear(String academicYear);
    List<FeeStructure> findByCourseIdAndAcademicYear(Long courseId, String academicYear);
    List<FeeStructure> findByBatchId(Long batchId);
    List<FeeStructure> findByIsActive(Boolean isActive);
}