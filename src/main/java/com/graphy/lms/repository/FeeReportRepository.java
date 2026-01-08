package com.graphy.lms.repository;

import com.graphy.lms.entity.FeeReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface FeeReportRepository extends JpaRepository<FeeReport, Long> {
    List<FeeReport> findByReportTypeAndReportDateBetween(String reportType, LocalDate start, LocalDate end);
}