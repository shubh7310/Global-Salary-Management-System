package com.acme.hrms.application.dto;

import com.acme.hrms.domain.model.Country;
import com.acme.hrms.domain.model.Department;

import java.math.BigDecimal;

public record AdjustmentSimulationResponse(
        Country filterCountry,
        Department filterDepartment,
        BigDecimal percentageChange,
        long affectedEmployees,
        BigDecimal currentTotalPayrollUSD,
        BigDecimal projectedTotalPayrollUSD,
        BigDecimal payrollDeltaUSD,
        BigDecimal currentAvgSalaryUSD,
        BigDecimal projectedAvgSalaryUSD
) {
}

