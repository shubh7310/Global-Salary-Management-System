package com.acme.hrms.domain.model;

import com.acme.hrms.domain.exception.DomainValidationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Core Employee Aggregate Root representing an employee in ACME.
 */
public class Employee {

    private final UUID id;
    private final String employeeCode;
    private String firstName;
    private String lastName;
    private String email;
    private Department department;
    private String jobTitle;
    private Country country;
    private LocalDate hireDate;
    private SalaryStructure salaryStructure;
    private BigDecimal annualGrossUSD;
    private BigDecimal annualNetUSD;

    public Employee(UUID id,
                    String employeeCode,
                    String firstName,
                    String lastName,
                    String email,
                    Department department,
                    String jobTitle,
                    Country country,
                    LocalDate hireDate,
                    SalaryStructure salaryStructure,
                    BigDecimal annualGrossUSD,
                    BigDecimal annualNetUSD) {
        if (employeeCode == null || employeeCode.isBlank()) {
            throw new DomainValidationException("Employee code cannot be empty");
        }
        if (firstName == null || firstName.isBlank()) {
            throw new DomainValidationException("First name cannot be empty");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new DomainValidationException("Last name cannot be empty");
        }
        if (department == null) {
            throw new DomainValidationException("Department cannot be null");
        }
        if (country == null) {
            throw new DomainValidationException("Country cannot be null");
        }
        if (salaryStructure == null) {
            throw new DomainValidationException("Salary structure cannot be null");
        }
        this.id = id != null ? id : UUID.randomUUID();
        this.employeeCode = employeeCode.trim();
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.email = email != null ? email.trim().toLowerCase() : "";
        this.department = department;
        this.jobTitle = jobTitle != null ? jobTitle.trim() : "Staff";
        this.country = country;
        this.hireDate = hireDate != null ? hireDate : LocalDate.now();
        this.salaryStructure = salaryStructure;
        this.annualGrossUSD = annualGrossUSD != null ? annualGrossUSD : BigDecimal.ZERO;
        this.annualNetUSD = annualNetUSD != null ? annualNetUSD : BigDecimal.ZERO;
    }

    /**
     * Updates employee compensation with synchronized USD normalization.
     */
    public void updateSalary(SalaryStructure newSalaryStructure, BigDecimal newGrossUSD, BigDecimal newNetUSD) {
        if (newSalaryStructure == null) {
            throw new DomainValidationException("New salary structure cannot be null");
        }
        this.salaryStructure = newSalaryStructure;
        this.annualGrossUSD = newGrossUSD;
        this.annualNetUSD = newNetUSD;
    }

    public UUID getId() {
        return id;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getEmail() {
        return email;
    }

    public Department getDepartment() {
        return department;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public Country getCountry() {
        return country;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public SalaryStructure getSalaryStructure() {
        return salaryStructure;
    }

    public BigDecimal getAnnualGrossUSD() {
        return annualGrossUSD;
    }

    public BigDecimal getAnnualNetUSD() {
        return annualNetUSD;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(employeeCode, employee.employeeCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeCode);
    }
}

