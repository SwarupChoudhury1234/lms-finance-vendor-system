package com.graphy.lms.repository;

import com.graphy.lms.entity.VendorServices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorServicesRepository extends JpaRepository<VendorServices, Long> {
}