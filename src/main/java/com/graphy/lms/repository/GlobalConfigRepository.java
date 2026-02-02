package com.graphy.lms.repository;

import com.graphy.lms.entity.GlobalConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GlobalConfigRepository extends JpaRepository<GlobalConfig, String> {
    Optional<GlobalConfig> findByConfigKey(String key);
}