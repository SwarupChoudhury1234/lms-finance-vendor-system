package com.graphy.lms.repository;

import com.graphy.lms.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentAlternativeRepository extends JpaRepository<PaymentAlternative, Long> {
    List<PaymentAlternative> findByIsActive(Boolean isActive);
    List<PaymentAlternative> findByCreatedBy(Long createdBy);
}