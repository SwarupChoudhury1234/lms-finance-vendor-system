package com.graphy.lms.repository;

import com.graphy.lms.entity.FeeStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {

    /**
     * Satisfies Access Matrix:
     * GET (by course/year) for Faculty and GET (own) for Students.
     * Filters fee structures based on the Course ID (Java, Python, etc.)
     */
    List<FeeStructure> findByCourseId(Long courseId);

    /**
     * Optional: Supports filtering by Academic Year (e.g., "2025-26")
     */
    List<FeeStructure> findByCourseIdAndAcademicYear(Long courseId, String academicYear);
}