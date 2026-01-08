package com.graphy.lms.repository;

import com.graphy.lms.entity.LateFeeRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LateFeeRuleRepository extends JpaRepository<LateFeeRule, Long> {
}