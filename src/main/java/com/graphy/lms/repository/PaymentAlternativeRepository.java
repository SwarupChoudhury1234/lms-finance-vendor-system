package com.graphy.lms.repository;

import com.graphy.lms.entity.PaymentAlternative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaymentAlternativeRepository extends JpaRepository<PaymentAlternative, Long> {
    
    // Find all active payment alternatives
    List<PaymentAlternative> findByIsActiveTrue();
    
    // Find by installment count
    List<PaymentAlternative> findByInstallmentCountAndIsActiveTrue(Integer installmentCount);
    
    // Find by created by (admin)
    List<PaymentAlternative> findByCreatedBy(Long createdBy);
}