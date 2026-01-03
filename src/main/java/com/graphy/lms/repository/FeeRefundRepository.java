package com.graphy.lms.repository;

import com.graphy.lms.entity.FeeRefund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository public interface FeeRefundRepository extends JpaRepository<FeeRefund, Long> {}