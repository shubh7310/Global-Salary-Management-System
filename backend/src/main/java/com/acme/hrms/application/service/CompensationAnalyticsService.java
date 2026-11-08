package com.acme.hrms.application.service;

import com.acme.hrms.application.dto.*;
import com.acme.hrms.domain.model.Country;
import com.acme.hrms.domain.model.Currency;
import com.acme.hrms.domain.model.Department;
import com.acme.hrms.infrastructure.persistence.projection.CountryAggregationProjection;
import com.acme.hrms.infrastructure.persistence.projection.DepartmentAggregationProjection;
import com.acme.hrms.infrastructure.persistence.projection.OverviewMetricsProjection;
import com.acme.hrms.infrastructure.persistence.repository.EmployeeJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CompensationAnalyticsService {

    private final EmployeeJpaRepository employeeRepository;

    public CompensationAnalyticsService(EmployeeJpaRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public AnalyticsOverviewResponse getOverview(Country country, Department department) {
        OverviewMetricsProjection metrics = employeeRepository.getFilteredOverview(country, department);
        long count = metrics != null && metrics.getHeadcount() != null ? metrics.getHeadcount() : 0L;
        BigDecimal total = metrics != null && metrics.getTotalPayrollUSD() != null ? metrics.getTotalPayrollUSD() : BigDecimal.ZERO;
        BigDecimal avg = metrics != null && metrics.getAvgSalaryUSD() != null ? metrics.getAvgSalaryUSD().setScale(2, RoundingMode.HALF_EVEN) : BigDecimal.ZERO;
        BigDecimal min = metrics != null && metrics.getMinSalaryUSD() != null ? metrics.getMinSalaryUSD() : BigDecimal.ZERO;
        BigDecimal max = metrics != null && metrics.getMaxSalaryUSD() != null ? metrics.getMaxSalaryUSD() : BigDecimal.ZERO;

        BigDecimal median = calculateMedian(country, department, count);

        return new AnalyticsOverviewResponse(count, total, avg, median, min, max);
    }

    public List<DepartmentAnalyticsResponse> getDepartmentAnalytics() {
        OverviewMetricsProjection totalMetrics = employeeRepository.getGlobalOverview();
        BigDecimal globalTotal = totalMetrics != null && totalMetrics.getTotalPayrollUSD() != null && totalMetrics.getTotalPayrollUSD().compareTo(BigDecimal.ZERO) > 0
                ? totalMetrics.getTotalPayrollUSD()
                : BigDecimal.ONE;

        List<DepartmentAggregationProjection> deptProjections = employeeRepository.getDepartmentAggregations();

        return deptProjections.stream().map(p -> {
            double pct = p.getTotalPayrollUSD().multiply(BigDecimal.valueOf(100))
                    .divide(globalTotal, 2, RoundingMode.HALF_EVEN)
                    .doubleValue();

            return new DepartmentAnalyticsResponse(
                    p.getDepartment(),
                    p.getDepartment().getDisplayName(),
                    p.getHeadcount(),
                    p.getTotalPayrollUSD(),
                    p.getAvgSalaryUSD().setScale(2, RoundingMode.HALF_EVEN),
                    p.getMinSalaryUSD(),
                    p.getMaxSalaryUSD(),
                    pct
            );
        }).toList();
    }

    public List<CountryAnalyticsResponse> getCountryAnalytics() {
        OverviewMetricsProjection totalMetrics = employeeRepository.getGlobalOverview();
        BigDecimal globalTotal = totalMetrics != null && totalMetrics.getTotalPayrollUSD() != null && totalMetrics.getTotalPayrollUSD().compareTo(BigDecimal.ZERO) > 0
                ? totalMetrics.getTotalPayrollUSD()
                : BigDecimal.ONE;

        List<CountryAggregationProjection> countryProjections = employeeRepository.getCountryAggregations();

        return countryProjections.stream().map(p -> {
            double pct = p.getTotalPayrollUSD().multiply(BigDecimal.valueOf(100))
                    .divide(globalTotal, 2, RoundingMode.HALF_EVEN)
                    .doubleValue();

            return new CountryAnalyticsResponse(
                    p.getCountry(),
                    p.getCountry().getDisplayName(),
                    p.getCurrency(),
                    p.getHeadcount(),
                    p.getTotalPayrollLocal(),
                    p.getTotalPayrollUSD(),
                    p.getAvgSalaryUSD().setScale(2, RoundingMode.HALF_EVEN),
                    p.getMinSalaryUSD(),
                    p.getMaxSalaryUSD(),
                    pct
            );
        }).toList();
    }

    public AdjustmentSimulationResponse simulateAdjustment(AdjustmentSimulationRequest request) {
        OverviewMetricsProjection current = employeeRepository.getFilteredOverview(request.country(), request.department());
        long count = current != null && current.getHeadcount() != null ? current.getHeadcount() : 0L;
        BigDecimal currentTotal = current != null && current.getTotalPayrollUSD() != null ? current.getTotalPayrollUSD() : BigDecimal.ZERO;
        BigDecimal currentAvg = current != null && current.getAvgSalaryUSD() != null ? current.getAvgSalaryUSD().setScale(2, RoundingMode.HALF_EVEN) : BigDecimal.ZERO;

        BigDecimal multiplier = BigDecimal.ONE.add(request.percentageChange().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN));
        BigDecimal projectedTotal = currentTotal.multiply(multiplier).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal delta = projectedTotal.subtract(currentTotal);
        BigDecimal projectedAvg = count > 0 ? projectedTotal.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_EVEN) : BigDecimal.ZERO;

        Currency currency = request.country() != null ? request.country().getDefaultCurrency() : com.acme.hrms.domain.model.Currency.USD;
        BigDecimal currentLocal = current != null && current.getTotalPayrollLocal() != null ? current.getTotalPayrollLocal() : currentTotal;
        if (request.country() == null) {
            currentLocal = currentTotal;
        }
        BigDecimal projectedLocal = currentLocal.multiply(multiplier).setScale(currency.getDecimalPlaces(), RoundingMode.HALF_EVEN);
        BigDecimal deltaLocal = projectedLocal.subtract(currentLocal);

        return new AdjustmentSimulationResponse(
                request.country(),
                request.department(),
                request.percentageChange(),
                count,
                currency,
                currentLocal,
                projectedLocal,
                deltaLocal,
                currentTotal,
                projectedTotal,
                delta,
                currentAvg,
                projectedAvg
        );
    }

    private BigDecimal calculateMedian(Country country, Department department, long count) {
        if (count == 0) {
            return BigDecimal.ZERO;
        }
        List<BigDecimal> salaries = employeeRepository.findAllSalariesUSDForMedian(country, department);
        if (salaries.isEmpty()) {
            return BigDecimal.ZERO;
        }
        int size = salaries.size();
        if (size % 2 == 1) {
            return salaries.get(size / 2);
        } else {
            BigDecimal m1 = salaries.get((size / 2) - 1);
            BigDecimal m2 = salaries.get(size / 2);
            return m1.add(m2).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_EVEN);
        }
    }
}

