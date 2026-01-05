package com.graphy.lms.repository;

import com.graphy.lms.entity.FeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeeTypeRepository extends JpaRepository<FeeType, Long> {

    /**
     * Satisfies Access Matrix: 
     * GET (active) ✅ Admin, ✅ Faculty, ✅ Student, ✅ Parent
     * Used by FeeServiceImpl to filter results when role is Student or Parent.
     */
    List<FeeType> findByIsActiveTrue();
}