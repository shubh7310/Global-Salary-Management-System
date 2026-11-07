package com.acme.hrms.infrastructure.persistence.entity;

import com.acme.hrms.domain.model.Country;
import com.acme.hrms.domain.model.Currency;
import com.acme.hrms.domain.model.Department;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * JPA Entity representing the employee table in the relational database.
 * Includes composite B-Tree indexes for fast multi-dimensional filtering across 10,000+ records.
 */
@Entity
@Table(
    name = "employees",
    indexes = {
        @Index(name = "idx_emp_country_dept", columnList = "country, department"),
        @Index(name = "idx_emp_gross_usd", columnList = "annual_gross_usd"),
        @Index(name = "idx_emp_code", columnList = "employee_code", unique = true),
        @Index(name = "idx_emp_name", columnList = "last_name, first_name"),
        @Index(name = "idx_emp_dept", columnList = "department"),
        @Index(name = "idx_emp_country", columnList = "country")
    }
)
public class EmployeeJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "employee_code", nullable = false, unique = true, length = 32)
    private String employeeCode;

    @Column(name = "first_name", nullable = false, length = 64)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 64)
    private String lastName;

    @Column(name = "email", nullable = false, length = 128)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "department", nullable = false, length = 32)
    private Department department;

    @Column(name = "job_title", nullable = false, length = 100)
    private String jobTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "country", nullable = false, length = 32)
    private Country country;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, length = 8)
    private Currency currency;

    @Column(name = "base_salary", nullable = false, precision = 15, scale = 2)
    private BigDecimal baseSalary;

    @Column(name = "housing_allowance", nullable = false, precision = 15, scale = 2)
    private BigDecimal housingAllowance;

    @Column(name = "transport_allowance", nullable = false, precision = 15, scale = 2)
    private BigDecimal transportAllowance;

    @Column(name = "performance_bonus", nullable = false, precision = 15, scale = 2)
    private BigDecimal performanceBonus;

    @Column(name = "statutory_deduction", nullable = false, precision = 15, scale = 2)
    private BigDecimal statutoryDeduction;

    @Column(name = "annual_gross_local", nullable = false, precision = 15, scale = 2)
    private BigDecimal annualGrossLocal;

    @Column(name = "annual_net_local", nullable = false, precision = 15, scale = 2)
    private BigDecimal annualNetLocal;

    @Column(name = "annual_gross_usd", nullable = false, precision = 15, scale = 2)
    private BigDecimal annualGrossUSD;

    @Column(name = "annual_net_usd", nullable = false, precision = 15, scale = 2)
    private BigDecimal annualNetUSD;

    public EmployeeJpaEntity() {
    }

    public EmployeeJpaEntity(UUID id,
                             String employeeCode,
                             String firstName,
                             String lastName,
                             String email,
                             Department department,
                             String jobTitle,
                             Country country,
                             LocalDate hireDate,
                             Currency currency,
                             BigDecimal baseSalary,
                             BigDecimal housingAllowance,
                             BigDecimal transportAllowance,
                             BigDecimal performanceBonus,
                             BigDecimal statutoryDeduction,
                             BigDecimal annualGrossLocal,
                             BigDecimal annualNetLocal,
                             BigDecimal annualGrossUSD,
                             BigDecimal annualNetUSD) {
        this.id = id;
        this.employeeCode = employeeCode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.department = department;
        this.jobTitle = jobTitle;
        this.country = country;
        this.hireDate = hireDate;
        this.currency = currency;
        this.baseSalary = baseSalary;
        this.housingAllowance = housingAllowance;
        this.transportAllowance = transportAllowance;
        this.performanceBonus = performanceBonus;
        this.statutoryDeduction = statutoryDeduction;
        this.annualGrossLocal = annualGrossLocal;
        this.annualNetLocal = annualNetLocal;
        this.annualGrossUSD = annualGrossUSD;
        this.annualNetUSD = annualNetUSD;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(BigDecimal baseSalary) {
        this.baseSalary = baseSalary;
    }

    public BigDecimal getHousingAllowance() {
        return housingAllowance;
    }

    public void setHousingAllowance(BigDecimal housingAllowance) {
        this.housingAllowance = housingAllowance;
    }

    public BigDecimal getTransportAllowance() {
        return transportAllowance;
    }

    public void setTransportAllowance(BigDecimal transportAllowance) {
        this.transportAllowance = transportAllowance;
    }

    public BigDecimal getPerformanceBonus() {
        return performanceBonus;
    }

    public void setPerformanceBonus(BigDecimal performanceBonus) {
        this.performanceBonus = performanceBonus;
    }

    public BigDecimal getStatutoryDeduction() {
        return statutoryDeduction;
    }

    public void setStatutoryDeduction(BigDecimal statutoryDeduction) {
        this.statutoryDeduction = statutoryDeduction;
    }

    public BigDecimal getAnnualGrossLocal() {
        return annualGrossLocal;
    }

    public void setAnnualGrossLocal(BigDecimal annualGrossLocal) {
        this.annualGrossLocal = annualGrossLocal;
    }

    public BigDecimal getAnnualNetLocal() {
        return annualNetLocal;
    }

    public void setAnnualNetLocal(BigDecimal annualNetLocal) {
        this.annualNetLocal = annualNetLocal;
    }

    public BigDecimal getAnnualGrossUSD() {
        return annualGrossUSD;
    }

    public void setAnnualGrossUSD(BigDecimal annualGrossUSD) {
        this.annualGrossUSD = annualGrossUSD;
    }

    public BigDecimal getAnnualNetUSD() {
        return annualNetUSD;
    }

    public void setAnnualNetUSD(BigDecimal annualNetUSD) {
        this.annualNetUSD = annualNetUSD;
    }
}

