package com.graphy.lms.repository;

import com.graphy.lms.entity.Vendors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorsRepository extends JpaRepository<Vendors, Long> {
}