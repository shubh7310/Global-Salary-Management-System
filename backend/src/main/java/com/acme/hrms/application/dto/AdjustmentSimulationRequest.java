package com.acme.hrms.application.dto;

import com.acme.hrms.domain.model.Country;
import com.acme.hrms.domain.model.Department;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AdjustmentSimulationRequest(
        Country country,
        Department department,

        @NotNull(message = "Percentage adjustment is required")
        @DecimalMin(value = "-50.00", message = "Percentage cannot be lower than -50%")
        @DecimalMax(value = "100.00", message = "Percentage cannot exceed 100%")
        BigDecimal percentageChange
) {
}

