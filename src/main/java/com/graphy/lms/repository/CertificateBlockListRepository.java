package com.graphy.lms.repository;

import com.graphy.lms.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateBlockListRepository extends JpaRepository<CertificateBlockList, Long> {
    Optional<CertificateBlockList> findByUserIdAndIsActive(Long userId, Boolean isActive);
    List<CertificateBlockList> findByIsActive(Boolean isActive);
    boolean existsByUserIdAndIsActive(Long userId, Boolean isActive);
}