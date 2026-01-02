package com.graphy.lms.repository;

import com.graphy.lms.entity.AssetsReturn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetsReturnRepository extends JpaRepository<AssetsReturn, Long> {
}