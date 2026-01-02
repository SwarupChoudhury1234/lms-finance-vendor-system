package com.graphy.lms.repository;

import com.graphy.lms.entity.VendorPayments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorPaymentsRepository extends JpaRepository<VendorPayments, Long> {
}