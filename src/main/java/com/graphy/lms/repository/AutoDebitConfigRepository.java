package com.graphy.lms.repository;

import com.graphy.lms.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutoDebitConfigRepository extends JpaRepository<AutoDebitConfig, Long> {
    List<AutoDebitConfig> findByUserId(Long userId);
    Optional<AutoDebitConfig> findByUserIdAndIsActive(Long userId, Boolean isActive);
    List<AutoDebitConfig> findByIsActiveAndConsentGiven(Boolean isActive, Boolean consentGiven);
}