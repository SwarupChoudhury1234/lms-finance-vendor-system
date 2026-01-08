package com.graphy.lms.repository;

import com.graphy.lms.entity.FeeRefund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeeRefundRepository extends JpaRepository<FeeRefund, Long> {
    List<FeeRefund> findByStatus(String status);
}