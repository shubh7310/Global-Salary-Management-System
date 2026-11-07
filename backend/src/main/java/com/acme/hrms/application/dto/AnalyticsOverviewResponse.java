package com.acme.hrms.application.dto;

import java.math.BigDecimal;

public record AnalyticsOverviewResponse(
        long totalHeadcount,
        BigDecimal totalPayrollUSD,
        BigDecimal averageSalaryUSD,
        BigDecimal medianSalaryUSD,
        BigDecimal minSalaryUSD,
        BigDecimal maxSalaryUSD
) {
}

