package com.acme.hrms.application.dto;

import com.acme.hrms.domain.model.Department;

import java.math.BigDecimal;

public record DepartmentAnalyticsResponse(
        Department department,
        String departmentName,
        long headcount,
        BigDecimal totalPayrollUSD,
        BigDecimal avgSalaryUSD,
        BigDecimal minSalaryUSD,
        BigDecimal maxSalaryUSD,
        double payrollPercentage
) {
}

