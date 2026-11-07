package com.acme.hrms.infrastructure.persistence.projection;

import com.acme.hrms.domain.model.Country;
import com.acme.hrms.domain.model.Currency;

import java.math.BigDecimal;

public interface CountryAggregationProjection {
    Country getCountry();
    Currency getCurrency();
    Long getHeadcount();
    BigDecimal getTotalPayrollLocal();
    BigDecimal getTotalPayrollUSD();
    BigDecimal getAvgSalaryUSD();
    BigDecimal getMinSalaryUSD();
    BigDecimal getMaxSalaryUSD();
}

