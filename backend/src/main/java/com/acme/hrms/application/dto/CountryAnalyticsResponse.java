package com.acme.hrms.application.dto;

import com.acme.hrms.domain.model.Country;
import com.acme.hrms.domain.model.Currency;

import java.math.BigDecimal;

public record CountryAnalyticsResponse(
        Country country,
        String countryName,
        Currency currency,
        long headcount,
        BigDecimal totalPayrollLocal,
        BigDecimal totalPayrollUSD,
        BigDecimal avgSalaryUSD,
        BigDecimal minSalaryUSD,
        BigDecimal maxSalaryUSD,
        double payrollPercentage
) {
}

