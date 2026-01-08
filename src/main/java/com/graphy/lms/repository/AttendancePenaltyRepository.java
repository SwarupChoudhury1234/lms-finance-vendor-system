package com.graphy.lms.repository;

import com.graphy.lms.entity.AttendancePenalty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AttendancePenaltyRepository extends JpaRepository<AttendancePenalty, Long> {
    List<AttendancePenalty> findByUserId(Long userId);
}