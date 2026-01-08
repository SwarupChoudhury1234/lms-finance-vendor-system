package com.graphy.lms.repository;

import com.graphy.lms.entity.ExamFeeMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamFeeMappingRepository extends JpaRepository<ExamFeeMapping, Long> {
}