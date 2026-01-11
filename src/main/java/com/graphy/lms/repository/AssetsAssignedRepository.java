package com.graphy.lms.repository;

import com.graphy.lms.entity.AssetsAssigned;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetsAssignedRepository extends JpaRepository<AssetsAssigned, Long> {
	List<AssetsAssigned> findByUserId(Long userId);
}