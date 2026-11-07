package com.acme.hrms.infrastructure.persistence.repository;

import com.acme.hrms.domain.model.Country;
import com.acme.hrms.domain.model.Department;
import com.acme.hrms.infrastructure.persistence.entity.EmployeeJpaEntity;
import com.acme.hrms.infrastructure.persistence.projection.CountryAggregationProjection;
import com.acme.hrms.infrastructure.persistence.projection.DepartmentAggregationProjection;
import com.acme.hrms.infrastructure.persistence.projection.OverviewMetricsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeJpaRepository extends JpaRepository<EmployeeJpaEntity, UUID> {

    Optional<EmployeeJpaEntity> findByEmployeeCode(String employeeCode);

    boolean existsByEmployeeCode(String employeeCode);

    /**
     * Database-level aggregate overview across all 10,000 employees.
     */
    @Query("SELECT COUNT(e) as headcount, " +
           "COALESCE(SUM(e.annualGrossUSD), 0) as totalPayrollUSD, " +
           "COALESCE(AVG(e.annualGrossUSD), 0) as avgSalaryUSD, " +
           "COALESCE(MIN(e.annualGrossUSD), 0) as minSalaryUSD, " +
           "COALESCE(MAX(e.annualGrossUSD), 0) as maxSalaryUSD " +
           "FROM EmployeeJpaEntity e")
    OverviewMetricsProjection getGlobalOverview();

    /**
     * Database-level aggregate overview with optional country and department filters.
     */
    @Query("SELECT COUNT(e) as headcount, " +
           "COALESCE(SUM(e.annualGrossUSD), 0) as totalPayrollUSD, " +
           "COALESCE(AVG(e.annualGrossUSD), 0) as avgSalaryUSD, " +
           "COALESCE(MIN(e.annualGrossUSD), 0) as minSalaryUSD, " +
           "COALESCE(MAX(e.annualGrossUSD), 0) as maxSalaryUSD " +
           "FROM EmployeeJpaEntity e " +
           "WHERE (:country IS NULL OR e.country = :country) " +
           "AND (:department IS NULL OR e.department = :department)")
    OverviewMetricsProjection getFilteredOverview(@Param("country") Country country,
                                                 @Param("department") Department department);

    /**
     * Departmental aggregation for compensation distribution charts.
     */
    @Query("SELECT e.department as department, " +
           "COUNT(e) as headcount, " +
           "SUM(e.annualGrossUSD) as totalPayrollUSD, " +
           "AVG(e.annualGrossUSD) as avgSalaryUSD, " +
           "MIN(e.annualGrossUSD) as minSalaryUSD, " +
           "MAX(e.annualGrossUSD) as maxSalaryUSD " +
           "FROM EmployeeJpaEntity e " +
           "GROUP BY e.department " +
           "ORDER BY totalPayrollUSD DESC")
    List<DepartmentAggregationProjection> getDepartmentAggregations();

    /**
     * Country aggregation for multi-currency regional footprint.
     */
    @Query("SELECT e.country as country, " +
           "e.currency as currency, " +
           "COUNT(e) as headcount, " +
           "SUM(e.annualGrossLocal) as totalPayrollLocal, " +
           "SUM(e.annualGrossUSD) as totalPayrollUSD, " +
           "AVG(e.annualGrossUSD) as avgSalaryUSD, " +
           "MIN(e.annualGrossUSD) as minSalaryUSD, " +
           "MAX(e.annualGrossUSD) as maxSalaryUSD " +
           "FROM EmployeeJpaEntity e " +
           "GROUP BY e.country, e.currency " +
           "ORDER BY totalPayrollUSD DESC")
    List<CountryAggregationProjection> getCountryAggregations();

    /**
     * High performance multi-facet search & filter query over 10,000 records.
     */
    @Query("SELECT e FROM EmployeeJpaEntity e WHERE " +
           "(:country IS NULL OR e.country = :country) AND " +
           "(:department IS NULL OR e.department = :department) AND " +
           "(:minSalary IS NULL OR e.annualGrossUSD >= :minSalary) AND " +
           "(:maxSalary IS NULL OR e.annualGrossUSD <= :maxSalary) AND " +
           "(:query IS NULL OR :query = '' OR " +
           " LOWER(e.employeeCode) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(e.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(e.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(e.jobTitle) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<EmployeeJpaEntity> findByCriteria(@Param("query") String query,
                                          @Param("country") Country country,
                                          @Param("department") Department department,
                                          @Param("minSalary") BigDecimal minSalary,
                                          @Param("maxSalary") BigDecimal maxSalary,
                                          Pageable pageable);

    /**
     * List all employees matching criteria for CSV export.
     */
    @Query("SELECT e FROM EmployeeJpaEntity e WHERE " +
           "(:country IS NULL OR e.country = :country) AND " +
           "(:department IS NULL OR e.department = :department) AND " +
           "(:minSalary IS NULL OR e.annualGrossUSD >= :minSalary) AND " +
           "(:maxSalary IS NULL OR e.annualGrossUSD <= :maxSalary) AND " +
           "(:query IS NULL OR :query = '' OR " +
           " LOWER(e.employeeCode) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(e.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(e.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(e.jobTitle) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<EmployeeJpaEntity> findAllByCriteria(@Param("query") String query,
                                              @Param("country") Country country,
                                              @Param("department") Department department,
                                              @Param("minSalary") BigDecimal minSalary,
                                              @Param("maxSalary") BigDecimal maxSalary);

    /**
     * Fetch all salaries for median calculation.
     */
    @Query("SELECT e.annualGrossUSD FROM EmployeeJpaEntity e " +
           "WHERE (:country IS NULL OR e.country = :country) " +
           "AND (:department IS NULL OR e.department = :department) " +
           "ORDER BY e.annualGrossUSD ASC")
    List<BigDecimal> findAllSalariesUSDForMedian(@Param("country") Country country,
                                                @Param("department") Department department);
}

