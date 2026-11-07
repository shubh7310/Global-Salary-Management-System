package com.acme.hrms.application.dto;

import com.acme.hrms.domain.model.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record SalaryUpdateRequest(
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
        BigDecimal statutoryDeduction,

        @NotBlank(message = "Reason for salary revision is mandatory for audit trail")
        String reason,

        String modifiedBy
) {
}

