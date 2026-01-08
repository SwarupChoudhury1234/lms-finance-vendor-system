package com.graphy.lms.repository;

import com.graphy.lms.entity.StudentFeePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface StudentFeePaymentRepository extends JpaRepository<StudentFeePayment, Long> {
    List<StudentFeePayment> findByStudentFeeAllocationId(Long allocationId);
    List<StudentFeePayment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);
    List<StudentFeePayment> findByCollectedBy(Long collectedBy);
}