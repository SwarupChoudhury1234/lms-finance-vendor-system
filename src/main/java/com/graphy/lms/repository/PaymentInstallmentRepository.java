package com.graphy.lms.repository;

import com.graphy.lms.entity.PaymentInstallment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentInstallmentRepository extends JpaRepository<PaymentInstallment, Long> {
    List<PaymentInstallment> findByStudentFeeAllocationId(Long allocationId);
    List<PaymentInstallment> findByDueDateBeforeAndStatus(LocalDate date, String status);
    List<PaymentInstallment> findByStatus(String status);
}