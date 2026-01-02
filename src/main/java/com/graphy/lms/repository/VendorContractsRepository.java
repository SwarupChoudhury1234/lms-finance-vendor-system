package com.graphy.lms.repository;

import com.graphy.lms.entity.VendorContracts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorContractsRepository extends JpaRepository<VendorContracts, Long> {
}