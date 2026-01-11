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
public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {
    List<FeeStructure> findByCourseIdAndAcademicYear(Long courseId, String academicYear);
    List<FeeStructure> findByBatchId(Long batchId);
    List<FeeStructure> findByFeeTypeId(Long feeTypeId);
    List<FeeStructure> findByAcademicYearAndIsActiveTrue(String academicYear);
    
    @Query("SELECT fs FROM FeeStructure fs WHERE fs.courseId = :courseId AND fs.academicYear = :academicYear AND fs.feeTypeId = :feeTypeId")
    Optional<FeeStructure> findByCourseYearAndFeeType(
        @Param("courseId") Long courseId, 
        @Param("academicYear") String academicYear,
        @Param("feeTypeId") Long feeTypeId
    );
}