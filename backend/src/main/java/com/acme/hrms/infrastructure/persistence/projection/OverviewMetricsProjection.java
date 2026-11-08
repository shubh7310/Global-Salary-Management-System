package com.acme.hrms.infrastructure.persistence.projection;

import java.math.BigDecimal;

public interface OverviewMetricsProjection {
    Long getHeadcount();
    BigDecimal getTotalPayrollLocal();
    BigDecimal getTotalPayrollUSD();
    BigDecimal getAvgSalaryUSD();
    BigDecimal getMinSalaryUSD();
    BigDecimal getMaxSalaryUSD();
}

