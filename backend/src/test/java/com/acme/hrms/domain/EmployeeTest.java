package com.acme.hrms.domain;

import com.acme.hrms.domain.exception.DomainValidationException;
import com.acme.hrms.domain.model.Country;
import com.acme.hrms.domain.model.Currency;
import com.acme.hrms.domain.model.Department;
import com.acme.hrms.domain.model.Employee;
import com.acme.hrms.domain.model.SalaryStructure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmployeeTest {

    @Test
    @DisplayName("Should create valid employee aggregate and update salary")
    void shouldCreateEmployeeAndUpdateSalary() {
        SalaryStructure initialSalary = new SalaryStructure(
                Currency.USD,
                new BigDecimal("120000.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("10000.00"),
                new BigDecimal("30000.00")
        );

        Employee employee = new Employee(
                UUID.randomUUID(),
                "EMP-00101",
                "Alice",
                "Smith",
                "alice.smith@acme.com",
                Department.ENGINEERING,
                "Staff Software Engineer",
                Country.UNITED_STATES,
                LocalDate.of(2023, 1, 15),
                initialSalary,
                new BigDecimal("130000.00"),
                new BigDecimal("100000.00")
        );

        assertThat(employee.getFullName()).isEqualTo("Alice Smith");
        assertThat(employee.getAnnualGrossUSD()).isEqualByComparingTo("130000.00");

        SalaryStructure revised = new SalaryStructure(
                Currency.USD,
                new BigDecimal("135000.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("15000.00"),
                new BigDecimal("35000.00")
        );

        employee.updateSalary(revised, new BigDecimal("150000.00"), new BigDecimal("115000.00"));

        assertThat(employee.getAnnualGrossUSD()).isEqualByComparingTo("150000.00");
        assertThat(employee.getSalaryStructure().calculateGrossAnnual()).isEqualByComparingTo("150000.00");
    }

    @Test
    @DisplayName("Should validate required employee fields")
    void shouldValidateRequiredFields() {
        SalaryStructure salary = new SalaryStructure(Currency.USD, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

        assertThatThrownBy(() -> new Employee(
                UUID.randomUUID(),
                "",
                "Bob",
                "Jones",
                "bob@acme.com",
                Department.SALES,
                "Sales Rep",
                Country.UNITED_STATES,
                LocalDate.now(),
                salary,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        )).isInstanceOf(DomainValidationException.class)
          .hasMessageContaining("Employee code cannot be empty");
    }
}

