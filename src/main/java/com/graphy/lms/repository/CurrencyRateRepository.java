package com.graphy.lms.repository;

import com.graphy.lms.entity.CurrencyRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, Long> {
    Optional<CurrencyRate> findByBaseCurrencyAndTargetCurrencyAndEffectiveDate(
        String baseCurrency, String targetCurrency, LocalDate effectiveDate);
}