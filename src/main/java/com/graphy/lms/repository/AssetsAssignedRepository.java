package com.graphy.lms.repository;

import com.graphy.lms.entity.AssetsAssigned;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AssetsAssignedRepository extends JpaRepository<AssetsAssigned, Long> {
    // Find all items currently held by a specific Faculty member
    List<AssetsAssigned> findByUserId(Long userId);
}