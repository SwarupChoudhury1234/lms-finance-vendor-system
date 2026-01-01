package com.graphy.lms.repository;

import com.graphy.lms.entity.StudentFeePayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentFeePaymentRepository extends JpaRepository<StudentFeePayment, Long> {
}