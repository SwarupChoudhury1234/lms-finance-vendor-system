package com.graphy.lms.repository;

import com.graphy.lms.entity.RefundApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefundApprovalRepository extends JpaRepository<RefundApproval, Long> {
}