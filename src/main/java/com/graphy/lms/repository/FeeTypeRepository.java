package com.graphy.lms.repository;

import com.graphy.lms.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeeTypeRepository extends JpaRepository<FeeType, Long> {
    List<FeeType> findByIsActive(Boolean isActive);
    boolean existsByName(String name);
}
