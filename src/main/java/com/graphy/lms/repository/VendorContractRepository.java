package com.graphy.lms.repository;

import com.graphy.lms.entity.VendorContract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorContractRepository extends JpaRepository<VendorContract, Long> {
    // Standard CRUD operations are enough for now
}