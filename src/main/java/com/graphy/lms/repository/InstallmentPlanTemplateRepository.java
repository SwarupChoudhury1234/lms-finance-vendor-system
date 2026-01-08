package com.graphy.lms.repository;

import com.graphy.lms.entity.InstallmentPlanTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstallmentPlanTemplateRepository extends JpaRepository<InstallmentPlanTemplate, Long> {
}