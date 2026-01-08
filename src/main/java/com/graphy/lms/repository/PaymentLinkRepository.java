package com.graphy.lms.repository;

import com.graphy.lms.entity.PaymentLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentLinkRepository extends JpaRepository<PaymentLink, Long> {
}