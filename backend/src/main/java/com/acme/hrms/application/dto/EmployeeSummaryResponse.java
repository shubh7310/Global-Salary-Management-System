package com.acme.hrms.application.dto;

import com.acme.hrms.domain.model.Country;
import com.acme.hrms.domain.model.Currency;
import com.acme.hrms.domain.model.Department;

import java.math.BigDecimal;
import java.util.UUID;

public record EmployeeSummaryResponse(
        UUID id,
        String employeeCode,
        String fullName,
        Department department,
        String jobTitle,
        Country country,
        Currency currency,
        BigDecimal annualGrossLocal,
        BigDecimal annualNetLocal,
        BigDecimal annualGrossUSD,
        BigDecimal annualNetUSD
) {
}

