package com.graphy.lms.repository;

import com.graphy.lms.entity.AttendancePenaltyRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendancePenaltyRuleRepository extends JpaRepository<AttendancePenaltyRule, Long> {
}