package com.acme.hrms.application.dto;

import com.acme.hrms.domain.model.Country;
import com.acme.hrms.domain.model.Currency;
import com.acme.hrms.domain.model.Department;

import java.math.BigDecimal;

public record AdjustmentSimulationResponse(
        Country filterCountry,
        Department filterDepartment,
        BigDecimal percentageChange,
        long affectedEmployees,
        Currency currency,
        BigDecimal currentTotalPayrollLocal,
        BigDecimal projectedTotalPayrollLocal,
        BigDecimal payrollDeltaLocal,
        BigDecimal currentTotalPayrollUSD,
        BigDecimal projectedTotalPayrollUSD,
        BigDecimal payrollDeltaUSD,
        BigDecimal currentAvgSalaryUSD,
        BigDecimal projectedAvgSalaryUSD
) {
}
