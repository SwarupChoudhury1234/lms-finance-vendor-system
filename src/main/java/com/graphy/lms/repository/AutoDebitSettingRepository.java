package com.graphy.lms.repository;

import com.graphy.lms.entity.AutoDebitSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AutoDebitSettingRepository extends JpaRepository<AutoDebitSetting, Long> {
    Optional<AutoDebitSetting> findByUserId(Long userId);
    List<AutoDebitSetting> findByIsActiveTrue();
}