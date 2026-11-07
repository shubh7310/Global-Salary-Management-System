package com.acme.hrms.application.dto;

import com.acme.hrms.domain.model.Currency;

import java.math.BigDecimal;

public record SalaryStructureDto(
        Currency currency,
        BigDecimal baseSalary,
        BigDecimal housingAllowance,
        BigDecimal transportAllowance,
        BigDecimal performanceBonus,
        BigDecimal statutoryDeduction,
        BigDecimal grossAnnualLocal,
        BigDecimal netAnnualLocal,
        BigDecimal monthlyGrossLocal,
        BigDecimal monthlyNetLocal,
        BigDecimal grossAnnualUSD,
        BigDecimal netAnnualUSD
) {
}

