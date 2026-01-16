package com.graphy.lms.repository;

import com.graphy.lms.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, Long> {
    
    @Query("SELECT c FROM CurrencyRate c WHERE c.fromCurrency = :fromCurrency AND c.toCurrency = :toCurrency AND c.effectiveDate <= :date ORDER BY c.effectiveDate DESC LIMIT 1")
    Optional<CurrencyRate> findLatestRate(@Param("fromCurrency") String fromCurrency, 
                                          @Param("toCurrency") String toCurrency, 
                                          @Param("date") LocalDate date);
}