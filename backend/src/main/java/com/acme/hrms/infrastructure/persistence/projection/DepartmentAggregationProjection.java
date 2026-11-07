package com.acme.hrms.infrastructure.persistence.projection;

import com.acme.hrms.domain.model.Department;

import java.math.BigDecimal;

public interface DepartmentAggregationProjection {
    Department getDepartment();
    Long getHeadcount();
    BigDecimal getTotalPayrollUSD();
    BigDecimal getAvgSalaryUSD();
    BigDecimal getMinSalaryUSD();
    BigDecimal getMaxSalaryUSD();
}

