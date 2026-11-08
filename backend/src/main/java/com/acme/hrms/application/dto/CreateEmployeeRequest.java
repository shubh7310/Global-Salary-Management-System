package com.acme.hrms.application.dto;

import com.acme.hrms.domain.model.Country;
import com.acme.hrms.domain.model.Currency;
import com.acme.hrms.domain.model.Department;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateEmployeeRequest(
        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Valid email is required")
        String email,

        @NotNull(message = "Department is required")
        Department department,

        @NotBlank(message = "Job title is required")
        String jobTitle,

        @NotNull(message = "Country is required")
        Country country,

        LocalDate hireDate,

        @NotNull(message = "Currency is required")
        Currency currency,

        @NotNull(message = "Base salary is required")
        @PositiveOrZero(message = "Base salary must be positive or zero")
        BigDecimal baseSalary,

        @NotNull(message = "Housing allowance is required")
        @PositiveOrZero(message = "Housing allowance must be positive or zero")
        BigDecimal housingAllowance,

        @NotNull(message = "Transport allowance is required")
        @PositiveOrZero(message = "Transport allowance must be positive or zero")
        BigDecimal transportAllowance,

        @NotNull(message = "Performance bonus is required")
        @PositiveOrZero(message = "Performance bonus must be positive or zero")
        BigDecimal performanceBonus,

        @NotNull(message = "Statutory deduction is required")
        @PositiveOrZero(message = "Statutory deduction must be positive or zero")
        BigDecimal statutoryDeduction
) {
}
