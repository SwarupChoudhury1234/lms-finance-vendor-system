package com.graphy.lms.repository;

import com.graphy.lms.entity.CertificateBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CertificateBlockRepository extends JpaRepository<CertificateBlock, Long> {
    List<CertificateBlock> findByUserIdAndStatus(Long userId, String status);
    CertificateBlock findByUserIdAndCertificateType(Long userId, String certificateType);
}